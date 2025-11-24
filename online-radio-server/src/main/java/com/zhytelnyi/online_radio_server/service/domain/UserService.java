package com.zhytelnyi.online_radio_server.service.domain;

import com.zhytelnyi.online_radio_server.model.Favorite;
import com.zhytelnyi.online_radio_server.model.Station;
import com.zhytelnyi.online_radio_server.model.User;
import com.zhytelnyi.online_radio_server.repository.FavoriteRepository;
import com.zhytelnyi.online_radio_server.repository.StationRepository;
import com.zhytelnyi.online_radio_server.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final StationRepository stationRepository;

    // Constructor Injection
    public UserService(UserRepository userRepository,
                       FavoriteRepository favoriteRepository,
                       StationRepository stationRepository) {
        this.userRepository = userRepository;
        this.favoriteRepository = favoriteRepository;
        this.stationRepository = stationRepository;
    }


    public String addFavorite(String username, Long stationId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station not found: " + stationId));

        if (favoriteRepository.existsByUserAndStation(user, station)) {
            return "Already added to favorites";
        }

        favoriteRepository.save(new Favorite(user, station));
        return "Successfully added to favorites";
    }

    public List<Station> getFavorites(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Отримуємо записи Favorite і витягуємо з них Station
        return favoriteRepository.findAllByUser(user).stream()
                .map(Favorite::getStation)
                .collect(Collectors.toList());
    }

    // Тут також можна додати методи для зміни пароля, оновлення профілю тощо.
}