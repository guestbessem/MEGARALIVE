package com.example.scripttransformer.repository;

import com.example.scripttransformer.entity.FilesToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileTokenJPA extends JpaRepository<FilesToken,Long> {
}
