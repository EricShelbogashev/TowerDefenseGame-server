package ru.nsu.shelbogashev.tdgserver.service;

public class CredentialsHelper {
    public static boolean isValidUsername(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }

        int usernameLength = username.length();
        if (usernameLength < 4 || usernameLength > 20) {
            return false;
        }

        return username.matches("[a-zA-Z0-9]+");
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        int passwordLength = password.length();
        if (passwordLength < 8 || passwordLength > 20) {
            return false;
        }

        return password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*\\d.*");
    }
}
