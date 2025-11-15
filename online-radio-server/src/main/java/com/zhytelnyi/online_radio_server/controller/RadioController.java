package com.zhytelnyi.online_radio_server.controller;
import com.zhytelnyi.online_radio_server.service.RadioServer;
import com.zhytelnyi.online_radio_server.model.Station;
import com.zhytelnyi.online_radio_server.service.facade.RadioFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class RadioController {

    private final RadioFacade radioFacade;
    private static final Path HLS_BASE_PATH =
            Paths.get(System.getProperty("user.home"), "radio-hls-static", "hls");
    @Autowired
    public RadioController(RadioFacade radioFacade) {
        this.radioFacade = radioFacade;
    }

    @GetMapping("/stations")
    public Mono<List<Station>> getAllStations() {
        return radioFacade.getAllStations();
    }


    @GetMapping("/hls/{stationId}/stream.m3u8")
    public Mono<ResponseEntity<Resource>> getHlsManifest(@PathVariable String stationId) {

        Path manifestPath = HLS_BASE_PATH.resolve(stationId).resolve("stream.m3u8");
        Resource resource = new FileSystemResource(manifestPath);

        if (!resource.exists()) {
            return Mono.just(ResponseEntity.notFound().build());
        }

//        CacheControl cacheControl = CacheControl.noStore()
//                .noCache()
//                .mustRevalidate();

        return Mono.just(ResponseEntity.ok()
//                .cacheControl(cacheControl)
                .contentType(MediaType.valueOf("application/vnd.apple.mpegurl"))
                .body(resource));
    }

    @PostMapping("/favorites")
    public ResponseEntity<String> addToFavorites(
            @RequestParam Long userId,
            @RequestParam Long stationId) {

        System.out.println("Adding station " + stationId + " to favorites for user " + userId);
        return ResponseEntity.ok("Added to favorites");
    }

}
