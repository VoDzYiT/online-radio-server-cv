package com.zhytelnyi.online_radio_server.model;
import com.zhytelnyi.online_radio_server.service.visitor.Element;
import com.zhytelnyi.online_radio_server.service.visitor.Visitor;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
@Entity
@Table(name = "tracks")
public class Track implements Element {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int durationInSeconds;

    private String artist;

    @Column(nullable = false, unique = true)
    private String filePath;

    @ManyToMany(mappedBy = "tracks")
    private Set<Playlist> playlists = new HashSet<>();

    public Track() {
    }

    public Track(String title, String artist, String filePath, int durationInSeconds){
        this.title = title;
        this.artist = artist;
        this.filePath = filePath;
        this.durationInSeconds = durationInSeconds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }


    public String getFilePath() {
        return filePath;
    }


    public Set<Playlist> getPlaylists() {
        return playlists;
    }


    public int getDurationInSeconds() {
        return durationInSeconds;
    }


    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
