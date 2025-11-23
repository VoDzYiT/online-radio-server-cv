package com.zhytelnyi.online_radio_server.service;

import com.mpatric.mp3agic.Mp3File;
import com.zhytelnyi.online_radio_server.model.ListenLog;
import com.zhytelnyi.online_radio_server.model.Playlist;
import com.zhytelnyi.online_radio_server.model.Station;
import com.zhytelnyi.online_radio_server.model.Track;
import com.zhytelnyi.online_radio_server.repository.ListenLogRepository;
import com.zhytelnyi.online_radio_server.repository.StationRepository;
import com.zhytelnyi.online_radio_server.service.iterator.TrackCollection;
import com.zhytelnyi.online_radio_server.service.iterator.TrackIterator;
import com.zhytelnyi.online_radio_server.service.factory.AudioFileFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLOutput;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.nio.file.Paths;
@Service
public class RadioServer {

    @Autowired private StationRepository stationRepository;
    @Autowired private StatisticsService statisticsService;
    @Autowired private AudioFileFactory audioFileFactory;
    @Autowired private ListenLogRepository listenLogRepository;
    private final Map<Long, Station> activeStations = new ConcurrentHashMap<>();
    private final Map<Long, Track> currentTrack = new ConcurrentHashMap<>();



    private static final int CHUNK_DURATION_SECONDS = 5;
    private static final int HLS_LIST_SIZE = 3;
    private static final Path HLS_BASE_PATH =
            Paths.get(System.getProperty("user.home"), "radio-hls-static", "hls");

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void startAllStreams() throws Exception {
        System.out.println("Application is Ready. Starting RadioServer...");
        Files.createDirectories(HLS_BASE_PATH);

        List<Station> stationsFromDb = stationRepository.findAll();
        for (Station station : stationsFromDb) {
            System.out.println("Starting HLS chunker for: " + station.getName());
            startChunking(station);
        }
    }

    public void startChunking(Station station) {

        final Long stationId = station.getId();
        final int stationBitrate = station.getBitrate();
        Path stationHlsPath = HLS_BASE_PATH.resolve(String.valueOf(stationId));
        try {
            Files.createDirectories(stationHlsPath);

            Files.list(stationHlsPath).forEach(file -> {
                try { Files.delete(file); } catch (Exception e) {}
            });
        } catch (IOException e) {
            throw new RuntimeException("Error when creating HLS directory", e);
        }

        Thread chunkerThread = new Thread(() -> {
            try {
                int chunkCounter = 0;
                LinkedList<String> manifestChunks = new LinkedList<>();
                TrackIterator trackIterator = station.getPlaylists().get(0).createIterator();


                while (true) {
                    Track track = trackIterator.next();
                    System.out.println("[" + station.getName() + "] Processing: " + track.getTitle());


                    try {
                        ListenLog log = new ListenLog(track, station);
                        listenLogRepository.save(log);
                    } catch (Exception e) {
                        System.err.println("Statistics Error: Could not save log: " + e.getMessage());
                    }

                    File trackFile = audioFileFactory.getAudioFile(track, stationBitrate);

                    if (!trackFile.exists()) {
                        System.err.println("File not found: " + track.getFilePath());
                        Thread.sleep(1000);
                        continue;
                    }

                    // Штука для отримання метаданих
                    Mp3File mp3 = new Mp3File(trackFile);

                    // Зріз метаданих
                    long dataOffset = mp3.hasId3v2Tag() ? mp3.getId3v2Tag().getLength() : 0;
                    long dataLength = trackFile.length() - dataOffset;


                    long bytesPerSec = (long) (dataLength / mp3.getLengthInSeconds());
                    long chunkByteSize = bytesPerSec * CHUNK_DURATION_SECONDS;

                    try (RandomAccessFile fileReader = new RandomAccessFile(trackFile, "r")) {
                        // Пропуск тегів
                        fileReader.seek(dataOffset);

                        long totalBytesRead = 0;
                        while (totalBytesRead < dataLength) {
                            String chunkName = String.format("chunk-%05d.mp3", chunkCounter++);
                            Path chunkPath = stationHlsPath.resolve(chunkName);

                            long bytesToRead = Math.min(chunkByteSize, dataLength - totalBytesRead);
                            byte[] buffer = new byte[(int) bytesToRead];
                            int read = fileReader.read(buffer);

                            if (read <= 0) break;

                            try (FileOutputStream fos = new FileOutputStream(chunkPath.toFile())) {
                                fos.write(buffer, 0, read);
                            }

                            manifestChunks.add(chunkName);
                            if (manifestChunks.size() > HLS_LIST_SIZE) {
                                String oldChunkName = manifestChunks.removeFirst();
                                Files.deleteIfExists(stationHlsPath.resolve(oldChunkName));
                            }

                            writeManifest(stationHlsPath, chunkCounter, manifestChunks);

                            totalBytesRead += read;

                            Thread.sleep(CHUNK_DURATION_SECONDS * 1000);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("HLS Chunker thread crashed: " + e.getMessage());
            }
        });
        chunkerThread.setName("Chunker-" + station.getName());
        chunkerThread.setDaemon(true);
        chunkerThread.start();
    }

    private void writeManifest(Path stationPath, int sequence, LinkedList<String> chunks) throws IOException {
        Path tempManifest = stationPath.resolve("stream.m3u8.tmp");

        try (PrintWriter writer = new PrintWriter(tempManifest.toFile())) {
            writer.println("#EXTM3U");
            writer.println("#EXT-X-VERSION:3");
            writer.println("#EXT-X-TARGETDURATION:" + (CHUNK_DURATION_SECONDS + 1));
            writer.println("#EXT-X-MEDIA-SEQUENCE:" + Math.max(0, sequence - chunks.size()));

            for (String chunkName : chunks) {
                writer.println("#EXTINF:" + CHUNK_DURATION_SECONDS + ".000,");
                writer.println(chunkName);
            }
        }

        Files.move(tempManifest, stationPath.resolve("stream.m3u8"), StandardCopyOption.REPLACE_EXISTING);
    }



    public void stopStreaming(Station station) { /* ... */ }

    public StatisticsService getStatistics() {
        return this.statisticsService;
    }

    public Track getTrackToStream(Long stationId) {
        return currentTrack.get(stationId);
    }

    public Station getStation(Long stationId) {
        return activeStations.get(stationId);
    }

    public List<Station> getStations() {
        return stationRepository.findAll();
    }




}
