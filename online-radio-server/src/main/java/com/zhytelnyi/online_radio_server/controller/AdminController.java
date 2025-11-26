package com.zhytelnyi.online_radio_server.controller;

import com.zhytelnyi.online_radio_server.model.Playlist;
import com.zhytelnyi.online_radio_server.service.facade.RadioFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private RadioFacade radioFacade;

    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("stations", radioFacade.getAllStations());
        model.addAttribute("playlists", radioFacade.getAllPlaylists());
        model.addAttribute("tracks", radioFacade.getAllTracks());
        return "admin";
    }

    @PostMapping("/tracks/add")
    public String addTrack(@RequestParam String title,
                           @RequestParam String artist,
                           @RequestParam("file") MultipartFile file) {
        radioFacade.uploadTrack(title, artist, file);
        return "redirect:/admin";
    }

    @PostMapping("/tracks/delete/{id}")
    public String deleteTrack(@PathVariable Long id) {
        radioFacade.deleteTrack(id);

        return "redirect:/admin";
    }

    @PostMapping("/playlists/add")
    public String addPlaylist(@RequestParam String name,
                              @RequestParam(required = false) List<Long> trackIds) {
        radioFacade.createPlaylist(name, trackIds);
        return "redirect:/admin";
    }

    @PostMapping("/playlists/delete/{id}")
    public String deletePlaylist(@PathVariable Long id) {
        radioFacade.deletePlaylist(id);
        return "redirect:/admin";
    }

    @PostMapping("/stations/add")
    public String addStation(@RequestParam String name,
                             @RequestParam int bitrate,
                             @RequestParam(required = false) List<Long> playlistIds) {
        radioFacade.createStation(name, bitrate, playlistIds);
        return "redirect:/admin";
    }

    @PostMapping("/stations/delete/{id}")
    public String deleteStation(@PathVariable Long id) {
        radioFacade.deleteStation(id);
        return "redirect:/admin";
    }

    @GetMapping("/playlists/edit/{id}")
    public String editPlaylistPage(@PathVariable Long id, Model model) {
        Playlist playlist = radioFacade.getPlaylistById(id);

        model.addAttribute("playlist", playlist);
        model.addAttribute("allTracks", radioFacade.getAllTracks());

        return "edit_playlist";
    }

    @PostMapping("/playlists/update")
    public String updatePlaylist(
            @RequestParam Long id,
            @RequestParam String name,
            @RequestParam(required = false) List<Long> trackIds
    ) {
        radioFacade.updatePlaylist(id, name, trackIds);
        return "redirect:/admin";
    }

    @GetMapping("/statistics")
    public String statisticsPage(Model model) {
        model.addAttribute("listenLogs", radioFacade.getRecentListenLogs());
        model.addAttribute("connectionLogs", radioFacade.getRecentConnectionLogs());
        return "admin_statistics";
    }
}