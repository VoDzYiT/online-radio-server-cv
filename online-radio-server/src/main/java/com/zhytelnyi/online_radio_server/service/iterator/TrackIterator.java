package com.zhytelnyi.online_radio_server.service.iterator;

import com.zhytelnyi.online_radio_server.model.Track;

public interface TrackIterator {
    boolean hasNext();

    Track next();
}
