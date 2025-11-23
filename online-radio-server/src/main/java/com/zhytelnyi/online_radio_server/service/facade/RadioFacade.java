package com.zhytelnyi.online_radio_server.service.facade;

import com.zhytelnyi.online_radio_server.model.*;
import com.zhytelnyi.online_radio_server.repository.*;
import com.zhytelnyi.online_radio_server.service.RadioServer;
import com.zhytelnyi.online_radio_server.service.adapter.IReportExporter;
import com.zhytelnyi.online_radio_server.service.factory.PlaylistFactory;
import com.zhytelnyi.online_radio_server.service.visitor.ContentStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import org.springframework.http.codec.multipart.FilePart;

import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RadioFacade {

    @Autowired private RadioServer radioServer;
    @Autowired private FavoriteRepository favoriteRepository;
    @Autowired private RecordingRepository recordingRepository;
    @Autowired private StationRepository stationRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TrackRepository trackRepository;
    @Autowired private PlaylistRepository playlistRepository;
    @Autowired private PlaylistFactory playlistFactory;
    @Autowired
    @Qualifier("jsonExporter")
    private IReportExporter reportExporter;
    private static final Path MUSIC_UPLOAD_DIR = Paths.get(System.getProperty("user.home"), "radio-hls-static", "music");



    public Mono<List<Station>> getAllStations() {
        return Mono.fromCallable(() -> stationRepository.findAll())
                .subscribeOn(Schedulers.boundedElastic());
    }



    public Mono<String> addStationToFavorites(String username, Long stationId) {
        return Mono.fromCallable(() -> {
            // 1. Знаходимо Юзера
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 2. Знаходимо Станцію
            Station station = stationRepository.findById(stationId)
                    .orElseThrow(() -> new RuntimeException("Station not found"));

            // 3. Перевіряємо на дублікат
            if (favoriteRepository.existsByUserAndStation(user, station)) {
                return "Station is already in favorites!";
            }

            // 4. Зберігаємо
            Favorite favorite = new Favorite(user, station);
            favoriteRepository.save(favorite);

            return "Success: Added to favorites";
        }).subscribeOn(Schedulers.boundedElastic()); // Виконуємо в окремому потоці
    }

    public Mono<List<Station>> getUserFavoriteStations(String username) {
        return Mono.fromCallable(() -> {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 2. Знаходимо всі записи Favorite
            List<Favorite> favorites = favoriteRepository.findAllByUser(user);

            // 3. Перетворюємо List<Favorite> у List<Station>
            // (Нам потрібні самі станції, щоб показати їх назви і бітрейт)
            return favorites.stream()
                    .map(Favorite::getStation) // Беремо станцію з кожного запису
                    .collect(Collectors.toList());

        }).subscribeOn(Schedulers.boundedElastic());
    }

    public void startRecording(User user, Station station) {

    }

    public void stopRecording(User user, Recording recording) {
        System.out.println("User " + user.getUsername() + " stopped recording.");
    }

    public Mono<String> getStationReport(Long stationId) {
        return Mono.fromCallable(() -> {
            Station station = stationRepository.findById(stationId).orElse(null);
            if (station == null) return "{ \"error\": \"Not Found\" }";

            return reportExporter.export(station);

        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Track> uploadTrack(String title, String artist, FilePart filePart) {
        return Mono.fromCallable(() -> {
                    // Створюємо папку, якщо немає
                    if (!MUSIC_UPLOAD_DIR.toFile().exists()) {
                        MUSIC_UPLOAD_DIR.toFile().mkdirs();
                    }

                    // Генеруємо унікальне ім'я файлу (щоб не перезаписати)
                    String filename = System.currentTimeMillis() + "_" + filePart.filename();
                    Path targetPath = MUSIC_UPLOAD_DIR.resolve(filename);

                    return targetPath;
                }).subscribeOn(Schedulers.boundedElastic())
                .flatMap(path -> {
                    // WebFlux метод для збереження файлу на диск
                    return filePart.transferTo(path)
                            .then(Mono.fromCallable(() -> {
                                // Після збереження - пишемо в БД
                                // (Тривалість ставимо 0 або розраховуємо mp3agic'ом, якщо хочете)
                                Track track = new Track(title, artist, path.toString(), 300);
                                return trackRepository.save(track);
                            }).subscribeOn(Schedulers.boundedElastic()));
                });
    }

    public Mono<Playlist> createPlaylist(String name, List<Long> trackIds) {
        return Mono.fromCallable(() -> {
            Playlist playlist = playlistFactory.createPlaylist(name);

            if (trackIds != null) {
                List<Track> tracks = trackRepository.findAllById(trackIds);
                for (Track t : tracks) {
                    playlist.addTrack(t);
                }
            }
            return playlistRepository.save(playlist);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * 3. СТВОРЕННЯ СТАНЦІЇ (З вибором плейлистів)
     */
    public Mono<Station> createStation(String name, int bitrate, List<Long> playlistIds) {
        return Mono.fromCallable(() -> {
            Station station = new Station();
            station.setName(name);
            station.setBitrate(bitrate);

            if (playlistIds != null) {
                List<Playlist> playlists = playlistRepository.findAllById(playlistIds);
                station.setPlaylists(playlists);
            }
            return stationRepository.save(station);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * 4. МЕТОДИ ВИДАЛЕННЯ (Delete)
     */
    public Mono<Void> deleteTrack(Long id) {
        return Mono.fromRunnable(() -> trackRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic()).then();
    }

    public Mono<Void> deletePlaylist(Long id) {
        return Mono.fromRunnable(() -> playlistRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic()).then();
    }

    public Mono<Void> deleteStation(Long id) {
        return Mono.fromRunnable(() -> stationRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic()).then();
    }

    // Допоміжні методи для отримання списків (для випадаючих списків в адмінці)
    public Mono<List<Track>> getAllTracks() {
        return Mono.fromCallable(() -> trackRepository.findAll()).subscribeOn(Schedulers.boundedElastic());
    }
    public Mono<List<Playlist>> getAllPlaylists() {
        return Mono.fromCallable(() -> playlistRepository.findAll()).subscribeOn(Schedulers.boundedElastic());
    }


}