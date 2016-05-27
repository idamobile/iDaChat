package com.idamobile.platform.chatbot.ws;

import com.github.jtail.jpa.util.EntityUtils;
import com.github.zjor.telegram.bot.api.Telegram;
import com.github.zjor.telegram.bot.api.dto.SendMessageRequest;
import com.github.zjor.telegram.bot.framework.model.TelegramUser;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Slf4j
@WebService(endpointInterface = "com.idamobile.platform.chatbot.ws.ManagementService")
public class ManagementServiceImpl implements ManagementService {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private Telegram telegram;

    @Override
    public void announce(String text) {
        List<TelegramUser> users = EntityUtils.find(em, TelegramUser.class).list();
        users.stream().forEach(u -> {
            SendMessageRequest req = new SendMessageRequest(u.getTelegramId(), text);
            telegram.sendMessage(req);
        });

        log.info("{}", text);
    }
}
