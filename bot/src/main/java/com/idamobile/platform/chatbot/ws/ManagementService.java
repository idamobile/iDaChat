package com.idamobile.platform.chatbot.ws;

import javax.jws.WebService;

@WebService
public interface ManagementService {

    Response announce(AnnouncementRequest req);

}
