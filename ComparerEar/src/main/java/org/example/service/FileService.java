package org.example.service;


import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.benf.cfr.reader.api.CfrDriver;
import org.example.entity.FilesToken;
import org.example.repository.FileTokenJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


import static org.example.util.MegaGoLiveConstants.*;


@Service
public class FileService implements IEarFileService{
    private MultipartFile file;

    private String destDir;
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    FileTokenJPA ft;
    @Autowired
    EntityManager em;

    @Override
    public String getZip1Path(Long sessionID){
        return em.getExtractionPath()+File.separator+sessionID+File.separator+ZIP1;
    }
    @Override
    public String getZip2Path(Long sessionID){
        return em.getExtractionPath()+File.separator+sessionID+File.separator+ZIP2;
    }
    @Override
    public String getResultPath(Long sessionID){
        return em.getExtractionPath()+File.separator+sessionID+File.separator+RESULT;
    }

    @Override
    public File convert(MultipartFile multipartFile, String path) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        FileCopyUtils.copy(multipartFile.getBytes(),file);
        //copyInputStreamToFile(multipartFile.getInputStream(), file);
        return file;
    }

    @Override
    public String getUNZip2Path(Long sessionID){
        return em.getExtractionPath()+File.separator+sessionID+File.separator+UNZIP2;
    }
    @Override
    public String getUNZip1Path(Long sessionID){
        return em.getExtractionPath()+File.separator+sessionID+File.separator+UNZIP1;
    }

    @Override
    public String getUnzipDirectory(Long sessionID){
        return em.getExtractionPath()+File.separator+sessionID+File.separator+UNZIP;
    }
    @Override
    public String getFormattingDirectoy(Long sessionID){
        return em.getExtractionPath()+File.separator+sessionID+File.separator+FORMATED;
    }
    @Override
    public String getZippingDirectory(Long sessionID){
        return em.getExtractionPath()+File.separator+sessionID+File.separator+ZIPPED;

    }
    @Override
    public void initWorkingFolders(String sessionID) {
      String path=em.getExtractionPath()+File.separator+sessionID;
      File file=new File(path);
      if(!file.exists()){
          file.mkdir();
      }
    }

    @Override
    public void clearWorkingFolder(String sessionID) throws IOException {
        String path=em.getExtractionPath()+File.separator+sessionID;
        FileUtils.deleteDirectory(new File(path));
    }



    @Override
    public void synchronizeFilesToken(FilesToken fIt){
     ft.save(fIt);
    }
    @Override
    public File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        //Multipart: fichier télécharger d'une requete http
        File file = new File(multipartFile.getOriginalFilename());
        FileCopyUtils.copy(multipartFile.getBytes(),file);
        //copyInputStreamToFile(multipartFile.getInputStream(), file);
        return file;
    }
    @Override
    public String getExtension(File file) {
        String fileName = file.getName();
        Optional<String> oFileExtension1 = Optional.ofNullable(fileName).filter(f -> f.contains(".")).map(f -> f.substring(fileName.lastIndexOf(".")));
        if (oFileExtension1.isPresent()) {
            return oFileExtension1.get();
        }

        return ".txt";
    }
    @Override
    public FileOutputStream extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        byte[] buffer = new byte[4096];

        // Use try-with-resources to ensure FileOutputStream is closed properly
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            int bytesRead;
            while ((bytesRead = zipIn.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        } // FileOutputStream is automatically closed here
        return new FileOutputStream(filePath);
    }
    @Override
    public void extractEarFile(MultipartFile earFile, String destination) throws IOException {
        InputStream inputStream1 = earFile.getInputStream();
        // Créez un fichier temporaire pour stocker le contenu du fichier
        unzipFile(inputStream1,destination);
    }
    private Boolean forceFolderCreation(File file){
        File child=file;
        File parent=file.getParentFile();
        while(!child.exists() && !parent.exists()){
            child=parent;
            parent=parent.getParentFile();
            forceFolderCreation(parent);
            parent.mkdirs();
            child.mkdirs();
        }
        return true;
    }
    public void writeFile(InputStream zipIn, String filePath) throws IOException {
        byte[] buffer = new byte[4096];
        File parentDir = new File(filePath.substring(0, filePath.lastIndexOf(File.separator)));
        File newFile = new File(filePath);

        // Ensure the parent directory exists
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        // Use try-with-resources to ensure FileOutputStream is closed properly
        try (FileOutputStream fos = new FileOutputStream(newFile)) {
            int bytesRead;
            while ((bytesRead = zipIn.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        } // FileOutputStream is automatically closed here
    }


    @Override
    public ZipInputStream unzipFile(InputStream is, String destDir) throws IOException {
        File file=new File(destDir);
        if(!file.exists()) file.mkdir();
        ZipInputStream zipIn = new ZipInputStream(is);
        ZipEntry entry = zipIn.getNextEntry();
        while (entry != null) {
            String filepath = destDir + File.separator + entry.getName();//fil
            if (!entry.isDirectory() ){
                writeFile(zipIn, filepath.replace("/",File.separator).replace("\\",File.separator));
            } else {
                File dir = new File(filepath);
                dir.mkdirs();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        return zipIn;

    }
    @Override
    public Set<String> getAllFileNamesFromJarsEars(File file){
          return (file.isDirectory())?new HashSet(List.of(file.listFiles())):new HashSet<>();
    }
    @Override
    public Long uploadAndExtract2Files(MultipartFile file1,MultipartFile file2) throws IOException {
        FilesToken ft=new FilesToken();
        synchronizeFilesToken(ft);
        String path=em.getExtractionPath();
        String file1P=path+File.separator+ ft.getToken();
        String file2P=path+File.separator+ ft.getToken();
        extractEarFile(file1,file1P+File.separator+ ZIP1);
        decompileManyJars(file1P+File.separator+ZIP1,file1P+File.separator+UNZIP1);
        extractEarFile(file2,file2P+File.separator+ZIP2);
        decompileManyJars(file2P+File.separator+ZIP2,file2P+File.separator+UNZIP2);
        return ft.getToken();
    }
    @Override

    public void decompileOneJar(File jarFile, String targetFolder, String targetParent) throws IOException {
        if (getExtension(jarFile).equals(".jar")) {
            unzipFile(new FileInputStream(jarFile), targetFolder);
        }

        File folder = new File(targetFolder);
        if (folder.isDirectory()) {
            processFilesInDirectory(folder, targetFolder, targetParent);
        } else {
            processFile(folder, targetFolder, targetParent);
        }
    }

    private void processFilesInDirectory(File directory, String targetFolder, String targetParent) throws IOException {
        for (File file : directory.listFiles()) {
            processFile(file, targetFolder, targetParent);
        }
    }

    private void processFile(File file, String targetFolder, String targetParent) throws IOException {
        String extension = getExtension(file);

        if (file.isDirectory()) {
            decompileOneJar(file, targetFolder + File.separator + file.getName(), targetParent);
        } else {
            switch (extension) {
                case ".jar":
                case ".war":
                    decompileOneJar(file, targetFolder + File.separator + file.getName(), targetParent);
                    break;
                case ".class":
                    decompileOneClass(file, targetParent);
                    Files.delete(file.toPath());
                    break;
                default:
                    Files.copy(file.toPath(), new File(targetFolder + File.separator + file.getName()).toPath());
                    Files.delete(file.toPath());
                    break;
            }
        }
    }
    @Override
    public void decompileManyJars(String sourceFolder, String targetFolder) throws IOException {
        File sourceFile = new File(sourceFolder);

        // Check if the sourceFolder is a directory and is not null
        if (sourceFile.isDirectory() && sourceFile.listFiles() != null) {
            File[] files = sourceFile.listFiles();

            for (File file : files) {
                if (getExtension(file).equals(".jar")) {
                    decompileOneJar(file, targetFolder + File.separator + file.getName(), targetFolder + File.separator + file.getName());
                    // Files.delete(Path.of(file.toURI()));
                }
            }
        } else {
            System.out.println("Source folder is not a directory or is empty.");
            // Handle the case where sourceFolder is not a directory or is empty.
        }
    }

    @Override
    public void decompileOneClass(File fileToDecompile,String targetFolder) throws IOException {
        // Vérifier si le fichier existe
        if (!fileToDecompile.exists()) {
            throw new IllegalArgumentException("Le fichier spécifié n'existe pas : " + fileToDecompile.getAbsolutePath());
        }
        //    FileOutputStream fos=new FileOutputStream(targetFile);
        Map<String, String> options = new HashMap<>();
        options.put("outputdir", targetFolder);
        CfrDriver driver = new CfrDriver.Builder()
                .withOptions(options)
                .build();
        // Décompiler le fichier
        driver.analyse(Arrays.asList(fileToDecompile.getAbsolutePath()));
    }


    @Override
    public void zipFolder(String sourceFolder, ZipOutputStream zos) throws IOException {
        File folder = new File(sourceFolder);
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                // Recursively zip sub-folders
                zipFolder(file.getAbsolutePath(), zos);
            } else {
                byte[] buffer = new byte[1024];
                FileInputStream fis = new FileInputStream(file);


                // Add zip entry for the file
                zos.putNextEntry(new ZipEntry(file.getName()));

                int length;
                while ((length = fis.read(buffer)) > 0) {
                    // Write the file content to the zip output stream
                    zos.write(buffer, 0, length);
                }

                fis.close();
            }
        }
    }


    /**
     * This method populates all the files in a directory to a List
     * @param dir
     * @throws IOException
     */
    private static ArrayList<String> populateFilesList(File dir) throws IOException {
        ArrayList<String> filesListInDir=new ArrayList<String>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) filesListInDir.add(file.getAbsolutePath());
                else filesListInDir.addAll(populateFilesList(file));
            }
        }
        return  filesListInDir;
    }

    /**
     * This method compresses the single file to zip format
     * @param file
     * @param zipFileName
     */
    private void zipSingleFile(File file, String zipFileName) {
        try (FileOutputStream fos = new FileOutputStream(zipFileName);
             ZipOutputStream zos = new ZipOutputStream(fos);
             FileInputStream fis = new FileInputStream(file)) {

            // Add a new Zip Entry to the ZipOutputStream
            ZipEntry ze = new ZipEntry(file.getName());
            zos.putNextEntry(ze);

            // Read the file and write to ZipOutputStream
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }

            // Close the zip entry to write to the zip file
            zos.closeEntry();
            System.out.println(file.getCanonicalPath() + " is zipped to " + zipFileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
