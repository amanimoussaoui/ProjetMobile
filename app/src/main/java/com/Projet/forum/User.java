package com.Projet.forum;

public class User {
    private String id;
    private String name;
    private String email;
    private String password;
    private boolean isAdmin;

    // Add this inside User class
    public User() {}
    public User(String id, String email, String name, String password, boolean isAdmin) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.isAdmin = isAdmin;
    }


    public User(String id, String email, String name,  boolean isAdmin) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.isAdmin = isAdmin;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }
}
