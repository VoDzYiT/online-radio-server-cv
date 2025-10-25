package com.zhytelnyi.online_radio_server.service.visitor;

public interface Element {
    void accept(Visitor visitor);
}
