package com.zhytelnyi.online_radio_server.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "connection_logs")
public class ConnectionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;

    private String userIp;
    private String userAgent;
    private LocalDateTime timestamp;

    public ConnectionLog(Station station, String userIp, String userAgent) {
        this.station = station;
        this.userIp = userIp;
        this.userAgent = userAgent;
        this.timestamp = LocalDateTime.now();
    }

    public ConnectionLog() {}

}
