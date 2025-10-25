package com.zhytelnyi.online_radio_server.service.facade;

import com.zhytelnyi.online_radio_server.model.Favorite;
import com.zhytelnyi.online_radio_server.model.Recording;
import com.zhytelnyi.online_radio_server.model.Station;
import com.zhytelnyi.online_radio_server.model.User;
import com.zhytelnyi.online_radio_server.repository.FavoriteRepository;
import com.zhytelnyi.online_radio_server.repository.RecordingRepository;
import com.zhytelnyi.online_radio_server.service.RadioServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RadioFacade {

    // Фасад інкапсулює всі ці компоненти
    @Autowired private RadioServer radioServer;
    @Autowired private FavoriteRepository favoriteRepository;
    @Autowired private RecordingRepository recordingRepository;

    // 1. Простий метод, що ховає RadioServer
    public List<Station> getAllStations() {
        return radioServer.getStations();
    }

    // 2. Простий метод, що ховає логіку "Вибраного"
    public void addToFavorites(User user, Station station) {
        Favorite fav = new Favorite(user, station);
        favoriteRepository.save(fav);
    }

    // 3. Простий метод, що ховає логіку "Запису"
    public void startRecording(User user, Station station) {
        // Тут була б складна логіка запуску запису потоку у файл
        Recording rec = new Recording(/* ... */);
        recordingRepository.save(rec);
        System.out.println("User " + user.getUsername() + " started recording " + station.getName());
    }

    public void stopRecording(User user, Recording recording) {
        // ... логіка зупинки ...
        System.out.println("User " + user.getUsername() + " stopped recording.");
    }
}