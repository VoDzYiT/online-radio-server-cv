package com.zhytelnyi.online_radio_server.config;

import com.zhytelnyi.online_radio_server.model.Playlist;
import com.zhytelnyi.online_radio_server.model.Station;
import com.zhytelnyi.online_radio_server.model.Track;
//import com.zhytelnyi.online_radio_server.repository.PlaylistRepository;
import com.zhytelnyi.online_radio_server.model.User;
import com.zhytelnyi.online_radio_server.repository.StationRepository;
//import com.zhytelnyi.online_radio_server.repository.TrackRepository;
import com.zhytelnyi.online_radio_server.repository.TrackRepository;
import com.zhytelnyi.online_radio_server.repository.UserRepository;
import com.zhytelnyi.online_radio_server.service.factory.PlaylistFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(1)
public class DataLoader implements CommandLineRunner {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private TrackRepository trackRepository;
    @Autowired
    private PlaylistFactory playlistFactory;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


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

            trackRepository.saveAll(List.of(track1, track2, track3, track4, track5));

            // Плейлисти
            Playlist rockPlaylist = playlistFactory.createPlaylist("Classic Rock");
            rockPlaylist.addTrack(track1);
            rockPlaylist.addTrack(track2);
            rockPlaylist.addTrack(track3);

            Playlist classicPlaylist = playlistFactory.createPlaylist("Classic Music");
            classicPlaylist.addTrack(track4);
            classicPlaylist.addTrack(track5);

            Playlist classicPlaylistLowQuality = playlistFactory.createPlaylist("Classic Music Low Quality");
            classicPlaylistLowQuality.addTrack(track4);
            classicPlaylistLowQuality.addTrack(track5);


            Station classicRockStation = new Station();
            classicRockStation.setName("Classic Rock Radio");
            classicRockStation.setPlaylists(List.of(rockPlaylist));
            classicRockStation.setBitrate(128);

            Station classicMusicStation = new Station();
            classicMusicStation.setName("Classic Music Radio");
            classicMusicStation.setPlaylists(List.of(classicPlaylist));
            classicMusicStation.setBitrate(128);

            Station classicMusicStationLowQuality = new Station();
            classicMusicStationLowQuality.setName("Classic Music Radio low quality");
            classicMusicStationLowQuality.setPlaylists(List.of(classicPlaylistLowQuality));
            classicMusicStationLowQuality.setBitrate(64);


            stationRepository.save(classicRockStation);
            stationRepository.save(classicMusicStation);
            stationRepository.save(classicMusicStationLowQuality);

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole("ROLE_ADMIN");
            userRepository.save(admin);




            System.out.println("Test data successfully uploaded.");
        } else {
            System.out.println("The database already contains data. Download skipped.");
        }
    }
}
