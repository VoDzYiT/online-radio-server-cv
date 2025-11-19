package com.zhytelnyi.online_radio_server.repository;

import com.zhytelnyi.online_radio_server.model.ConnectionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectionLogRepository extends JpaRepository<ConnectionLog, Long> {
}
