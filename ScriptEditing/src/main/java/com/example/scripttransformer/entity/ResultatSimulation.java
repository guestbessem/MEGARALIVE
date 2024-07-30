package com.example.scripttransformer.entity;

import jakarta.persistence.*;


import java.io.File;
import java.util.Date;

@Table
@Entity
public class ResultatSimulation {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="SEQUENCE2")
    @SequenceGenerator(name="SEQUENCE2", sequenceName="SEQUENCE2", allocationSize=1)
    private Long id;
    private String rapport;
    private File script;
    @OneToOne
     private   ParametreSimulation ps;
    private Date dateDeSimulation;
    private Status status;
    private Boolean withError;

    public enum Status{
        VALIDATED, REJECTED, NOTHING
}

    public Boolean getWithError() {
        return withError;
    }

    public void setWithError(Boolean withError) {
        this.withError = withError;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ResultatSimulation(String rapport, File script, ParametreSimulation ps, Date dateDeSimulation, Status status) {
        this.rapport = rapport;
        this.script = script;
        this.ps = ps;
        this.dateDeSimulation = dateDeSimulation;
        this.status = status;
    }

    public Date getDateDeSimulation() {
        return dateDeSimulation;
    }

    public void setDateDeSimulation(Date dateDeSimulation) {
        this.dateDeSimulation = dateDeSimulation;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ResultatSimulation(File script, ParametreSimulation ps) {
        this.script = script;
        this.ps = ps;
    }

    public ResultatSimulation() {

    }

    public String getRapport() {
        return rapport;
    }

    public void setRapport(String rapport) {
        this.rapport = rapport;
    }

    public File getScript() {
        return script;
    }

    public void setScript(File script) {
        this.script = script;
    }

    public ParametreSimulation getPs() {
        return ps;
    }

    public void setPs(ParametreSimulation ps) {
        this.ps = ps;
    }
}
