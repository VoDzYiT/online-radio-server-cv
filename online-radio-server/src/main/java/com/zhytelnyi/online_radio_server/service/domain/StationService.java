package com.zhytelnyi.online_radio_server.service.domain;

import com.zhytelnyi.online_radio_server.model.Playlist;
import com.zhytelnyi.online_radio_server.model.Station;
import com.zhytelnyi.online_radio_server.repository.StationRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
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
    public void delete(Long id) { stationRepository.deleteById(id); }
}