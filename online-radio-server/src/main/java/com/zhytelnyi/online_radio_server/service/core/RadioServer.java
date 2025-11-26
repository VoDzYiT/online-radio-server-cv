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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class RadioServer {

    private final StationRepository stationRepository;
    private final AudioFileFactory audioFileFactory;
    private final ListenLogRepository listenLogRepository;
    private final HlsFileManager hlsFileManager;

    public RadioServer(StationRepository stationRepository,
                       AudioFileFactory audioFileFactory,
                       ListenLogRepository listenLogRepository,
                       HlsFileManager hlsFileManager) {
        this.stationRepository = stationRepository;
        this.audioFileFactory = audioFileFactory;
        this.listenLogRepository = listenLogRepository;
        this.hlsFileManager = hlsFileManager;
    }

    private final Map<Long, Thread> streamThreads = new ConcurrentHashMap<>();

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
        stopStreaming(station.getId());
        System.out.println(">>> Starting stream for: " + station.getName());

        StreamProcessor processor = new StreamProcessor(
                station,
                hlsFileManager,
                audioFileFactory,
                listenLogRepository
        );

        Thread thread = new Thread(processor);
        thread.setName(">>> Stream-" + station.getName());
        thread.setDaemon(true);
        thread.start();

        streamThreads.put(station.getId(), thread);
    }

    public void stopStreaming(Long stationId) {
        Thread thread = streamThreads.get(stationId);
        if (thread != null) {
            System.out.println(">>> Stopping stream for station ID: " + stationId);
            thread.interrupt();
            streamThreads.remove(stationId);
        }
    }

    public void restartChunking(Station station) {
        System.out.println(">>> Restarting stream for updated station: " + station.getName());

        stopStreaming(station.getId());

        startChunking(station);
    }
}
