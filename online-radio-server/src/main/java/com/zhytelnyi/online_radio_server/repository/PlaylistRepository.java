package com.zhytelnyi.online_radio_server.repository;

import com.zhytelnyi.online_radio_server.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
}
