package com.zhytelnyi.online_radio_server.model;

import com.zhytelnyi.online_radio_server.service.visitor.Element;
import com.zhytelnyi.online_radio_server.service.visitor.Visitor;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "users")
public class User implements Element {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;

    private String role;

    @OneToMany(mappedBy = "user")
    private Set<Favorite> favorites;

    @OneToMany(mappedBy = "user")
    private Set<Recording> recording;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Set<Favorite> getFavorites() {
        return favorites;
    }

    public void setFavorites(Set<Favorite> favorites) {
        this.favorites = favorites;
    }

    public Set<Recording> getRecording() {
        return recording;
    }

    public void setRecording(Set<Recording> recording) {
        this.recording = recording;
    }

    @Override
    public void accept(Visitor visitor) {

    }
}
