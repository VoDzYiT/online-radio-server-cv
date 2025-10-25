package com.zhytelnyi.online_radio_server.service.factory;

import com.zhytelnyi.online_radio_server.model.Playlist;
import org.springframework.stereotype.Component;

@Component
public class PlaylistFactory {
    public Playlist createPlaylist(String name) {
        return new Playlist(name);
    }
}
