package com.zhytelnyi.online_radio_server.service.core;

import com.zhytelnyi.online_radio_server.model.Station;
import com.zhytelnyi.online_radio_server.repository.ListenLogRepository;
import com.zhytelnyi.online_radio_server.repository.StationRepository;
import com.zhytelnyi.online_radio_server.service.core.hls.HlsFileManager;
import com.zhytelnyi.online_radio_server.service.core.hls.StreamProcessor;
import com.zhytelnyi.online_radio_server.service.factory.AudioFileFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class RadioServer {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private AudioFileFactory audioFileFactory;
    @Autowired
    private ListenLogRepository listenLogRepository;
    @Autowired
    private HlsFileManager hlsFileManager;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void startAllStreams() {
        System.out.println(">>> Application Ready. Starting RadioServer...");

        List<Station> stationsFromDb = stationRepository.findAll();
        for (Station station : stationsFromDb) {
            startChunking(station);
        }
    }

    public void startChunking(Station station) {
        System.out.println("Starting stream for: " + station.getName());

        StreamProcessor processor = new StreamProcessor(
                station,
                hlsFileManager,
                audioFileFactory,
                listenLogRepository
        );

        Thread thread = new Thread(processor);
        thread.setName("Stream-" + station.getName());
        thread.setDaemon(true);
        thread.start();
    }
}
