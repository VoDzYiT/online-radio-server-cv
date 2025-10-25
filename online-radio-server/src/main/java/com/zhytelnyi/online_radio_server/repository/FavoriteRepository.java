package com.zhytelnyi.online_radio_server.repository;

import com.zhytelnyi.online_radio_server.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> { }
