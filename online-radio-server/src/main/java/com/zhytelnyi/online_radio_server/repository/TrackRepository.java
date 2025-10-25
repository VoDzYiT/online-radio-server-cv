package com.zhytelnyi.online_radio_server.repository;
import com.zhytelnyi.online_radio_server.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {
    Optional<Track> findByFilePath(String filePath);
}
