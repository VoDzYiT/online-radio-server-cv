package com.zhytelnyi.online_radio_server.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Favorites")
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne @JoinColumn(name = "station_id")
    private Station station;

    public Favorite(User user, Station station) {
        this.user = user;
        this.station = station;
    }
}
