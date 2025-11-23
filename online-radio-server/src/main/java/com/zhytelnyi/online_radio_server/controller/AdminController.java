package com.zhytelnyi.online_radio_server.controller;

import com.zhytelnyi.online_radio_server.service.facade.RadioFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private RadioFacade radioFacade;

    /**
     * Головна сторінка адмінки.
     * Завантажуємо всі списки, щоб відобразити їх у таблицях і селектах.
     */
    @GetMapping
    public Mono<String> adminPage(Model model) {
        return Mono.zip(
                radioFacade.getAllStations(),
                radioFacade.getAllPlaylists(),
                radioFacade.getAllTracks()
        ).doOnNext(tuple -> {
            model.addAttribute("stations", tuple.getT1());
            model.addAttribute("playlists", tuple.getT2());
            model.addAttribute("tracks", tuple.getT3());
        }).thenReturn("admin");
    }

    // --- TRACKS ---
    @PostMapping("/tracks/add")
    public Mono<String> addTrack(@RequestPart String title,
                                 @RequestPart String artist,
                                 @RequestPart("file") FilePart file) {
        return radioFacade.uploadTrack(title, artist, file)
                .thenReturn("redirect:/admin");
    }

    @PostMapping("/tracks/delete/{id}")
    public Mono<String> deleteTrack(@PathVariable Long id) {
        return radioFacade.deleteTrack(id).thenReturn("redirect:/admin");
    }

    // --- PLAYLISTS ---
    @PostMapping("/playlists/add")
    public Mono<String> addPlaylist(@RequestParam String name,
                                    @RequestParam(required = false) List<Long> trackIds) {
        return radioFacade.createPlaylist(name, trackIds)
                .thenReturn("redirect:/admin");
    }

    @PostMapping("/playlists/delete/{id}")
    public Mono<String> deletePlaylist(@PathVariable Long id) {
        return radioFacade.deletePlaylist(id).thenReturn("redirect:/admin");
    }

    // --- STATIONS ---
    @PostMapping("/stations/add")
    public Mono<String> addStation(@RequestParam String name,
                                   @RequestParam int bitrate,
                                   @RequestParam(required = false) List<Long> playlistIds) {
        return radioFacade.createStation(name, bitrate, playlistIds)
                .thenReturn("redirect:/admin");
    }

    @PostMapping("/stations/delete/{id}")
    public Mono<String> deleteStation(@PathVariable Long id) {
        return radioFacade.deleteStation(id).thenReturn("redirect:/admin");
    }
}