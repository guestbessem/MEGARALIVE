package org.example.repository;



import org.example.entity.FilesToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileTokenJPA extends JpaRepository<FilesToken,Long> {
}
