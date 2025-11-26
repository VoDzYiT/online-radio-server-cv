package com.zhytelnyi.online_radio_server.service.facade;

import com.zhytelnyi.online_radio_server.model.*;
import com.zhytelnyi.online_radio_server.service.core.RadioServer;
import com.zhytelnyi.online_radio_server.service.domain.*; // Імпортуємо наші нові сервіси
import com.zhytelnyi.online_radio_server.service.adapter.IReportExporter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service

public class RadioFacade {

    private final TrackService trackService;
    private final PlaylistService playlistService;
    private final StationService stationService;
    private final UserService userService;
    private final RadioServer radioServer;
    private final IReportExporter reportExporter;

    public RadioFacade(TrackService trackService,
                       PlaylistService playlistService,
                       StationService stationService,
                       UserService userService,
                       RadioServer radioServer,
                       @Qualifier("jsonExporter") IReportExporter reportExporter) {
        this.trackService = trackService;
        this.playlistService = playlistService;
        this.stationService = stationService;
        this.userService = userService;
        this.radioServer = radioServer;
        this.reportExporter = reportExporter;
    }

    // === TRACKS ===
    public Track uploadTrack(String title, String artist, MultipartFile file) {
        return trackService.uploadTrack(title, artist, file);
    }
    public List<Track> getAllTracks() { return trackService.findAll(); }
    public void deleteTrack(Long id) { trackService.delete(id); }

    // === PLAYLISTS ===
    public Playlist createPlaylist(String name, List<Long> trackIds) {
        List<Track> tracks = (trackIds != null) ? trackService.findAllByIds(trackIds) : null;
        return playlistService.create(name, tracks);
    }

    public void updatePlaylist(Long id, String newName, List<Long> trackIds) {
        List<Track> tracks = (trackIds != null) ? trackService.findAllByIds(trackIds) : null;
        playlistService.update(id, newName, tracks);

        Playlist updatedPlaylist = playlistService.findById(id);
        List<Station> affectedStations = stationService.findAllByPlaylist(updatedPlaylist);

        for (Station station : affectedStations) {
            try {
                radioServer.restartChunking(station);
            } catch (Exception e) {
                System.err.println("Failed to restart stream for station " + station.getName());
            }
        }
    }
    public Playlist getPlaylistById(Long id) { return playlistService.findById(id); }
    public List<Playlist> getAllPlaylists() { return playlistService.findAll(); }
    public void deletePlaylist(Long id) { playlistService.delete(id); }

    // === STATIONS ===
    public Station createStation(String name, int bitrate, List<Long> playlistIds) {
        List<Playlist> playlists = (playlistIds != null) ? playlistService.findAllByIds(playlistIds) : null;

        // 1. Створити станцію (через сервіс)
        Station savedStation = stationService.create(name, bitrate, playlists);

        // 2. Запустити ефір (через RadioServer)
        // Ось тут Фасад показує свою силу: він об'єднує логіку БД і логіку Стрімінгу
        try {
            radioServer.startChunking(savedStation);
        } catch (Exception e) {
            System.err.println("Warning: Failed to start stream immediately: " + e.getMessage());
        }
        return savedStation;
    }

    public List<Station> getAllStations() { return stationService.findAll(); }
    public void deleteStation(Long id) { stationService.delete(id); }

    public String getStationReport(Long id) {
        Station station = stationService.findById(id);
        if (station == null) return "{\"error\": \"Not Found\"}";
        return reportExporter.export(station);
    }

    // === FAVORITES ===
    public String addStationToFavorites(String username, Long stationId) {
        return userService.addFavorite(username, stationId);
    }
    public List<Station> getUserFavoriteStations(String username) {
        return userService.getFavorites(username);
    }
}