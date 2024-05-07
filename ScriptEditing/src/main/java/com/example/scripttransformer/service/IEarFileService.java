package com.example.scripttransformer.service;

import com.example.scripttransformer.entity.FilesToken;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public interface IEarFileService {
    String getZip1Path(Long sessionID);

    String getZip2Path(Long sessionID);

    String getUNZip1Path(Long sessionID);
    String getUNZip2Path(Long sessionID);
    String getResultPath(Long sessionID);

    File convert(MultipartFile multipartFile, String path) throws IOException;


    String getUnzipDirectory(Long sessionID);

    String getFormattingDirectoy(Long sessionID);

    String getZippingDirectory(Long sessionID);

    public void initWorkingFolders(String sessionID);
    public void clearWorkingFolder(String sessionID) throws IOException;

    void synchronizeFilesToken(FilesToken fIt);

    public File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException;

    String getExtension(File file);

    FileOutputStream extractFile(ZipInputStream zipIn, String filePath) throws IOException;
   // public void extractEarFile(File earFile, String destination);
    //ZipInputStream unzipFile(MultipartFile file, String destDir) throws IOException;
    void extractEarFile(MultipartFile earFile, String destination) throws IOException;

    ZipInputStream unzipFile(InputStream is, String destDir) throws IOException;

    Set<String> getAllFileNamesFromJarsEars(File file);

    public void decompileOneJar(File jarFile, String targetFilePath , String parent) throws IOException;
    public Long uploadAndExtract2Files(MultipartFile file1,MultipartFile file2) throws IOException ;


    public void decompileManyJars(String sourceFolder, String targetFolder) throws IOException;
    public void decompileOneClass(File classFile,String targetFolder) throws IOException;

    void zipFolder(String sourceFolder, ZipOutputStream zos) throws IOException;
}
