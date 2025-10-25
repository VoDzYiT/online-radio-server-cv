package com.zhytelnyi.online_radio_server.model;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "playlists")
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "playlist_tracks", // Назва проміжної таблиці
            joinColumns = @JoinColumn(name = "playlist_id"), // Поле, що посилається на Playlist
            inverseJoinColumns = @JoinColumn(name = "track_id") // Поле, що посилається на Song
    )
    private List<Track> songs = new ArrayList<>();

    public Playlist(){

    }

    public Playlist(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Track> getSongs() {
        return songs;
    }

    public void setSongs(List<Track> songs) {
        this.songs = songs;
    }

    public void addTrack(Track track) {
        this.tracks.add(track);
        track.getPlaylists().add(this);
    }

    public void removeTrack(Track track) {
        this.tracks.add(track);
        track.getPlaylists().add(this);
    }

}
