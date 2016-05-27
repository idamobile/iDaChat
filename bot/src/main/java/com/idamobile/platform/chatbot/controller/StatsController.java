package com.idamobile.platform.chatbot.controller;

import com.github.jtail.jpa.util.EntityUtils;
import com.github.zjor.telegram.bot.framework.model.TelegramUser;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Controller
@RequestMapping("stats")
public class StatsController {

    @PersistenceContext
    private EntityManager em;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object getStats() {
        return EntityUtils.find(em, TelegramUser.class).list();
    }
}
