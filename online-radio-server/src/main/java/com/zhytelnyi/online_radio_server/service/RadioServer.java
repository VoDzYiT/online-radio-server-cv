package com.zhytelnyi.online_radio_server.service;

import com.zhytelnyi.online_radio_server.model.Station;
import com.zhytelnyi.online_radio_server.model.Track;
import com.zhytelnyi.online_radio_server.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class RadioServer {

    @Autowired private StationRepository stationRepository;
    @Autowired private StatisticsService statisticsService;

    private final Map<Long, Station> activeStations = new ConcurrentHashMap<>();
    private final Map<Long, Track> currentTrack = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // Завантажуємо всі станції з БД в пам'ять
        List<Station> stationsFromDb = stationRepository.findAll();
        for (Station s : stationsFromDb) {
            activeStations.put(s.getId(), s);
            System.out.println("Loaded station: " + s.getName());
            // Тут треба буде запустити логіку мовлення для кожної станції
            // startStreaming(s);
        }
    }

    public void startStreaming(Station station) {
        // ... складна логіка запуску потоку ...
        System.out.println("Starting stream for " + station.getName());
        // Застосувати ITERATOR для плейлиста
        // Track t = station.getPlaylists().get(0).createIterator().next();
        // currentTrack.put(station.getId(), t);
    }

    public void stopStreaming(Station station) { /* ... */ }

    public StatisticsService getStatistics() {
        return this.statisticsService;
    }

    public List<Station> getStations() {
        return List.copyOf(activeStations.values());
    }


}
