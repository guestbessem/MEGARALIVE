package org.example.services.inters;

import org.example.entities.User;


import java.util.ArrayList;

public interface IUtilisateurService {
    public User ajouterUtilisateur(User utilisateur);

    public User modifier(User utilisateur);



    boolean supprimerUtilisateur(User utilisateur);

    public User getUtilisateur(String cin);
    public ArrayList<User> getAllUtilisateur();
}
