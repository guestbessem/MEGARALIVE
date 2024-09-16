package org.example.controllers;


import org.example.entities.User;
import org.example.services.impls.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;



@RestController
@RequestMapping("/Utilisateur")
public class UtilisateurController {

    @Autowired
    UtilisateurService us;

    @GetMapping("/all")
    public ArrayList<User> getAllUtilisateur(){
        return us.getAllUtilisateur();
    }
    @PostMapping("/add")
    public ResponseEntity<?> createUtilisateur(@RequestBody User utilisateur) {
        try {
            User createdUser = us.ajouterUtilisateur(utilisateur);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating user: " + e.getMessage());
        }
    }

    @PostMapping("/email")
    public ResponseEntity<User> getUtilisateur(@RequestParam("email")String email){
        User u=us.getUtilisateur(email);
        return ResponseEntity.ok().body(u);
    }
    @PostMapping(value="/delete")
    public boolean deleteUtilisateur(@RequestBody User utilisateur){
        return us.supprimerUtilisateur(utilisateur);
    }

    @PostMapping(value="/update")
    public User updateUtilisateur(@RequestBody User utilisateur){
        User u=us.modifier(utilisateur);
        return u;
    }
}
