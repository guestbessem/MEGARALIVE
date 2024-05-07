package com.example.scripttransformer.controller;


import com.example.scripttransformer.entity.STransformationParameters;
import com.example.scripttransformer.entity.ScriptTransformation;
import com.example.scripttransformer.service.FileService;
import com.example.scripttransformer.service.ScriptTransformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@CrossOrigin(origins = { "*" })
@RestController
@RequestMapping("/ScriptTransformer")
public class ScriptTransformerController {

    @Autowired
    FileService es;
    @Autowired
    ScriptTransformationService st;

    @PostMapping( value="/result")
    public ResponseEntity<InputStreamResource> getResult(@RequestParam("file") MultipartFile file, @RequestParam("dbName") String dbName, @RequestParam("url") String url, @RequestParam("port") String port, @RequestParam("user1") String user1,
                                                         @RequestParam("user2") String user2,
                                                         @RequestParam("source_File_Name_User_1") String source_File_Name_User_1,
                                                         @RequestParam("source_File_Name_User_2") String source_File_Name_User_2,
                                                         @RequestParam("file0_regex_User_1") String file0_regex_User_1,
                                                         @RequestParam("file1_regex_User_1") String file1_regex_User_1,
                                                         @RequestParam("file0_regex_User_2") String file0_regex_User_2,
                                                         @RequestParam("file1_regex_User_2") String file1_regex_User_2,
                                                         @RequestParam("user1_role") String user1_role,
                                                         @RequestParam("user2_role") String user2_role) throws IOException {
        ScriptTransformation transformationHistory=new ScriptTransformation();
        st.save(transformationHistory);

        //identifiant unique pour générer le fichier de unzip et result
        Long sessionId=transformationHistory.getId();
        transformationHistory.setTempLocation(es.getUnzipDirectory(sessionId));
        transformationHistory.setSparams(new STransformationParameters( user1_role,user2_role,dbName,  url,  port,  user1,  user2,  source_File_Name_User_1,  source_File_Name_User_2,  file0_regex_User_1,  file1_regex_User_1,  file0_regex_User_2,  file1_regex_User_2) );
        File filei = new File(es.getZippingDirectory(sessionId)+File.separator+"file.zip");
        st.transform(file.getInputStream(),transformationHistory);
        HttpHeaders headers = new HttpHeaders();//définition l'entete de la requete http de la reponse
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=file.zip");//piéce jointe
        InputStreamResource resource = new InputStreamResource(new FileInputStream(filei));
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)//
                .contentLength(filei.length())
                .body(resource);


    }



}
