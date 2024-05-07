package com.example.scripttransformer.entity;

import jakarta.persistence.*;



@Entity
@Table(name="STransformationParameters")
public class STransformationParameters {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="SEQUENCE5")
    @SequenceGenerator(name="SEQUENCE5", sequenceName="SEQUENCE5", allocationSize=1)
    private Long id;
    private String dbName;
    private String url;
    private String port;

    private String user1;

    private String user2;
    private String source_File_Name_User_1;
    private String source_File_Name_User_2;

    private String file0_regex_User_1;
    private String file1_regex_User_1;
    private String file0_regex_User_2;
    private String file1_regex_User_2;

    private String user1_role;
    private String user2_role;

    private String pwd;

    public STransformationParameters() {

    }

    public String getUser1_role() {
        return user1_role;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setUser1_role(String user1_role) {
        this.user1_role = user1_role;
    }

    public String getUser2_role() {
        return user2_role;
    }

    public void setUser2_role(String user2_role) {
        this.user2_role = user2_role;
    }

    public String getSource_File_Name_User_1() {
        return source_File_Name_User_1;
    }

    public void setSource_File_Name_User_1(String source_File_Name_User_1) {
        this.source_File_Name_User_1 = source_File_Name_User_1;
    }

    public String getSource_File_Name_User_2() {
        return source_File_Name_User_2;
    }

    public void setSource_File_Name_User_2(String source_File_Name_User_2) {
        this.source_File_Name_User_2 = source_File_Name_User_2;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public String getFile0_regex_User_1() {
        return file0_regex_User_1;
    }

    public void setFile0_regex_User_1(String file0_pattern_User_1) {
        this.file0_regex_User_1 = file0_pattern_User_1;
    }

    public String getFile1_regex_User_1() {
        return file1_regex_User_1;
    }

    public void setFile1_regex_User_1(String file1_pattern_User_1) {
        this.file1_regex_User_1 = file1_pattern_User_1;
    }

    public String getFile0_regex_User_2() {
        return file0_regex_User_2;
    }

    public void setFile0_regex_User_2(String file0_pattern_User_2) {
        this.file0_regex_User_2 = file0_pattern_User_2;
    }

    public String getFile1_regex_User_2() {
        return file1_regex_User_2;
    }

    public void setFile1_regex_User_2(String file1_pattern_User_2) {
        this.file1_regex_User_2 = file1_pattern_User_2;
    }


    public STransformationParameters(String user1_role,String user2_role,String dbName, String url, String port, String user1, String user2, String source_File_Name_User_1, String source_File_Name_User_2, String file0_regex_User_1, String file1_regex_User_1, String file0_regex_User_2, String file1_regex_User_2) {
        this.dbName = dbName;
        this.url = url;
        this.port = port;
        this.user1 = user1;
        this.user2 = user2;
        this.source_File_Name_User_1 = source_File_Name_User_1;
        this.source_File_Name_User_2 = source_File_Name_User_2;
        this.file0_regex_User_1 = file0_regex_User_1;
        this.file1_regex_User_1 = file1_regex_User_1;
        this.file0_regex_User_2 = file0_regex_User_2;
        this.file1_regex_User_2 = file1_regex_User_2;
        this.user1_role=user1_role;
        this.user2_role=user2_role;
    }
}
