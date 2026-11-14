package service;

import dao.UserDao;
import model.User;
import utils.PasswordUtil;

public class AuthService {
    private final UserDao userDao = new UserDao();
    private static User utilisateurConnecte;

    public User connecter(String login, String passwordClair) {
        String passwordHache = PasswordUtil.hashPassword(passwordClair);
        System.out.println("Login saisi: [" + login + "]");
        System.out.println("Hash calculé: [" + passwordHache + "]");
        User user = userDao.authentifier(login, passwordHache);
        if (user != null) {
            utilisateurConnecte = user;
        }
        return user;
    }

    public static User getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    public static void deconnecter() {
        utilisateurConnecte = null;
    }
}