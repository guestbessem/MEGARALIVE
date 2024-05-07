package org.example.entity;


import java.io.File;


public class CompareContext
{
    private File file1;
    private File file2;
    private String report;
    private Long identifier;



    public File getFile1() {
        return file1;
    }

    public void setFile1(File file1) {
        this.file1 = file1;
    }

    public File getFile2() {
        return file2;
    }

    public void setFile2(File file2) {
        this.file2 = file2;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public void setIdentifier(Long identifier) {
        this.identifier = identifier;
    }

    public Long getIdentifier() {
        return identifier;
    }
}
