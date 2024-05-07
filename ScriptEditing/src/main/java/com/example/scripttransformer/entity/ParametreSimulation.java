package com.example.scripttransformer.entity;


import jakarta.persistence.*;



@Table
@Entity
public class ParametreSimulation {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="SEQUENCE3")
    @SequenceGenerator(name="SEQUENCE3", sequenceName="SEQUENCE3", allocationSize=1)
    private Long id;


    private String url;
    private String port;
    private String jdbcName;
    @Column(name = "user_")
    private String user;
    private String pwd;

    public ParametreSimulation(String url, String port, String jdbcName, String user, String pwd) {
        this.url = url;
        this.port = port;
        this.jdbcName = jdbcName;
        this.user = user;
        this.pwd = pwd;
    }

    public ParametreSimulation() {

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

    public String getJdbcName() {
        return jdbcName;
    }

    public void setJdbcName(String jdbcName) {
        this.jdbcName = jdbcName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

}
