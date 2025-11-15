package com.zhytelnyi.online_radio_server.service.factory;

import com.zhytelnyi.online_radio_server.model.Playlist;
import org.springframework.stereotype.Component;

@Component
public class PlaylistFactory {

    public Playlist createPlaylist(String name) {
        Playlist playlist = new Playlist();
        playlist.setName(name);
        return playlist;
    }
}
