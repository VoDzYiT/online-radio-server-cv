package com.zhytelnyi.online_radio_server.service.adapter;

import com.zhytelnyi.online_radio_server.model.Station;
import com.zhytelnyi.online_radio_server.service.visitor.ContentStatistics;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
@Primary
@Component("jsonExporter")
public class JsonReportAdapter implements IReportExporter {

    @Override
    public String export(Station station) {
        ContentStatistics visitor = new ContentStatistics();
        station.accept(visitor);

        // 2. Отримуємо сирі дані
        String name = visitor.getStationName();
        int tracks = visitor.getTotalTracks();
        int playlists = visitor.getPlaylistCount();
        long duration = visitor.getTotalDurationSeconds();

        return String.format(
                "{\n" +
                        "  \"station\": \"%s\",\n" +
                        "  \"statistics\": {\n" +
                        "    \"total_tracks\": %d,\n" +
                        "    \"total_playlists\": %d,\n" +
                        "    \"duration_seconds\": %d\n" +
                        "  },\n" +
                        "  \"status\": \"active\"\n" +
                        "}",
                name, tracks, playlists, duration
        );
    }
}