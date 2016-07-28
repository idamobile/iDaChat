package com.idamobile.platform.chatbot.facebook.service;

import com.google.gson.Gson;
import com.idamobile.platform.chatbot.facebook.FacebookBotConfiguration;
import com.idamobile.platform.chatbot.facebook.api.dto.Message;
import com.idamobile.platform.chatbot.facebook.api.dto.SendMessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;

@Slf4j
@Service
public class FacebookService {

    @Inject
    private FacebookBotConfiguration conf;

    private HttpClient httpClient = HttpClients.createDefault();

    public void sendMessage(String recipientId, Message message) throws IOException {
        HttpPost post = new HttpPost("https://graph.facebook.com/v2.6/me/messages?access_token=" + conf.getPageAccessToken());
        post.setHeader(new BasicHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
        SendMessageRequest reqBody = new SendMessageRequest(recipientId, message);
        post.setEntity(new StringEntity((new Gson()).toJson(reqBody), "UTF-8"));

        log.info("Req => {}", message);
        HttpResponse httpResponse = httpClient.execute(post);
        String response = EntityUtils.toString(httpResponse.getEntity());
        log.info("Status <= {}; {}", httpResponse.getStatusLine(), response);
    }

}
