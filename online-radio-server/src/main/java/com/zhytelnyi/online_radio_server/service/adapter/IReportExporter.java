package com.zhytelnyi.online_radio_server.service.adapter;

import com.zhytelnyi.online_radio_server.model.Station;

public interface IReportExporter {
    String export(Station station);
}
