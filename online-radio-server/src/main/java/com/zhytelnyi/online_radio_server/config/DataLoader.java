package com.zhytelnyi.online_radio_server.config;

import com.zhytelnyi.online_radio_server.model.Playlist;
import com.zhytelnyi.online_radio_server.model.Station;
import com.zhytelnyi.online_radio_server.model.Track;
//import com.zhytelnyi.online_radio_server.repository.PlaylistRepository;
import com.zhytelnyi.online_radio_server.repository.StationRepository;
//import com.zhytelnyi.online_radio_server.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(1)
public class DataLoader implements CommandLineRunner {

    @Autowired
    private StationRepository stationRepository;


    @Override
    public void run(String... args) throws Exception {

        if (stationRepository.count() == 0) {
            System.out.println("The database is empty. Loading test data...");

            // Треки
            Track track1 = new Track("Bohemian Rhapsody", "Queen", "online-radio-server/src/main/resources/music/Queen-Bohemian-Rhaopsody-Clean-V.mp3", 21);
            Track track2 = new Track("Hotel California", "The eagles", "online-radio-server/src/main/resources/music/hotel_california.mp3", 18);
            Track track3 = new Track("Thunderstruck", "AC/DC", "online-radio-server/src/main/resources/music/thunderstruck.mp3", 19);

            Track track4 = new Track("Fur Elise", "Beethoven", "online-radio-server/src/main/resources/music/fur-elise-and-orchestra-110149.mp3", 21);
            Track track5 = new Track("Symphony No. 40", "Mozart", "online-radio-server/src/main/resources/music/SymphonyNo40InGMinor.mp3", 19);


            // Плейлисти
            Playlist rockPlaylist = new Playlist("Classic Rock");
            rockPlaylist.addTrack(track1);
            rockPlaylist.addTrack(track2);
            rockPlaylist.addTrack(track3);

            Playlist classicPlaylist = new Playlist("Classic Music");
            classicPlaylist.addTrack(track4);
            classicPlaylist.addTrack(track5);


            Station classicRockStation = new Station();
            classicRockStation.setName("Classic Rock Radio");
            classicRockStation.setPlaylists(List.of(rockPlaylist));

            Station classicMusicStation = new Station();
            classicMusicStation.setName("Classic Music Radio");
            classicMusicStation.setPlaylists(List.of(classicPlaylist));


            stationRepository.save(classicRockStation);
            stationRepository.save(classicMusicStation);



            System.out.println("Test data successfully uploaded.");
        } else {
            System.out.println("The database already contains data. Download skipped.");
        }
    }
}
