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

            Track track1 = new Track("Music1", "Pixabay", "src/main/resources/music/music1.mp3", 111);
            Track track2 = new Track("Music2", "Pixabay", "src/main/resources/music/music2.mp3", 24);
            Track track3 = new Track("Music3", "Pixabay", "src/main/resources/music/music3.mp3", 39);



            trackRepository.saveAll(List.of(track1, track2, track3));

            Playlist music1_2playlist = playlistFactory.createPlaylist("Music1_2 Playlist");
            music1_2playlist.addTrack(track1);
            music1_2playlist.addTrack(track2);


            Playlist music3playlist = playlistFactory.createPlaylist("Music3 Playlist");
            music3playlist.addTrack(track3);


            Station music1_2station = new Station();
            music1_2station.setName("Station For music 1 2");
            music1_2station.setPlaylists(List.of(music1_2playlist));
            music1_2station.setBitrate(224);

            Station musci3station = new Station();
            musci3station.setName("64b Station For music 3");
            musci3station.setPlaylists(List.of(music3playlist));
            musci3station.setBitrate(64);




            stationRepository.save(music1_2station);
            stationRepository.save(musci3station);

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
