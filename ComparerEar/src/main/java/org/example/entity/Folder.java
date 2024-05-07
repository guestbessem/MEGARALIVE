package org.example.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

@JsonSerialize
public class Folder {
    public static enum Status {
        MODIFIED,
        IDENTICAL
    }
    private int level;

    private Status status= Status.IDENTICAL;
    private String name;
    private String path;

    private ArrayList<Folder> children;
    private String data;
    private String  source;
    private String target;

    private String icon=  "pi pi-folder";
    private boolean hasChildren;

    public String getSource() {
        return source;
    }
    public Folder(String name, String path, Status status){
        this.name=name;
        this.path=path;
        this.status=status;
        this.children=new ArrayList<Folder>();
    }
    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }
    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }
    public Folder(int level, String name, String path, ArrayList<Folder> children, boolean hasChildren, Status status) throws IOException {
        this.name = name;
        this.children = children;
        this.hasChildren = hasChildren;
        this.status=status;
        this.path=path;
        this.level=level;
    }
    public Folder(){}
    public Folder(int level, String name, ArrayList<Folder> children, boolean hasChildren, String path, File data) throws IOException {
        this.name = name;
        this.children = children;
        this.hasChildren = hasChildren;
        this.path=path;
        this.level=level;
        initFileContent();
    }
    public Folder(int level, String name, ArrayList<Folder> children, boolean hasChildren, String path, String data) throws IOException {
        this.name = name;
        this.children = children;
        this.hasChildren = hasChildren;
        this.path=path;
        this.level=level;
       // initFileContent(data);
        this.data=data;
    }
    private void initFileContent() throws IOException {
        File file=new File(path);
        if(!file.isDirectory()){
            this.data = IOUtils.toString(new FileInputStream(file));
        }
    }
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Folder> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Folder> children) {
        this.children = children;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void addChildren(Folder folder){
        if(this.children==null) this.children=new ArrayList<Folder>();
        this.children.add(folder);
    }
}
