package com.zhytelnyi.online_radio_server.model;
import com.zhytelnyi.online_radio_server.service.iterator.TrackCollection;
import com.zhytelnyi.online_radio_server.service.iterator.TrackIterator;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

@Entity
@Table(name = "playlists")
public class Playlist implements TrackCollection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "playlist_tracks",
            joinColumns = @JoinColumn(name = "playlist_id"),
            inverseJoinColumns = @JoinColumn(name = "track_id")
    )
    private List<Track> tracks = new ArrayList<>();

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

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public void addTrack(Track track) {
        this.tracks.add(track);
        track.getPlaylists().add(this);
    }

    public void removeTrack(Track track) {
        this.tracks.add(track);
        track.getPlaylists().add(this);
    }

    @Override
    @Transient
    public TrackIterator createIterator(){
        return new PlaylistIterator(this);
    }

    private class PlaylistIterator implements TrackIterator {
        private List<Track> tracksToIterate;
        private int currentIndex = 0;

        public PlaylistIterator(Playlist playlist) {
            this.tracksToIterate = new ArrayList<>(playlist.getTracks());
            // Можна потім додати щось по типу перемішування
        }

        @Override
        public  boolean hasNext() {
            return !tracksToIterate.isEmpty();
        }

        @Override
        public Track next() {
            if (!hasNext()) {
                throw new NoSuchElementException("Empty");
            }
            if (currentIndex >= tracksToIterate.size()) {
                currentIndex = 0;
            }

            Track track = tracksToIterate.get(currentIndex);
            currentIndex++;
            return track;
        }
    }

}
