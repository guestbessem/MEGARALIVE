package org.example.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFiles {

    private static final Logger logger = Logger.getLogger(ZipFiles.class.getName());
    private List<String> filesListInDir = new ArrayList<>();

    /**
     * This method zips the directory.
     * @param dir The directory to zip.
     * @param zipDirName The name of the resulting zip file.
     */
    public void zipDirectory(File dir, String zipDirName) {
        try {
            populateFilesList(dir);

            // Create ZipOutputStream to write to the zip file
            try (FileOutputStream fos = new FileOutputStream(zipDirName);
                 ZipOutputStream zos = new ZipOutputStream(fos)) {

                for (String filePath : filesListInDir) {
                    logger.info("Zipping " + filePath);
                    // For ZipEntry we need to keep only relative file path, so we used substring on absolute path
                    ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length() + 1));
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
        } catch (IOException e) {
            logger.severe("An error occurred while zipping the directory: " + e.getMessage());
        }
    }

    /**
     * This method populates all the files in a directory to a List.
     * @param dir The directory to scan.
     * @throws IOException
     */
    private void populateFilesList(File dir) throws IOException {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    filesListInDir.add(file.getAbsolutePath());
                } else if (file.isDirectory()) {
                    populateFilesList(file);
                }
            }
        }
    }

    /**
     * This method compresses a single file to zip format.
     * @param file The file to zip.
     * @param zipFileName The name of the resulting zip file.
     */
    public static void zipSingleFile(File file, String zipFileName) {
        try {
            // Create ZipOutputStream to write to the zip file
            try (FileOutputStream fos = new FileOutputStream(zipFileName);
                 ZipOutputStream zos = new ZipOutputStream(fos)) {

                // Add a new Zip Entry to the ZipOutputStream
                ZipEntry ze = new ZipEntry(file.getName());
                zos.putNextEntry(ze);

                // Read the file and write to ZipOutputStream
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                }

                // Close the zip entry to write to zip file
                zos.closeEntry();
            }

            logger.info(file.getCanonicalPath() + " is zipped to " + zipFileName);
        } catch (IOException e) {
            logger.severe("An error occurred while zipping the file: " + e.getMessage());
        }
    }
}
