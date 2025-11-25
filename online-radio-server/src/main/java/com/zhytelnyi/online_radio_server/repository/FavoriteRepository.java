package com.zhytelnyi.online_radio_server.repository;

import com.zhytelnyi.online_radio_server.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import com.zhytelnyi.online_radio_server.model.Station;
import com.zhytelnyi.online_radio_server.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    boolean existsByUserAndStation(User user, Station station);
    List<Favorite> findAllByUser(User user);

    @Transactional
    void deleteAllByStationId(Long stationId);
}
