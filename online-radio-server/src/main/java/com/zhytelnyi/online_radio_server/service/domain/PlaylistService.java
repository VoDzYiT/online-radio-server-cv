package com.zhytelnyi.online_radio_server.service.domain;

import com.zhytelnyi.online_radio_server.model.Playlist;
import com.zhytelnyi.online_radio_server.model.Station;
import com.zhytelnyi.online_radio_server.model.Track;
import com.zhytelnyi.online_radio_server.repository.PlaylistRepository;
import com.zhytelnyi.online_radio_server.repository.StationRepository;
import com.zhytelnyi.online_radio_server.service.factory.PlaylistFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistFactory playlistFactory;
    private final StationRepository stationRepository;

    public PlaylistService(PlaylistRepository playlistRepository, PlaylistFactory playlistFactory, StationRepository stationRepository) {
        this.playlistRepository = playlistRepository;
        this.playlistFactory = playlistFactory;
        this.stationRepository = stationRepository;
    }

    public Playlist create(String name, List<Track> tracks) {
        Playlist playlist = playlistFactory.createPlaylist(name);
        if (tracks != null) tracks.forEach(playlist::addTrack);
        return playlistRepository.save(playlist);
    }

    public void update(Long id, String newName, List<Track> newTracks) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        playlist.setName(newName);

        if (newTracks != null) {
            playlist.setTracks(newTracks);
        } else {
            playlist.getTracks().clear();
        }
        playlistRepository.save(playlist);
    }

    public Playlist findById(Long id) {
        return playlistRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
    }

    public List<Playlist> findAll() { return playlistRepository.findAll(); }
    public List<Playlist> findAllByIds(List<Long> ids) { return playlistRepository.findAllById(ids); }
    @Transactional
    public void delete(Long id) {
        Playlist playlist = findById(id);
        List<Station> stations = stationRepository.findAllByPlaylistsContaining(playlist);

        for (Station station : stations) {
            station.getPlaylists().remove(playlist);
            stationRepository.save(station);
        }

        playlistRepository.deleteById(id);
    }
}