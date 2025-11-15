//package com.zhytelnyi.online_radio_server.service;
//
//import com.zhytelnyi.online_radio_server.model.Station;
//import com.zhytelnyi.online_radio_server.model.Track;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.core.io.Resource;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
////import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
//
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//@Service
//public class StreamingService {
//
//    @Autowired
//    private RadioServer radioServer;
//
//    public StreamingResponseBody subscribe(Long stationId) {
//        return (OutputStream outputStream) -> {
//            try {
//                radioServer.addListener(stationId, outputStream);
//
//                while (true) {
//                    Thread.sleep(10000);
//                }
//            } catch ( InterruptedException e) {
//                Thread.currentThread().interrupt();
//            } finally {
//                radioServer.removeListener(stationId, outputStream);
//            }
//        };
//    }
//
//    public Station getStation(Long stationId) {
//        return radioServer.getStation(stationId);
//    }
//
//    public ResponseEntity<StreamingResponseBody> streamTrack(Track track) {
//        try {
//            final Path filePath = Paths.get(track.getFilePath());
//            final Resource audioResource = new FileSystemResource(filePath);
//
//            if (!audioResource.exists()) {
//                return ResponseEntity.notFound().build();
//            }
//
//            StreamingResponseBody responseBody = (OutputStream outputStream) -> {
//                try (InputStream inputStream = audioResource.getInputStream()) {
//                    byte[] buffer = new byte[4096];
//                    int bytesRead;
//                    while ((bytesRead = inputStream.read(buffer)) != -1) {
//                        outputStream.write(buffer, 0, bytesRead);
//                        outputStream.flush();
//                    }
//                }
//            };
//            return ResponseEntity.ok().contentType(MediaType.valueOf("audio/mpeg")).body(responseBody);
//        }catch (Exception e) {
//            return ResponseEntity.internalServerError().build();
//        }
//    }
//
//
//}
