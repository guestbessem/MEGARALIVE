package org.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFolder {
    public  void main2(String[] args) {
        String sourceFolder = "path/to/source/folder";
        String zipFile = "path/to/destination/archive.zip";

        try {
            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);

            zipFolder(sourceFolder, zos);

            zos.close();
            fos.close();

            System.out.println("Folder successfully zipped!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void zipFolder(String sourceFolder, ZipOutputStream zos) throws IOException {
        File folder = new File(sourceFolder);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IOException("Source folder does not exist or is not a directory.");
        }

        zipFolderRecursive(folder, folder.getName(), zos);
    }

    private static void zipFolderRecursive(File folder, String basePath, ZipOutputStream zos) throws IOException {
        File[] files = folder.listFiles();

        if (files == null) {
            return; // If no files, exit early
        }

        for (File file : files) {
            String zipEntryName = basePath + "/" + file.getName();

            if (file.isDirectory()) {
                // Recursively zip sub-folders, adding a trailing slash to the entry name
                zos.putNextEntry(new ZipEntry(zipEntryName + "/"));
                zos.closeEntry();
                zipFolderRecursive(file, zipEntryName, zos);
            } else {
                byte[] buffer = new byte[1024];

                try (FileInputStream fis = new FileInputStream(file)) {
                    // Add zip entry for the file
                    zos.putNextEntry(new ZipEntry(zipEntryName));

                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        // Write the file content to the zip output stream
                        zos.write(buffer, 0, length);
                    }

                    zos.closeEntry();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }
    }

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
    public static void zipDirectory(File dir, String zipDirName) throws IOException {
        ArrayList<String> filesListInDir=populateFilesList(dir);
        //now zip files one by one
        //create ZipOutputStream to write to the zip file
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipDirName))) {
            for (String filePath : filesListInDir) {
                System.out.println("Zipping " + filePath);
                // For ZipEntry we need to keep only relative file path, so we used substring on absolute path
                ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length() + 1, filePath.length()));
                zos.putNextEntry(ze);

                // Read the file and write to ZipOutputStream
                try (FileInputStream fis = new FileInputStream(filePath)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                }

                zos.closeEntry();
            }
        }
    }


    public static final void main(String[] args) throws IOException {
        String sourceFolder = "C:\\work\\ExtractPat\\2802\\FormattedScript";
        String zipFile = "C:\\work\\ExtractPat\\2802\\ZippedScript\\Result.zip";
        try {
            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);

            zipFolder(sourceFolder, zos);

            zos.close();
            fos.close();

            System.out.println("Folder successfully zipped!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 }
