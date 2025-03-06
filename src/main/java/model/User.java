package model;

public class User {
    private int idUser;
    private String username;
    private String passwordHash;
    private String role;

    public User() {
    }

    public User(int idUser, String username, String passwordHash, String role) {
        this.idUser = idUser;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "idUser=" + idUser +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}

