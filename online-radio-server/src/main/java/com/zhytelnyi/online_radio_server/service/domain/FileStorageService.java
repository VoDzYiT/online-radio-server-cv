package com.zhytelnyi.online_radio_server.service.domain;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.io.File;
@Service
public class FileStorageService {

    private static final Path MUSIC_UPLOAD_DIR = Paths.get(System.getProperty("user.home"), "radio-hls-static", "music");
    private static final List<Integer> TARGET_BITRATES = List.of(64000, 92000, 128000, 192000, 224000);

    public String storeFile(MultipartFile file) {
        try {
            if (!Files.exists(MUSIC_UPLOAD_DIR)) {
                Files.createDirectories(MUSIC_UPLOAD_DIR);
            }

            String originalName = file.getOriginalFilename();
            String uniqueFileName = System.currentTimeMillis() + "_" + originalName;

            Path targetPath = MUSIC_UPLOAD_DIR.resolve(uniqueFileName);

            file.transferTo(targetPath);

            CompletableFuture.runAsync(() -> convertToBitrates(targetPath.toFile()));

            return targetPath.toString();

        } catch (IOException e) {
            throw new RuntimeException("Cannot save file: " + file.getOriginalFilename(), e);
        }
    }

    private void convertToBitrates(File source) {
        String sourcePath = source.getAbsolutePath();

        for (Integer bitrate : TARGET_BITRATES) {
            try {
                String targetPath = sourcePath.replace(".mp3", "_" + (bitrate / 1000) + "k.mp3");
                File target = new File(targetPath);

                if (target.exists()) continue;

                AudioAttributes audio = new AudioAttributes();
                audio.setCodec("libmp3lame");
                audio.setBitRate(bitrate);
                audio.setChannels(2);
                audio.setSamplingRate(44100);

                EncodingAttributes attrs = new EncodingAttributes();
                attrs.setOutputFormat("mp3");
                attrs.setAudioAttributes(audio);

                Encoder encoder = new Encoder();
                encoder.encode(new MultimediaObject(source), target, attrs);

                System.out.println(">>> Conversion finished: " + target.getName());

            } catch (Exception e) {
                System.err.println("Conversion failed for " + bitrate + ": " + e.getMessage());
            }
        }
    }
}