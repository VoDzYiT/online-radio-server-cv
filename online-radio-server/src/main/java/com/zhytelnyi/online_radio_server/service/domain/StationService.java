package com.zhytelnyi.online_radio_server.service.domain;

import com.zhytelnyi.online_radio_server.model.Playlist;
import com.zhytelnyi.online_radio_server.model.Station;
import com.zhytelnyi.online_radio_server.repository.FavoriteRepository;
import com.zhytelnyi.online_radio_server.repository.ListenLogRepository;
import com.zhytelnyi.online_radio_server.repository.StationRepository;
import com.zhytelnyi.online_radio_server.service.core.RadioServer;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StationService {

    private final StationRepository stationRepository;
    private final RadioServer radioServer;
    private final FavoriteRepository favoriteRepository;
    private final ListenLogRepository listenLogRepository;
    public StationService(StationRepository stationRepository, RadioServer radioServer, FavoriteRepository favoriteRepository, ListenLogRepository listenLogRepository) {
        this.stationRepository = stationRepository;
        this.radioServer = radioServer;
        this.favoriteRepository = favoriteRepository;
        this.listenLogRepository = listenLogRepository;
    }

    public Station create(String name, int bitrate, List<Playlist> playlists) {
        Station station = new Station();
        station.setName(name);
        station.setBitrate(bitrate);
        if (playlists != null) station.setPlaylists(playlists);
        return stationRepository.save(station);
    }

    public Station findById(Long id) { return stationRepository.findById(id).orElse(null); }
    public List<Station> findAll() { return stationRepository.findAll(); }
    public void delete(Long id) {
        radioServer.stopStreaming(id);
        favoriteRepository.deleteAllByStationId(id);
        listenLogRepository.detachStationFromLogs(id);
        stationRepository.deleteById(id);
    }
}