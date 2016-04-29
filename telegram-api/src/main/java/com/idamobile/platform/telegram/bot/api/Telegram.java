package com.idamobile.platform.telegram.bot.api;

import com.idamobile.platform.telegram.bot.api.dto.GetUpdatesRequest;
import com.idamobile.platform.telegram.bot.api.dto.Message;
import com.idamobile.platform.telegram.bot.api.dto.Response;
import com.idamobile.platform.telegram.bot.api.dto.ResponseParametrizedType;
import com.idamobile.platform.telegram.bot.api.dto.SendMessageRequest;
import com.idamobile.platform.telegram.bot.api.dto.Update;
import com.idamobile.platform.telegram.bot.api.dto.User;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.List;

@Slf4j
public class Telegram {

    public static final String TELEGRAM_API_URL = "https://api.telegram.org/bot{0}/{1}";

    public static final String METHOD_GET_ME = "getMe";
    public static final String METHOD_GET_UPDATES = "getUpdates";
    public static final String METHOD_SEND_MESSAGE = "sendMessage";

    private String token;
    private HttpClient httpClient;

    private Gson gson;

    public Telegram(String token) {
        this(token, HttpClientBuilder.create().build());
    }

    public Telegram(String token, HttpClient httpClient) {
        this.token = token;
        this.httpClient = httpClient;

        gson = (new GsonBuilder()).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    }


    public static Telegram getDefault(String token) {
        return new Telegram(token, HttpClientBuilder.create().build());
    }

    public User getMe() throws TelegramException {
        return get(METHOD_GET_ME, new ResponseParametrizedType(User.class));
    }

    public List<Update> getUpdates(Integer offset, Integer limit, Integer timeout) throws TelegramException {
        Type updatesType = new TypeToken<List<Update>>(){}.getType();
        return post(METHOD_GET_UPDATES, new GetUpdatesRequest(offset, limit, timeout), new ResponseParametrizedType(updatesType));
    }

    public Message sendMessage(SendMessageRequest req) throws TelegramException {
        return post(METHOD_SEND_MESSAGE, req, new ResponseParametrizedType(Message.class));
    }

    private <T> T get(String method, Type responseType) throws TelegramException {
        HttpGet req = new HttpGet(buildUri(method));
        return executeRequest(req, responseType);
    }

    private <T> T post(String method, Object requestBody, Type responseType) throws TelegramException {
        HttpPost post = new HttpPost(buildUri(method));
        post.setHeader(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

        try {
            post.setEntity(new StringEntity(gson.toJson(requestBody), "UTF-8"));

        } catch (UnsupportedEncodingException e) {
            throw new TelegramException("Unable to construct request", e);
        }
        return executeRequest(post, responseType);
    }

    private <T> T executeRequest(HttpUriRequest req, Type responseType) throws TelegramException {
        try {
            log.debug("=> {}", req.getURI());

            HttpResponse httpResponse = httpClient.execute(req);
            String content = EntityUtils.toString(httpResponse.getEntity());
            log.debug("<= {}: {}", httpResponse.getStatusLine(), content);

            Response<T> response = gson.fromJson(content, responseType);
            if (!response.isOk()) {
                throw new TelegramException(response.getErrorCode(), response.getDescription());
            }
            return response.getResult();
        } catch (Exception e) {
            throw new TelegramException("Unable to execute request", e);
        }
    }

    private String buildUri(String method) {
        return MessageFormat.format(TELEGRAM_API_URL, token, method);
    }


}
