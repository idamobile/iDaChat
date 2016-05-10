package com.idamobile.platform.chatbot;

import com.idamobile.platform.telegram.bot.api.Telegram;
import com.idamobile.platform.telegram.bot.api.TelegramException;
import com.idamobile.platform.telegram.bot.api.dto.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@Slf4j
public class Main {

    public static void main(String[] args) throws TelegramException {
        ApplicationContext context = new ClassPathXmlApplicationContext("com/idamobile/platform/chatbot/spring-context-bot.xml");
        Telegram telegram = context.getBean(Telegram.class);

        User botInfo = telegram.getMe();
        log.info("Bot has started: {}", botInfo);
        log.info("Visit https://telegram.me/{} for adding bot to contacts", botInfo.getUsername());
    }
}
