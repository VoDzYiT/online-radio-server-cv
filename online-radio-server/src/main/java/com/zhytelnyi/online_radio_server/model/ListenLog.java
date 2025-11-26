package com.zhytelnyi.online_radio_server.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "listen_logs")
public class ListenLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "track_id", nullable = true)
    private Track track;

    @ManyToOne
    @JoinColumn(name = "station_id", nullable = true)
    private Station station;

    public LocalDateTime timestamp;

    public Track getTrack() {
        return track;
    }

    public Station getStation() {
        return station;
    }

    public ListenLog(Track track, Station station) {
        this.track = track;
        this.station = station;
        this.timestamp = LocalDateTime.now();
    }

    public ListenLog() {}

}
