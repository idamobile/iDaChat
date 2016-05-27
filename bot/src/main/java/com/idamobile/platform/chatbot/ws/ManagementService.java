package com.idamobile.platform.chatbot.ws;

import javax.jws.WebService;

@WebService
public interface ManagementService {

    void announce(String text);

}
