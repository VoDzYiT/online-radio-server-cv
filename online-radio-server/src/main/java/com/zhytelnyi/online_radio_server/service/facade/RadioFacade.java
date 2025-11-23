package com.zhytelnyi.online_radio_server.service.facade;

import com.zhytelnyi.online_radio_server.model.*;
import com.zhytelnyi.online_radio_server.repository.*;
import com.zhytelnyi.online_radio_server.service.RadioServer;
import com.zhytelnyi.online_radio_server.service.adapter.IReportExporter;
import com.zhytelnyi.online_radio_server.service.factory.PlaylistFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RadioFacade {

    @Autowired private StationRepository stationRepository;
    @Autowired private PlaylistRepository playlistRepository;
    @Autowired private TrackRepository trackRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private FavoriteRepository favoriteRepository;
    @Autowired private PlaylistFactory playlistFactory;
    @Autowired
    @Qualifier("jsonExporter")
    private IReportExporter reportExporter;
    @Autowired private RadioServer radioServer;

    private static final Path MUSIC_UPLOAD_DIR = Paths.get(System.getProperty("user.home"), "radio-hls-static", "music");

    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }

    public Track uploadTrack(String title, String artist, MultipartFile file) {
        try {
            if (!MUSIC_UPLOAD_DIR.toFile().exists()) {
                MUSIC_UPLOAD_DIR.toFile().mkdirs();
            }

            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path targetPath = MUSIC_UPLOAD_DIR.resolve(filename);

            file.transferTo(targetPath);

            Track track = new Track(title, artist, targetPath.toString(), 300);
            return trackRepository.save(track);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    public Playlist createPlaylist(String name, List<Long> trackIds) {
        Playlist playlist = playlistFactory.createPlaylist(name);
        if (trackIds != null) {
            List<Track> tracks = trackRepository.findAllById(trackIds);
            for (Track t : tracks) playlist.addTrack(t);
        }
        return playlistRepository.save(playlist);
    }

    public Station createStation(String name, int bitrate, List<Long> playlistIds) {
        Station station = new Station();
        station.setName(name);
        station.setBitrate(bitrate);

        if (playlistIds != null) {
            List<Playlist> playlists = playlistRepository.findAllById(playlistIds);
            station.setPlaylists(playlists);
        }

        Station savedStation = stationRepository.save(station);

        try {
            System.out.println("Dynamically starting stream for: " + savedStation.getName());
            radioServer.startChunking(savedStation);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return savedStation;
    }

    public String addStationToFavorites(String username, Long stationId) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Station station = stationRepository.findById(stationId).orElseThrow();

        if (favoriteRepository.existsByUserAndStation(user, station)) {
            return "Already added";
        }
        favoriteRepository.save(new Favorite(user, station));
        return "Added to favorites";
    }

    public List<Station> getUserFavoriteStations(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        return favoriteRepository.findAllByUser(user).stream()
                .map(Favorite::getStation)
                .collect(Collectors.toList());
    }

    public Playlist getPlaylistById(Long id) {
        return playlistRepository.findById(id).orElseThrow();
    }

    public void updatePlaylist(Long id, String newName, List<Long> trackIds) {
        Playlist playlist = playlistRepository.findById(id).orElseThrow();

        // 1. Оновлюємо назву
        playlist.setName(newName);

        // 2. Оновлюємо треки (якщо список передали)
        if (trackIds != null) {
            List<Track> newTracks = trackRepository.findAllById(trackIds);
            // Оскільки у нас @ManyToMany, ми просто замінюємо список
            playlist.setTracks(newTracks);
        } else {
            // Якщо нічого не вибрали - очищуємо список?
            // Або залишаємо як є. Для безпеки краще залишити, якщо null.
            // Але HTML форма може надіслати порожній список.
            // Давайте зробимо так:
            playlist.getTracks().clear();
        }

        playlistRepository.save(playlist);
    }

    public String getStationReport(Long stationId) {
        Station station = stationRepository.findById(stationId).orElse(null);

        if (station == null) {
            return "{ \"error\": \"Not Found\" }";
        }

        return reportExporter.export(station);
    }

    public List<Track> getAllTracks() { return trackRepository.findAll(); }
    public List<Playlist> getAllPlaylists() { return playlistRepository.findAll(); }

    public void deleteTrack(Long id) { trackRepository.deleteById(id); }
    public void deletePlaylist(Long id) { playlistRepository.deleteById(id); }
    public void deleteStation(Long id) { stationRepository.deleteById(id); }
}