package com.idamobile.platform.chatbot.util;

import com.github.zjor.telegram.bot.framework.dispatch.HandlingFailedException;
import com.idamobile.platform.chatbot.fn.XBiFunction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageLoaderUtils {

    private static final Pattern FILENAME_REGEX = Pattern.compile("([^/.]*\\.\\w*)$");

    public static String getFilename(String url) {
        Matcher m = FILENAME_REGEX.matcher(url);
        if (m.find()) {
            return m.group(1);
        }

        throw new IllegalArgumentException("Unable to parse filename");
    }

    public static <R> Result<R> fetchImage(String url, XBiFunction<String, InputStream, R, ? extends Throwable> fn) throws HandlingFailedException {
        HttpClient httpClient = HttpClients.createDefault();
        try {
            HttpResponse httpResponse = httpClient.execute(new HttpGet(url));
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                Result<R> result = null;
                try {
                    result = new Result(fn.apply(getFilename(url), httpResponse.getEntity().getContent()), null);
                } catch (Throwable t) {
                    result = new Result<>(null, t);
                } finally {
                    EntityUtils.consume(httpResponse.getEntity());
                    return result;
                }
            } else {
                throw new HandlingFailedException("Failed to download banner image: " + httpResponse.getStatusLine());
            }
        } catch (IOException e) {
            throw new HandlingFailedException("Error requesting banner image", e);
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Result<R> {
        private R result;

        private Throwable error;
    }


}
