package com.zhytelnyi.online_radio_server.service.core.hls;

import org.springframework.stereotype.Component;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;

@Component
public class HlsFileManager {

    private static final Path HLS_BASE_PATH = Paths.get(System.getProperty("user.home"), "radio-hls-static", "hls");

    public Path prepareStationDirectory(Long stationId) {
        Path stationPath = HLS_BASE_PATH.resolve(String.valueOf(stationId));
        try {
            Files.createDirectories(stationPath);
            Files.list(stationPath).forEach(file -> {
                try { Files.delete(file); } catch (Exception e) {}
            });
            return stationPath;
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize HLS directory", e);
        }
    }

    public void writeChunk(Path chunkPath, byte[] buffer, int read) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(chunkPath.toFile())) {
            fos.write(buffer, 0, read);
        }
    }

    public void updateManifest(Path stationPath, int sequence, LinkedList<String> chunks, int duration) throws IOException {
        Path tempManifest = stationPath.resolve("stream.m3u8.tmp");

        try (PrintWriter writer = new PrintWriter(tempManifest.toFile())) {
            writer.println("#EXTM3U");
            writer.println("#EXT-X-VERSION:3");
            writer.println("#EXT-X-TARGETDURATION:" + (duration + 1));
            writer.println("#EXT-X-MEDIA-SEQUENCE:" + Math.max(0, sequence - chunks.size()));

            for (String chunkName : chunks) {
                writer.println("#EXTINF:" + duration + ".000,");
                writer.println(chunkName);
            }
        }
        Files.move(tempManifest, stationPath.resolve("stream.m3u8"), StandardCopyOption.REPLACE_EXISTING);
    }

    public void deleteOldChunk(Path stationPath, String chunkName) {
        try {
            Files.deleteIfExists(stationPath.resolve(chunkName));
        } catch (IOException e) {
        }
    }
}