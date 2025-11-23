package com.zhytelnyi.online_radio_server.controller;

import com.zhytelnyi.online_radio_server.model.Station;
import com.zhytelnyi.online_radio_server.repository.ConnectionLogRepository;
import com.zhytelnyi.online_radio_server.repository.StationRepository;
import com.zhytelnyi.online_radio_server.service.facade.RadioFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class RadioController {

    private final RadioFacade radioFacade;

    @Autowired private ConnectionLogRepository connectionLogRepository;
    @Autowired private StationRepository stationRepository;

    @Autowired
    public RadioController(RadioFacade radioFacade) {
        this.radioFacade = radioFacade;
    }

    @GetMapping("/stations")
    public List<Station> getAllStations() {
        return radioFacade.getAllStations();
    }

    @GetMapping("/hls/{stationId}/stream.m3u8")
    public ResponseEntity<Resource> getHlsManifest(@PathVariable String stationId) {
        Path path = Paths.get(System.getProperty("user.home"), "radio-hls-static", "hls", stationId, "stream.m3u8");
        Resource resource = new FileSystemResource(path);

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .contentType(MediaType.parseMediaType("application/vnd.apple.mpegurl"))
                .body(resource);
    }

    @PostMapping("/api/v1/favorites")
    public ResponseEntity<String> addToFavorites(
            @RequestParam Long stationId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // Перевірка авторизації
        if (userDetails == null) {
            return ResponseEntity.status(401).body("You must contain login first");
        }

        try {
            String message = radioFacade.addStationToFavorites(userDetails.getUsername(), stationId);

            return ResponseEntity.ok(message);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/api/v1/station/{id}/report")
    public String getStationReport(@PathVariable Long id) {
        return radioFacade.getStationReport(id);
    }
}