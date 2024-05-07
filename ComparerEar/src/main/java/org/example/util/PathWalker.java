package org.example.util;



import org.example.entity.Folder;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class PathWalker {
    private Folder.Status pathStatus;
    private String path;

    public PathWalker(Folder.Status status) {
        this.pathStatus=status;
    }
    public PathWalker(String path) {
        this.path=path;
    }

    public static class Node {
        private final Map<String, Node> children = new TreeMap<>();

        public Node getChild(String name) {
            if (children.containsKey(name))
                return children.get(name);
            Node result = new Node();
            children.put(name, result);
            return result;
        }

        public Map<String, Node> getChildren() {
            return Collections.unmodifiableMap(children);
        }
    }

    private final Node root = new Node();

    private static final Pattern PATH_SEPARATOR = Pattern.compile("/");
    public void setListOfPaths(Set<String> paths){
        for(String path :paths){
            addPath(path);
        }
    }
    public void addPath(String path) {
        String[] names = PATH_SEPARATOR.split(path);
        Node node = root;
        for (String name : names)
            node = node.getChild(name);
    }
    public Folder showTree() throws IOException {
        Folder folder=new Folder(0,"","",new ArrayList<Folder>(),true,this.pathStatus);
        printTree(folder,root);
        return folder;
    }
    private  void printTree(Folder folder, Node node) throws IOException {
        Map<String, Node> children = node.getChildren();
        if ( children==null || children.isEmpty() )
            return;
        List<Folder> childs=new ArrayList<>();
        for (Map.Entry<String, Node> child : children.entrySet()) {
            Folder subFolder  = new Folder(0,child.getKey(),child.getKey(), new ArrayList<Folder>(),true,this.pathStatus);
            printTree(subFolder,child.getValue());
            childs.add(subFolder);
        }
        folder.setChildren((ArrayList<Folder>) childs);
    }

   public static Folder getFolderTree(String path, int level) throws IOException {
        File file=new File(path);
        ArrayList<Folder> folderArrayList=new ArrayList<Folder>();
        Folder folder=new Folder(level,file.getName(),folderArrayList,(file.listFiles()!=null && file.listFiles().length>0),path, file);
        if(file.isDirectory()){
            for(File f:file.listFiles()){
                if(!f.isDirectory()) {
                    Folder f2 = new Folder(level+1,f.getName() ,null, false,f.getAbsolutePath(), f);
                    folderArrayList.add(f2);
                }
                else{
                    folderArrayList.add(getFolderTree(f.getAbsolutePath(),level+1));
                }
            }
            folder.setChildren(folderArrayList);
        }
       return folder;
    }
    }
