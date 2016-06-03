package com.idamobile.platform.chatbot.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class LocalizationUtils {

    public static final String I18N = "i18n";

    public static String getLocalizedValue(String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        if (value.startsWith(I18N)) {
            String json = value.substring(I18N.length());
            Map<String, String> values = new Gson().fromJson(json, new TypeToken<Map<String, String>>() {
            }.getType());
            return values.get("ru");
        } else {
            return value;
        }
    }


}
