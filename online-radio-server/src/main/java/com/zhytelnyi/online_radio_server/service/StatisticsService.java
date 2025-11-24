//package com.zhytelnyi.online_radio_server.service;
//
//import com.zhytelnyi.online_radio_server.model.User;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.List;
//@Service
//public class StatisticsService {
//    private int totalConnections = 0;
//    private final Map<Long, List<User>> activeClients = new ConcurrentHashMap<>();
//
//    public void userConnected(Long stationId, User user) {
//        totalConnections ++;
//        activeClients.computeIfAbsent(stationId, k -> new ArrayList<>()).add(user);
//    }
//
//    public void userDisconnected(Long stationId, User user) {
//        if (activeClients.containsKey(stationId)) {
//            activeClients.get(stationId).remove(user);
//        }
//    }
//
//    public int getTotalConnections() {
//        return totalConnections;
//    }
//
//    public int getActiveClientsCount(Long stationId) {
//        return activeClients.getOrDefault(stationId, List.of()).size();
//    }
//}
