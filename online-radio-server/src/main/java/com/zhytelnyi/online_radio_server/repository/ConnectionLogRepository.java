package com.zhytelnyi.online_radio_server.repository;

import com.zhytelnyi.online_radio_server.model.ConnectionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConnectionLogRepository extends JpaRepository<ConnectionLog, Long> {
    List<ConnectionLog> findTop50ByOrderByTimestampDesc();
}
