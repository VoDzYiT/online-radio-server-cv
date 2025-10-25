package com.zhytelnyi.online_radio_server.repository;

import com.zhytelnyi.online_radio_server.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Long> { }
