package com.zhytelnyi.online_radio_server.service.storage; // Або ваш пакет

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    private static final Path MUSIC_UPLOAD_DIR = Paths.get(System.getProperty("user.home"), "radio-hls-static", "music");

    public String storeFile(MultipartFile file) {
        try {
            if (!Files.exists(MUSIC_UPLOAD_DIR)) {
                Files.createDirectories(MUSIC_UPLOAD_DIR);
            }

            String originalName = file.getOriginalFilename();
            String uniqueFileName = System.currentTimeMillis() + "_" + originalName;

            Path targetPath = MUSIC_UPLOAD_DIR.resolve(uniqueFileName);

            file.transferTo(targetPath);

            return targetPath.toString();

        } catch (IOException e) {
            throw new RuntimeException("Cannot save file: " + file.getOriginalFilename(), e);
        }
    }
}