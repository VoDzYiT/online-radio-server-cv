package com.zhytelnyi.online_radio_server.service.facade;

import com.zhytelnyi.online_radio_server.model.*;
import com.zhytelnyi.online_radio_server.repository.FavoriteRepository;
import com.zhytelnyi.online_radio_server.repository.RecordingRepository;
import com.zhytelnyi.online_radio_server.repository.StationRepository;
import com.zhytelnyi.online_radio_server.service.RadioServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
public class RadioFacade {

    @Autowired private RadioServer radioServer;
    @Autowired private FavoriteRepository favoriteRepository;
    @Autowired private RecordingRepository recordingRepository;
    @Autowired private StationRepository stationRepository;



    public Mono<List<Station>> getAllStations() {
        return Mono.fromCallable(() -> stationRepository.findAll())
                .subscribeOn(Schedulers.boundedElastic());
    }


    public void addToFavorites(User user, Station station) {
        Favorite fav = new Favorite(user, station);
        favoriteRepository.save(fav);
    }

    public void startRecording(User user, Station station) {
        Recording rec = new Recording(/* ... */);
        recordingRepository.save(rec);
        System.out.println("User " + user.getUsername() + " started recording " + station.getName());
    }

    public void stopRecording(User user, Recording recording) {
        System.out.println("User " + user.getUsername() + " stopped recording.");
    }


}