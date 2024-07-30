package com.example.scripttransformer.entity;


import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@NoArgsConstructor

@Entity
@Table(name="FilesToken")
public class FilesToken {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="SEQUENCE1")
    @SequenceGenerator(name="SEQUENCE1", sequenceName="SEQUENCE1", allocationSize=1)
    private Long token;

    public Long getToken() {
        return token;
    }

    public void setToken(Long t){
        this.token=t;
    }

}