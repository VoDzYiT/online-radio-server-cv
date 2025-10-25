package com.zhytelnyi.online_radio_server.service.factory;
import org.springframework.stereotype.Component;
import com.zhytelnyi.online_radio_server.model.Track;

@Component
public class TrackFactory {
    public Track createTrack(String title, String artist, String path) {
        return new Track(title, artist, path);
    }
}
