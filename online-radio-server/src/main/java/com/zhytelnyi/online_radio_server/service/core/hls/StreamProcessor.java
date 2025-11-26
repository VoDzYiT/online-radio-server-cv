package com.zhytelnyi.online_radio_server.service.core.hls;

import com.mpatric.mp3agic.Mp3File;
import com.zhytelnyi.online_radio_server.model.ListenLog;
import com.zhytelnyi.online_radio_server.model.Station;
import com.zhytelnyi.online_radio_server.model.Track;
import com.zhytelnyi.online_radio_server.repository.ListenLogRepository;
import com.zhytelnyi.online_radio_server.service.factory.AudioFileFactory;
import com.zhytelnyi.online_radio_server.service.iterator.TrackIterator;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.LinkedList;

public class StreamProcessor implements Runnable {

    private final Station station;
    private final HlsFileManager fileManager;
    private final AudioFileFactory audioFileFactory;
    private final ListenLogRepository listenLogRepository;

    private static final int CHUNK_DURATION = 5;
    private static final int LIST_SIZE = 3;
    private int chunkCounter = 0;

    public StreamProcessor(Station station,
                           HlsFileManager fileManager,
                           AudioFileFactory audioFileFactory,
                           ListenLogRepository listenLogRepository) {
        this.station = station;
        this.fileManager = fileManager;
        this.audioFileFactory = audioFileFactory;
        this.listenLogRepository = listenLogRepository;
    }

    @Override
    public void run() {
        try {
            Path stationPath = fileManager.prepareStationDirectory(station.getId());

            LinkedList<String> manifestChunks = new LinkedList<>();
            TrackIterator trackIterator = station.getPlaylists().get(0).createIterator();

            while (true) {
                Track track = trackIterator.next();
                System.out.println("[" + station.getName() + "] Processing: " + track.getTitle());

                logStatistic(track);

                File trackFile = audioFileFactory.getAudioFile(track, station.getBitrate());
                if (trackFile == null || !trackFile.exists()) {
                    Thread.sleep(1000);
                    continue;
                }

                processTrackFile(trackFile, stationPath, manifestChunks);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processTrackFile(File file, Path stationPath, LinkedList<String> chunks) throws Exception {
        Mp3File mp3 = new Mp3File(file);
        long dataOffset = mp3.hasId3v2Tag() ? mp3.getId3v2Tag().getLength() : 0;
        long dataLength = file.length() - dataOffset;
        long bytesPerSec = (long) (dataLength / mp3.getLengthInSeconds());
        long chunkByteSize = bytesPerSec * CHUNK_DURATION;

        try (RandomAccessFile fileReader = new RandomAccessFile(file, "r")) {
            fileReader.seek(dataOffset);
            long totalBytesRead = 0;

            while (totalBytesRead < dataLength) {
                String chunkName = String.format("chunk-%05d.mp3", chunkCounter++);
                Path chunkPath = stationPath.resolve(chunkName);

                long bytesToRead = Math.min(chunkByteSize, dataLength - totalBytesRead);
                byte[] buffer = new byte[(int) bytesToRead];
                int read = fileReader.read(buffer);

                if (read <= 0) break;

                fileManager.writeChunk(chunkPath, buffer, read);

                chunks.add(chunkName);
                if (chunks.size() > LIST_SIZE) {
                    fileManager.deleteOldChunk(stationPath, chunks.removeFirst());
                }

                fileManager.updateManifest(stationPath, chunkCounter, chunks, CHUNK_DURATION);

                totalBytesRead += read;
                Thread.sleep(CHUNK_DURATION * 1000);
            }
        }
    }

    private void logStatistic(Track track) {
        try {
            listenLogRepository.save(new ListenLog(track, station));
        } catch (Exception e) {
            System.err.println("Log Error: " + e.getMessage());
        }
    }
}