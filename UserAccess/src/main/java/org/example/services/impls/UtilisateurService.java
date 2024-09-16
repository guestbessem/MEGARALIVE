package org.example.services.impls;


import org.example.entities.User;
import org.example.repositories.UtilisateurJPA;
import org.example.services.inters.IUtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UtilisateurService implements IUtilisateurService {
    @Autowired
    UtilisateurJPA j;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public User ajouterUtilisateur(User utilisateur) {
        utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));//passwordEncoder: hachage des mot de passe pour le sécurisé
        return j.save(utilisateur);
    }

    @Override
    public User modifier(User utilisateur) {
        User u=j.getOne(utilisateur.getEmail());
        if(u!=null){
            u.setUsername(utilisateur.getUsername());
            u.setEmail(utilisateur.getEmail());
            u.setCin(utilisateur.getCin());
            u.setRoles(utilisateur.getRoles());
            u.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
            j.save(u);
            return u;
        }
        return utilisateur;
    }

    @Override
    public boolean supprimerUtilisateur(User User) {
        j.delete(User);
        return true;
    }

    @Override
    public User getUtilisateur(String email) {
        return j.getOne(email);
    }

    @Override
    public ArrayList<User> getAllUtilisateur() {
        return (ArrayList<User>) j.findAll();
    }
}
