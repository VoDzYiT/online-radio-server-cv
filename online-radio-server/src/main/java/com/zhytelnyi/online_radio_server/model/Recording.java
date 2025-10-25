package com.zhytelnyi.online_radio_server.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "recordings")
public class Recording {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne @JoinColumn(name = "station_id")
    private Station station;

    private LocalDateTime timestamp;
    private String filePath;
}
