package com.zhytelnyi.online_radio_server.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Favorites")
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    private LocalDateTime addedAt;

    public Favorite(User user, Station station) {
        this.user = user;
        this.station = station;
        this.addedAt = LocalDateTime.now();
    }

    public Favorite() {}

    public User getUser() { return user; }
    public Station getStation() { return station; }
}
