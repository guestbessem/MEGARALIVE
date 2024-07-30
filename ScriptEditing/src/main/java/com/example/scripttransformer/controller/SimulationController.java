package com.example.scripttransformer.controller;


import com.example.scripttransformer.entity.ParametreSimulation;
import com.example.scripttransformer.entity.ResultatSimulation;
import com.example.scripttransformer.repository.ParamSimulationRepo;
import com.example.scripttransformer.repository.ResultatSimulationRepo;
import com.example.scripttransformer.service.FileService;
import com.example.scripttransformer.service.ScriptSimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@CrossOrigin(origins = { "*" })
@RestController
@RequestMapping("/Simulation")
public class SimulationController {
    @Autowired
    ScriptSimulationService ss;
    @Autowired
    FileService fs;
    @Autowired
    ResultatSimulationRepo rr;
    @Autowired
    ParamSimulationRepo pas;
    @PostMapping("/simulate")
    public ResultatSimulation doSimulation(@RequestParam("file")MultipartFile file, @RequestParam("driver") String driver,
                                           @RequestParam("url") String url, @RequestParam("user") String user,
                                           @RequestParam("pwd") String pwd) throws IOException {

        ResultatSimulation rs=new ResultatSimulation();
        ParametreSimulation ps=new ParametreSimulation(url, "", driver, user,  pwd);
        rs.setPs(ps);
        rs.setScript(fs.convertMultipartFileToFile(file));//changement de script
        rs=ss.simulate(rs);
        pas.save(ps);//enregistrement des paramétres dans la base
        rr.save(rs);//enregistrement du resultat dans la base
        return rs;
    }

    @GetMapping("/all")
    public ArrayList<ResultatSimulation> getAll(){
        return (ArrayList<ResultatSimulation>) rr.findAll();
    }
    @PostMapping("/showFile")
    public ResponseEntity<InputStreamResource> findFileContent(@RequestParam("id") Long id) throws FileNotFoundException {
        Optional<ResultatSimulation> re = rr.findById(id);
        if (re.isPresent()) {
            ResultatSimulation result = re.get();
            File filei = result.getScript();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+filei.getName());
            InputStreamResource resource = new InputStreamResource(new FileInputStream(filei));
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(filei.length())
                    .body(resource);
        }
        return null;
    }

    @PostMapping("/validate")
    public ResultatSimulation validate(@RequestParam("id")  Long id){
        Optional<ResultatSimulation> re=rr.findById(id);
        if(re.isPresent()){
            ResultatSimulation result= re.get();
            result.setStatus(ResultatSimulation.Status.VALIDATED);
            rr.save(result);
        }
        return re.get();
    }
    @PostMapping("/reject")
    public ResultatSimulation reject(@RequestParam("id") Long id){
        Optional<ResultatSimulation> re=rr.findById(id);
        if(re.isPresent()){
            ResultatSimulation result= re.get();
            result.setStatus(ResultatSimulation.Status.REJECTED);
            rr.save(result);
        }
        return re.get();
    }
}
