package com.planner.backend;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
public class Account {
    private @Id @GeneratedValue Long id;

    @Column(unique = true)
    private String email;

    private String passwordHash;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Template> templates;

    Account() {

    }

    Account(String username, String passwordHash) {
        this.email = username;
        this.passwordHash = passwordHash;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account user = (Account) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                '}';
    }
}
