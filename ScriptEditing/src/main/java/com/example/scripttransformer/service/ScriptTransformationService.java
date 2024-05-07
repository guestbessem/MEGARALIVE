package com.example.scripttransformer.service;

import com.example.scripttransformer.entity.STransformationParameters;
import com.example.scripttransformer.entity.ScriptTransformation;
import com.example.scripttransformer.repository.ScriptTransfRepository;
import com.example.scripttransformer.util.ScriptGenContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.zip.ZipOutputStream;

@Service
public class ScriptTransformationService {
    public static final String separator = System.lineSeparator();


    @Autowired
    FileService fs;

    @Autowired
    ScriptTransfRepository r;

    public ScriptTransformation save(ScriptTransformation str){
        return   r.save(str);
    }
    public FileOutputStream transform(InputStream is, ScriptTransformation st) throws IOException {
        STransformationParameters parameters=st.getSparams();
        String unzipSourceDir=fs.getUnzipDirectory(st.getId());//path vers le fichier source décompresser
        String zipFormattedScripts=fs.getZippingDirectory(st.getId());//path vers le dossier formaté et zippé
        String FormattedScripts=fs.getFormattingDirectoy(st.getId());//path vers le dossier formaté
        String datasource1=unzipSourceDir+ File.separator+parameters.getSource_File_Name_User_1();//le path vers le fichier1
        String datasource2=unzipSourceDir+File.separator+parameters.getSource_File_Name_User_2();//le path vers le fichier2

        //Extraire le fichier zip source
        fs.unzipFile(is,unzipSourceDir);

        generateScripts(
                st.getSparams().getUser1_role(),               // Rôle de l'utilisateur 1
                st.getSparams().getUser2_role(),               // Rôle de l'utilisateur 2
                st.getSparams().getSource_File_Name_User_1(),  // Nom du fichier source de l'utilisateur 1
                st.getSparams().getSource_File_Name_User_2(),  // Nom du fichier source de l'utilisateur 2
                new File(datasource1),                      // Chemin vers le dossier de la source de données
                parameters.getFile0_regex_User_1(),            // Expression régulière pour le fichier 0 de l'utilisateur 1
                parameters.getFile1_regex_User_1(),            // Expression régulière pour le fichier 1 de l'utilisateur 1
                new File(datasource2),                          // Chemin vers le dossier d'historique
                parameters.getFile0_regex_User_2(),            // Expression régulière pour le fichier 0 de l'utilisateur 2
                parameters.getFile1_regex_User_2(),            // Expression régulière pour le fichier 1 de l'utilisateur 2
                FormattedScripts                                      // Dossier de sortie
        );
        //Générer le fichier zip formaté
        File scriptZIP=new File(zipFormattedScripts);
        scriptZIP.mkdirs();
        try {
            FileOutputStream fos = new FileOutputStream(zipFormattedScripts+File.separator+"file.zip");
            ZipOutputStream zos = new ZipOutputStream(fos);

            fs.zipFolder(FormattedScripts, zos);

            zos.close();
            fos.close();
            return fos;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
        //  zos.close();
    }

    public static File getFile(String path){
        return new File(path);
    }
    public static void generateScripts(String user1_role, String user2_role,
                                       String user1, String user2,
                                       File dataSource, String pattern0,
                                       String pattern1, File historyDataSource,
                                       String pattern10, String pattern11,
                                       String outputDir) {
        if (dataSource != null && historyDataSource != null) {
            // Création du dossier de sortie
            File outputFolder = new File(outputDir);
            outputFolder.mkdir();

            // Génération des scripts à partir des données sources
            generate(dataSource, 0, outputDir, pattern0);
            generate(dataSource, 1, outputDir, pattern1);
            generate(historyDataSource, 10, outputDir, pattern10);
            generate(historyDataSource, 11, outputDir, pattern11);

            // Génération des scripts odds pour les utilisateurs
            boolean test_02_03 = generateOdds(user1_role, dataSource, outputDir, "02.sql", "03.sql");
            boolean test_12_13 = generateOdds(user2_role, historyDataSource, outputDir, "12.sql", "13.sql");

            // Configuration des indicateurs de contexte
            setContextBooleans(test_02_03, test_12_13);

            // Génération des scripts supplémentaires
            generateExtra("lanzador_" + user1_role + ".sql", outputDir, "cmp");
            generateExtra("lanzador_" + user2_role + ".sql", outputDir, "audit");
            generateExtra("lanzador_system.sql", outputDir, "system");

            // Copie des fichiers vers un sous-dossier spécifique
            // copyFiles(outputFolder, outputDir + File.separator + "marchaatras");
        }
    }
    private static void copyFiles(File outputFolder, String toFolder) {
        File[] files = outputFolder.listFiles();
        File to = new File(toFolder);
        to.mkdir();
        try {
            for (int i = 0; i < files.length; i++) {
                byte[] bytes = Files.readAllBytes(Paths.get(files[i]
                        .getAbsolutePath()));
                File newFile = new File(toFolder + File.separator
                        + files[i].getName());
                newFile.createNewFile();
                BufferedWriter br = new BufferedWriter(new FileWriter(newFile));
                br.write(new String(bytes));
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generate(File dataSource, int fileNumber, String parentPath, String pattern) {
        try {
            String outputFileName = "";
            switch (fileNumber) {
                case 0:
                    outputFileName = "00.sql";
                    break;
                case 1:
                    outputFileName = "01.sql";
                    break;
                case 10:
                    outputFileName = "10.sql";
                    break;
                case 11:
                    outputFileName = "11.sql";
                    break;
                default:
                    break;
            }

            // Création du fichier de sortie à partir du chemin parent et outputfilename
            File output = new File(parentPath + File.separator + outputFileName);
            output.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(output, false));

            // Lecture du fichier source ligne par ligne
            Scanner scanner = new Scanner(dataSource);
            scanner.useDelimiter("\\n");// "\n" caratére de saut de ligne
            while (scanner.hasNext()) {
                String statement = scanner.next();
                // Remplacement du motif par un espace " "
                String content = statement.replaceAll(pattern, ScriptGenContext.replaceTo);
                content = content.trim();
                if (!(content.length() == 0)) {
                    // Écriture du contenu dans le fichier de sortie, suivi d'une nouvelle ligne
                    writer.write(content + System.lineSeparator());
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean generateOdds(String roleName,File historyDataSource,
                                        String parentPath, String grantStatementsFile,
                                        String createStatementsFile) {
        if (historyDataSource != null) {
            boolean returnedValue = false;
            try {
                byte[] bytes = Files.readAllBytes(Paths.get(historyDataSource
                        .getAbsolutePath()));
                String content = new String(bytes);
                ScriptGenContext.setRoleName(roleName);
                if (content.contains("create")) {
                    returnedValue = true;
                    File output_1 = new File(parentPath + File.separator
                            + grantStatementsFile);
                    File output_2 = new File(parentPath + File.separator
                            + createStatementsFile);
                    output_1.createNewFile();
                    output_2.createNewFile();
                    BufferedWriter write_1 = new BufferedWriter(new FileWriter(
                            output_1, false));
                    BufferedWriter write_2 = new BufferedWriter(new FileWriter(
                            output_2, false));
                    Scanner scanner = new Scanner(content);
                    scanner.useDelimiter("\\n");
                    while (scanner.hasNext()) {
                        String statement = scanner.next();
                        String tableName = "";
                        String newStatement_1 = "";
                        String newStatement_2 = "";
                        if (statement.toUpperCase().startsWith("CREATE")) {

                            if (statement.toUpperCase().startsWith("CREATE TABLE")) {
                                tableName = statement.substring(
                                        statement.toUpperCase().indexOf("TABLE") + 5,
                                        statement.indexOf("(") - 1).trim();
                                newStatement_1 = ScriptGenContext.grantOnTables
                                        .replace("$1", tableName);
                                newStatement_2 = ScriptGenContext.createSynonym
                                        .replace("$1", tableName);
                            }
                            if (statement.toUpperCase().contains("VIEW")) {
                                tableName = statement.substring(
                                        statement.toUpperCase().indexOf("VIEW") + 4,
                                        statement.indexOf("(") - 1).trim();
                                newStatement_1 = ScriptGenContext.grantOnViews
                                        .replace("$1", tableName);
                                newStatement_2 = ScriptGenContext.createSynonym
                                        .replaceAll("$1", tableName);
                            }
                            if (statement.toUpperCase().contains("FUNCTION")) {
                                tableName = statement.substring(
                                        statement.toUpperCase().indexOf("FUNCTION") + 8,
                                        statement.indexOf("(") - 1).trim();
                                newStatement_2 = ScriptGenContext.createSynonym
                                        .replaceAll("$1", tableName);
                            }
                            if (statement.toUpperCase().contains("PROCEDURE")) {
                                tableName = statement.substring(
                                        statement.toUpperCase().indexOf("PROCEDURE") + 9,
                                        statement.indexOf("(") - 1).trim();
                                newStatement_2 = ScriptGenContext.createSynonym
                                        .replaceAll("$1", tableName);
                            }
                            if (!tableName.isEmpty()) {
                                if (!newStatement_1.isEmpty())
                                    write_1.write(newStatement_1 + separator);
                                if (!newStatement_2.isEmpty())
                                    write_2.write(newStatement_2 + separator);
                            }
                        }
                    }
                    scanner.close();
                    write_1.close();
                    write_2.close();
                    return returnedValue;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static void setContextBooleans(boolean b, boolean b1) {
        ScriptGenContext.setExists_02(b);
        ScriptGenContext.setExists_03(b);
        ScriptGenContext.setExists_12(b1);
        ScriptGenContext.setExists_13(b1);
    }

    private static void generateExtra(String fileName, String absolutePath,
                                      String end) {
        if (fileName != null && end != null) {
            String selectQuery = separator
                    + "select to_char(sysdate,'dd/mm/yyyy hh24:mi:ssss') from dual;"
                    + separator;
            try {
                File extra = new File(absolutePath + File.separator + fileName);
                extra.createNewFile();
                BufferedWriter br = new BufferedWriter(new FileWriter(extra,
                        false));
                String content = ScriptGenContext.getLanzador_megacmp_Script();
                if (end.equals("cmp")) {
                    content = content.replace("$1", "00.sql");
                    content = content.replace("$2", "01.sql");
                    if (ScriptGenContext.exists_02 == true) {
                        content = content.replace("$3", "02.sql");
                    } else {
                        content = content.replace("@$3" + selectQuery, "");
                    }
                }
                if (end.equals("audit")) {
                    content = content.replace("$1", "10.sql");
                    content = content.replace("$2", "11.sql");
                    if (ScriptGenContext.exists_12 == true) {
                        content = content.replace("$3", "12.sql");
                    } else {
                        content = content.replace("@$3" + selectQuery, "");
                    }
                }
                if (end.equals("system")) {
                    if (ScriptGenContext.exists_03 == true) {
                        content = content.replace("$1", "03.sql");
                    } else {
                        content = content.replace("@$1" + selectQuery, "");
                    }
                    if (ScriptGenContext.exists_13 == true) {
                        content = content.replace("$2", "13.sql");
                    } else {
                        content = content.replace("@$2" + selectQuery, "");
                    }
                    content = content.replace("@$3" + selectQuery, "");
                }
                br.write(content);
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
