package com.example.scripttransformer.repository;

import com.example.scripttransformer.entity.ScriptTransformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScriptTransfRepository extends JpaRepository<ScriptTransformation,Long> {
}
