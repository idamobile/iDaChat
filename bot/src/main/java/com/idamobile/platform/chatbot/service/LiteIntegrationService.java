package com.idamobile.platform.chatbot.service;

import com.idamobile.platform.light.core.ws.client.WsEndpointClient;
import com.idamobile.platform.light.core.ws.dto.WsCurrencyRateDTO;
import com.idamobile.platform.light.core.ws.dto.contacts.WsContactDTO;
import com.idamobile.platform.light.core.ws.dto.news.WsGetNewsRequestDTO;
import com.idamobile.platform.light.core.ws.dto.news.WsGetNewsResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.idamobile.platform.chatbot.util.LocalizationUtils.getLocalizedValue;

@Slf4j
@Service
public class LiteIntegrationService {

    @Inject
    private WsEndpointClient client;

    public String getContacts() {
        WsContactDTO[] cs = client.getContacts().getContacts();
        if (cs == null || cs.length == 0) {
            return "No contacts";
        }
        StringBuilder contacts = new StringBuilder();
        Arrays.stream(cs).forEach(c -> {
            contacts.append("<b>").append(getLocalizedValue(c.getName())).append("</b>\n");
            if ("PHONE".equals(c.getType())) {
                contacts.append("&#128222; ");
            }
            contacts.append(getLocalizedValue(c.getValue())).append("\n\n");

        });
        return contacts.toString();
    }

    public String getExchangeRates() {
        Map<String, List<WsCurrencyRateDTO>> index = new HashMap<>();
        Map<String, String> names = new HashMap<>();
        Arrays.stream(client.getCurrencyRates().getRates()).forEach(rate -> {
            if (!index.containsKey(rate.getType())) {
                index.put(rate.getType(), new LinkedList<>());
                names.put(rate.getType(), getLocalizedValue(rate.getGroupName()));
            }
            index.get(rate.getType()).add(rate);
        });
        DateFormat dateFormat = new SimpleDateFormat("dd.mm.yyyy");
        StringBuilder rates = new StringBuilder("&#128197; Данные актуальны на ").append(dateFormat.format(new Date())).append("\n");
        for (String type : index.keySet()) {
            rates.append("\n[ ").append(names.get(type)).append(" ]\n");
            index.get(type).stream().forEach(r -> {
                rates.append(MessageFormat.format("<b>{0} &#8596; {1}</b>\n", r.getFirstCurrency(), r.getSecondCurrency()));
                rates.append("  &#8226; Покупка:  ").append(r.getBuy()).append('\n');
                rates.append("  &#8226; Продажа:  ").append(r.getSell()).append('\n');
            });
        }
        return rates.toString();
    }

    public String getLastNews() {
        WsGetNewsResponseDTO res = client.getNewsFeed(new WsGetNewsRequestDTO(1));
        StringBuilder news = new StringBuilder();
        Arrays.stream(res.getNews()).forEach(n -> {
            news
                    .append(sanitize(n.getPreview()))
                    .append('\n')
                    .append(n.getUrl()).append('\n')
                    .append(n.getCreationDate());
        });
        return news.toString();
    }

    private String sanitize(String content) {
        content = content.replaceAll("</p><p>", "\n\n");
        return content.replaceAll("<.*?>", "");
    }

}
