package com.zhytelnyi.online_radio_server.model;

import com.zhytelnyi.online_radio_server.service.visitor.Element;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "stations")
public class Station implements Element {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "station_id")
    private List<Playlist> playlists;

    public void addPlayList(Playlist p) {}
    public void removePlayList(Playlist p) {}

    @Override
    public void accept(Visitor visitor) {

    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }
}
