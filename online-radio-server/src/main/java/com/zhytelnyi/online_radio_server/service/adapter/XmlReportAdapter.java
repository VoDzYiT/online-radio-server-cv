package com.zhytelnyi.online_radio_server.service.adapter;

import com.zhytelnyi.online_radio_server.model.Station;
import com.zhytelnyi.online_radio_server.service.visitor.ContentStatistics;
import org.springframework.stereotype.Component;

@Component("xmlExporter")
public class XmlReportAdapter implements IReportExporter{
    @Override
    public String export(Station station) {
        // 1. Збираємо дані (так само, як і в JSON адаптері)
        ContentStatistics visitor = new ContentStatistics();
        station.accept(visitor);

        // 2. АДАПТУЄМО дані у формат XML
        return String.format(
                "<StationReport>\n" +
                        "  <Name>%s</Name>\n" +
                        "  <Statistics>\n" +
                        "    <TotalTracks>%d</TotalTracks>\n" +
                        "    <TotalPlaylists>%d</TotalPlaylists>\n" +
                        "    <DurationSeconds>%d</DurationSeconds>\n" +
                        "  </Statistics>\n" +
                        "  <Status>Active</Status>\n" +
                        "</StationReport>",
                visitor.getStationName(),
                visitor.getTotalTracks(),
                visitor.getPlaylistCount(),
                visitor.getTotalDurationSeconds()
        );
    }
}
