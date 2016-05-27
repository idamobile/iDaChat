package com.idamobile.platform.chatbot.ws;

import lombok.extern.slf4j.Slf4j;

import javax.jws.WebService;

@Slf4j
@WebService(endpointInterface = "com.idamobile.platform.chatbot.ws.ManagementService")
public class ManagementServiceImpl implements ManagementService {

    @Override
    public void announce(String text) {

        log.info("{}", text);

    }
}
