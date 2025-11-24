package com.zhytelnyi.online_radio_server.service.domain;

import com.zhytelnyi.online_radio_server.model.Track;
import com.zhytelnyi.online_radio_server.repository.TrackRepository;
import com.zhytelnyi.online_radio_server.service.storage.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
public class TrackService {

    private final TrackRepository trackRepository;
    private final FileStorageService fileStorageService;

    public TrackService(TrackRepository trackRepository, FileStorageService fileStorageService) {
        this.trackRepository = trackRepository;
        this.fileStorageService = fileStorageService;
    }

    public Track uploadTrack(String title, String artist, MultipartFile file) {
        String filePath = fileStorageService.storeFile(file);
        Track track = new Track(title, artist, filePath, 300); // 300 - це заглушка тривалості
        return trackRepository.save(track);
    }

    public List<Track> findAll() { return trackRepository.findAll(); }

    public List<Track> findAllByIds(List<Long> ids) { return trackRepository.findAllById(ids); }

    public void delete(Long id) { trackRepository.deleteById(id); }
}