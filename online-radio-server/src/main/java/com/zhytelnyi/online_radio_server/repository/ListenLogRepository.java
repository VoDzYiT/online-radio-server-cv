package com.zhytelnyi.online_radio_server.repository;

import com.zhytelnyi.online_radio_server.model.ListenLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ListenLogRepository extends JpaRepository<ListenLog, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE ListenLog l SET l.track = NULL WHERE l.track.id = :trackId")
    void detachTrackFromLogs(Long trackId);

    @Modifying
    @Transactional
    @Query("UPDATE ListenLog l SET l.station = NULL WHERE l.station.id = :stationId")
    void detachStationFromLogs(Long stationId);

    List<ListenLog> findTop50ByOrderByTimestampDesc();
}
