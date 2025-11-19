package com.zhytelnyi.online_radio_server.service.visitor;

import com.zhytelnyi.online_radio_server.model.Playlist;
import com.zhytelnyi.online_radio_server.model.Station;
import com.zhytelnyi.online_radio_server.model.Track;
import com.zhytelnyi.online_radio_server.model.User;

public interface Visitor {
    void visit(Station station);
    void visit(User user);
    void visit(Track track);
    void visit(Playlist playlist);
}
