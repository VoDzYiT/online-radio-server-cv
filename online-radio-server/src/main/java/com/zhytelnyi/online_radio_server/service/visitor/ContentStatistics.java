package com.zhytelnyi.online_radio_server.service.visitor;

import com.zhytelnyi.online_radio_server.model.Playlist;
import com.zhytelnyi.online_radio_server.model.Station;
import com.zhytelnyi.online_radio_server.model.Track;
import com.zhytelnyi.online_radio_server.model.User;

public class ContentStatistics implements Visitor{



    private int totalTracks = 0;
    private long totalDurationSeconds = 0;
    private int playlistCount = 0;
    private String stationName = "";

    @Override
    public void visit(Station station) {
        this.stationName = station.getName();
    }

    @Override
    public void visit(User user) {

    }

    @Override
    public void visit(Track track) {
        totalTracks++;
        totalDurationSeconds += track.getDurationInSeconds();
    }

    @Override
    public void visit(Playlist playlist) {
        playlistCount++;
    }

    public int getTotalTracks() {
        return totalTracks;
    }

    public long getTotalDurationSeconds() {
        return totalDurationSeconds;
    }

    public int getPlaylistCount() {
        return playlistCount;
    }

    public String getStationName() {
        return stationName;
    }
}
