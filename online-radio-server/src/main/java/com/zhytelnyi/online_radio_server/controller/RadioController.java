package com.zhytelnyi.online_radio_server.controller;
import com.zhytelnyi.online_radio_server.model.ConnectionLog;
import com.zhytelnyi.online_radio_server.repository.ConnectionLogRepository;
import com.zhytelnyi.online_radio_server.repository.StationRepository;
import com.zhytelnyi.online_radio_server.service.RadioServer;
import com.zhytelnyi.online_radio_server.model.Station;
import com.zhytelnyi.online_radio_server.service.facade.RadioFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class RadioController {

    private final RadioFacade radioFacade;

    @Autowired private ConnectionLogRepository connectionLogRepository;
    @Autowired private StationRepository stationRepository;
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
    public Mono<ResponseEntity<Resource>> getHlsManifest(@PathVariable String stationId, ServerHttpRequest request) {

        Mono.fromRunnable(() -> {
                    try {
                        Long sId = Long.parseLong(stationId);
                        Station station = stationRepository.findById(sId).orElse(null);

                        if (station != null) {
                            String ip = request.getRemoteAddress() != null
                                    ? request.getRemoteAddress().getAddress().toString()
                                    : "unknown";
                            String userAgent = request.getHeaders().getFirst("User-Agent");

                            ConnectionLog log = new ConnectionLog(station, ip, userAgent);
                            connectionLogRepository.save(log);

                        }
                    } catch (Exception e) {
                        System.err.println("Log error: " + e.getMessage());
                    }
                })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();


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

    @PostMapping("/api/v1/favorites")
    public Mono<ResponseEntity<String>> addToFavorites(
            @RequestParam Long stationId,
            @AuthenticationPrincipal UserDetails userDetails // <-- Ось тут Spring дасть нам юзера
    ) {
        // Якщо користувач не залогінений, userDetails буде null
        if (userDetails == null) {
            return Mono.just(ResponseEntity.status(401).body("You must contain login first"));
        }

        return radioFacade.addStationToFavorites(userDetails.getUsername(), stationId)
                .map(message -> ResponseEntity.ok(message))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @GetMapping("/api/v1/station/{id}/report")
    @ResponseBody
    public Mono<String> getStationReport(@PathVariable Long id) {
        return radioFacade.getStationReport(id);
    }

}
