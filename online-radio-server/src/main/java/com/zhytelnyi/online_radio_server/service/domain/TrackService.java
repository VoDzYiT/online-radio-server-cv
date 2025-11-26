package com.zhytelnyi.online_radio_server.service.domain;

import com.zhytelnyi.online_radio_server.model.Playlist;
import com.zhytelnyi.online_radio_server.model.Track;
import com.zhytelnyi.online_radio_server.repository.ListenLogRepository;
import com.zhytelnyi.online_radio_server.repository.PlaylistRepository;
import com.zhytelnyi.online_radio_server.repository.TrackRepository;
import com.zhytelnyi.online_radio_server.service.domain.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
public class TrackService {

    private final TrackRepository trackRepository;
    private final FileStorageService fileStorageService;
    private final PlaylistRepository playlistRepository;
    private final ListenLogRepository listenLogRepository;

    public TrackService(TrackRepository trackRepository, FileStorageService fileStorageService, PlaylistRepository playlistRepository, ListenLogRepository listenLogRepository) {
        this.trackRepository = trackRepository;
        this.fileStorageService = fileStorageService;
        this.playlistRepository = playlistRepository;
        this.listenLogRepository = listenLogRepository;
    }

    public Track uploadTrack(String title, String artist, MultipartFile file) {
        String filePath = fileStorageService.storeFile(file);
        Track track = new Track(title, artist, filePath, 300); // 300 - це заглушка тривалості
        return trackRepository.save(track);
    }

    public List<Track> findAll() { return trackRepository.findAll(); }

    public List<Track> findAllByIds(List<Long> ids) { return trackRepository.findAllById(ids); }

    public Track findById(Long id) {
        return trackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Track not found with id: " + id));
    }

    @Transactional
    public void delete(Long id) {
        Track track = findById(id);

        for (Playlist playlist : track.getPlaylists()) {
            playlist.getTracks().remove(track);
            playlistRepository.save(playlist);
        }

        trackRepository.deleteById(id);

        listenLogRepository.detachTrackFromLogs(id);

        trackRepository.delete(track);
    }
}