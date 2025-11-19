package com.zhytelnyi.online_radio_server.repository;

import com.zhytelnyi.online_radio_server.model.ListenLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListenLogRepository extends JpaRepository<ListenLog, Long> {
}
