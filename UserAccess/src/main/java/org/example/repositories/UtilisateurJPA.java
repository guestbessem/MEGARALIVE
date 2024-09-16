package org.example.repositories;


import org.example.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilisateurJPA extends JpaRepository<User,String> {
}
