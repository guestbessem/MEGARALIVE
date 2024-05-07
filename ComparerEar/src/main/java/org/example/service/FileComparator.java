package org.example.service;



import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.example.entity.Folder;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class FileComparator {

    public Folder fromList(List<Folder> folders) {
        Folder rootFolder = new Folder("", "", Folder.Status.IDENTICAL);

        for (Folder folder : folders) {
            String[] parts = folder.getPath().split(Pattern.quote(System.getProperty("file.separator")));//récupérer les différents noms de fichiers au niveau fu folder en position
            Folder parentFolder = rootFolder;

            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];

                if (!part.isEmpty()) {
                    Folder childFolder = findChildFolderByName(parentFolder, part);

                    if (childFolder == null) {
                        childFolder = new Folder(part, parentFolder.getPath() + File.separator + part,folder.getStatus());
                        if(part.contains(".")){
                            childFolder.setSource(folder.getSource());
                            childFolder.setTarget(folder.getTarget());
                        }
                        parentFolder.addChildren(childFolder);
                    }

                    parentFolder = childFolder;
                }
            }
        }

        return rootFolder;
    }

    private static Folder findChildFolderByName(Folder parentFolder, String name) {
        if(parentFolder.getChildren()!=null){
            for (Folder childFolder : parentFolder.getChildren()) {
                if (childFolder.getName().equals(name)) {
                    return childFolder;
                }
            }
        }
        return null;
    }
    public   Folder compare(File file1, File file2) throws IOException {
        Path dir1 = Paths.get(file1.getAbsolutePath());
        Path dir2 = Paths.get(file2.getAbsolutePath());

        ArrayList<Folder> folders=new ArrayList<Folder>();
        // transformer les fichiers en une Map (nom de fichier -> contenu du fichier) (clé/valeur)
        Map<String, String> files1 = getFilesMap(dir1);
        Map<String, String> files2 = getFilesMap(dir2);

        // transformer les fichiers en une Map (nom de fichier -> chemin absolue du fichier).
        Map<String, String> files1_n = getFilesMapPath(dir1);
        Map<String, String> files2_n = getFilesMapPath(dir2);

        Set<String> allFiles = new HashSet<>(files1.keySet());//keySet retourne les clés( les noms)
        allFiles.addAll(files2.keySet());
        for (String filename : allFiles) {
            String content1 = files1.getOrDefault(filename, "");//getOrDefault retourne le contenu du fichier selon le filename
            String content2 = files2.getOrDefault(filename, "");
            Folder folder=new Folder();
            folder.setPath(filename);
            folder.setName(filename);
            //comparer le fichier dans le ear 1 avec le fichier dans le ear2
            if (!content1.equals(content2)) {
                folder.setStatus(Folder.Status.MODIFIED);
                if(content1.equals("")){
                    folder.setSource("Empty");
                    folder.setTarget(getComparisonResultInHTML(new File(files2_n.getOrDefault(filename,"")),new File(files1_n.getOrDefault(filename,""))));
                }
                else if(content2.equals("")){
                    folder.setSource(getComparisonResultInHTML(new File(files1_n.getOrDefault(filename,"")),new File(files2_n.getOrDefault(filename,""))));
                    folder.setTarget("Empty");
                }
                else{ //si les deux fichiers ne sont pas vides
                    folder.setSource(getComparisonResultInHTML(new File(files1_n.getOrDefault(filename,"")),new File(files2_n.getOrDefault(filename,""))));
                    folder.setTarget(getComparisonResultInHTML(new File(files2_n.getOrDefault(filename,"")),new File(files1_n.getOrDefault(filename,""))));
                }
            }
            //traiter le cas ou le fichier n'est pas modifié
            else {
                folder.setStatus(Folder.Status.IDENTICAL);
                folder.setSource(getComparisonResultInHTML(new File(files1_n.getOrDefault(filename,"")),new File(files2_n.getOrDefault(filename,""))));
                folder.setTarget(getComparisonResultInHTML(new File(files2_n.getOrDefault(filename,"")),new File(files1_n.getOrDefault(filename,""))));
            }
            folders.add(folder);
        }

        return fromList(folders);
    }
    public static String getComparisonResultInHTML(File file1 , File file2) throws IOException {
        StringBuilder stringBuilder=new StringBuilder();
        String line1 =null;
        String line2 = null;
        BufferedReader reader1 = null;
        BufferedReader reader2 = null;

        // Vérifie si le fichier 1 existe
        if(file1.exists()){
            reader1 = new BufferedReader(new FileReader(file1.getAbsolutePath()));
            line1=reader1.readLine();
        }

        // Vérifie si le fichier 2 existe
        if(file2.exists()){
            reader2 = new BufferedReader(new FileReader(file2.getAbsolutePath()));
            line2=reader2.readLine();
        }

        DiffMatchPatch dmp = new DiffMatchPatch(); //bibliothéque de manipulation de texte
        // La méthode diffMain permet de générer juste les différences
        LinkedList<DiffMatchPatch.Diff> diffs = dmp.diffMain(
                readFileContent(Path.of(file1.toURI())),
                readFileContent(Path.of(file2.toURI())),
                true
        );
        dmp.diffCleanupSemantic(diffs);// exemple: diffs = [  Diff(Operation.DELETE, "world!"),......

        //Il faut générer une sortie HTML avec ces différences
        for (DiffMatchPatch.Diff diff : diffs) {
            //ajouter les correspondances
            if (diff.operation == DiffMatchPatch.Operation.EQUAL) {
                stringBuilder.append("<br>");
                stringBuilder.append(diff.text.replaceAll("\\n", "<br>"));
            // ajouter les suppressions
            } else if (diff.operation == DiffMatchPatch.Operation.DELETE) {
                stringBuilder.append("<span class=\"removed\" >");stringBuilder.append("");
                stringBuilder.append(diff.text.replaceAll("\\n", "<br>"));
                stringBuilder.append("</span>");

                //ajouter les lignes insérés
            } else if (diff.operation == DiffMatchPatch.Operation.INSERT) {
                stringBuilder.append("<br>");
                stringBuilder.append("<span class=\"added\" >");  stringBuilder.append("");
                stringBuilder.append(diff.text.replaceAll("\\n", "<br>"));

                stringBuilder.append("</span>");
            }
        }
        return stringBuilder.toString();
    }
    private static Map<String, String> getFilesMap(Path dir) throws IOException {
        return Files.walk(dir)
                .filter(p -> !Files.isDirectory(p))
                .collect(Collectors.toMap(
                        p -> relativizePath(dir, p),
                        p -> readFileContent(p)));
    }
    private static String relativizePath(Path baseDir, Path file) {
        return baseDir.relativize(file).toString();//renvoi le chemin relative
    }
    private static Map<String, String> getFilesMapPath(Path dir) throws IOException {
        return Files.walk(dir)
                .filter(p -> !Files.isDirectory(p))
                .collect(Collectors.toMap(
                        p -> relativizePath(dir, p),
                        p -> p.toString()));
    }
    private static String readFileContent(Path file) {
        try {
            return new String(Files.readAllBytes(file));
        } catch (IOException e) {
        return "";
         }
    }
}
