package com.zhytelnyi.online_radio_server.service.factory;
import org.springframework.stereotype.Component;
import com.zhytelnyi.online_radio_server.model.Track;

import java.io.File;

@Component
public class AudioFileFactory {
    public File getAudioFile(Track track, int bitrate) {
        String basePath = track.getFilePath();

        String newPath = basePath.replace(".mp3", "_" + bitrate + "k.mp3");

        File bitrateFile = new File(newPath);

        if (bitrateFile.exists() && bitrateFile.canRead()) {
            return bitrateFile;
        }

        System.out.println(">>> Factory: Bitrate file not found: " + basePath);
        File defaultFile = new File(basePath);
        if (defaultFile.exists() && defaultFile.canRead()) {
            return defaultFile;
        }

        System.err.println(">>> Factory: No files found for track: " + basePath);
        return null;
    }
}