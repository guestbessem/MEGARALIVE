package com.example.scripttransformer.entity;

import jakarta.persistence.*;



import java.io.File;
@Table(name="ScriptTransformationHistory")
@Entity
public class ScriptTransformation {


    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="SEQUENCE4")
    @SequenceGenerator(name="SEQUENCE4", sequenceName="SEQUENCE4", allocationSize=1)
    private long id;

    enum Status{
        ACCEPTED, REJECTED
    }
    private File sourceScripts;

    private File transformedScripts;
    @OneToOne
    @JoinColumn(name = "id")
    private STransformationParameters sparams;
    private String tempLocation;

    public File getTransformedScripts() {
        return transformedScripts;
    }

    public void setTransformedScripts(File transformedScripts) {
        this.transformedScripts = transformedScripts;
    }

    public STransformationParameters getSparams() {
        return sparams;
    }

    public void setSparams(STransformationParameters sparams) {
        this.sparams = sparams;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public File getSourceScripts() {
        return sourceScripts;
    }

    public void setSourceScripts(File sourceScripts) {
        this.sourceScripts = sourceScripts;
    }

    public String getTempLocation() {
        return tempLocation;
    }

    public void setTempLocation(String tempLocation) {
        this.tempLocation = tempLocation;
    }
}
