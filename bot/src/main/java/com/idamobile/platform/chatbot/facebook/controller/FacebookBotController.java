package com.idamobile.platform.chatbot.facebook.controller;

import com.idamobile.platform.chatbot.facebook.FacebookBotConfiguration;
import com.idamobile.platform.chatbot.facebook.service.FacebookService;
import com.idamobile.platform.chatbot.facebook.api.dto.Attachment;
import com.idamobile.platform.chatbot.facebook.api.dto.Button;
import com.idamobile.platform.chatbot.facebook.api.dto.Message;
import com.idamobile.platform.chatbot.facebook.api.dto.MessagingEntry;
import com.idamobile.platform.chatbot.facebook.api.dto.TemplatePayload;
import com.idamobile.platform.chatbot.facebook.api.dto.WebHookPostRequest;
import com.idamobile.platform.chatbot.service.LiteIntegrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

@Slf4j
@Controller
@RequestMapping("facebook")
public class FacebookBotController {

    public static final String SUBSCRIBE = "subscribe";

    @Inject
    private FacebookBotConfiguration config;

    @Inject
    private FacebookService facebookService;

    @Inject
    private LiteIntegrationService integrationService;

    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    @RequestMapping(value = "webhook", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> verifyWebHook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.challenge") String challenge,
            @RequestParam("hub.verify_token") String verifyToken) {

        log.info("{}, {}, {}", mode, challenge, verifyToken);

        if (SUBSCRIBE.equals(mode) && config.getVerificationToken().equals(verifyToken)) {
            log.info("WebHook validation was successful");
            return ResponseEntity.ok(challenge);
        } else {
            log.info("WebHook validation failed");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @RequestMapping(value = "webhook", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> handleMessage(@RequestBody WebHookPostRequest req) {
        log.info("Req <= {}", req);

        req.getEntry().stream().flatMap(e -> e.getMessaging().stream()).forEach(m -> {
            executorService.submit(() -> processMessageEntry(m));
        });

        return ResponseEntity.ok("success");
    }

    private void processMessageEntry(MessagingEntry entry) {
        log.info("Handling: {}", entry);
        try {
            if (entry.getMessage() != null) {
                replyWithButtons(entry.getSender().getId());
            } else if (entry.getPostBack() != null) {
                handleCommand(entry.getSender().getId(), entry.getPostBack().getPayload());
            }
        } catch (Exception e) {
            log.error("Failed to handle message: " + e.getMessage(), e);
        }
    }

    private void replyWithButtons(String recipientId) throws IOException {
        /**
         * buttons is limited to 3
         */
        facebookService.sendMessage(recipientId, new Message(
                Attachment.createTemplate(TemplatePayload.createButtons(
                        "I support the following commands",
                        Button.createPostback("News", "news"),
                        Button.createPostback("Contacts", "contacts"),
                        Button.createPostback("Rates", "rates")
//                        Button.createPostback("ATMs", "atms")
                ))
        ));
    }

    private Supplier<String> routeIntegration(String command) {
        if ("news".equals(command)) {
            return () -> integrationService.getLastNews();
        } else if ("contacts".equals(command)) {
            return () -> integrationService.getContacts();
        } else if ("rates".equals(command)) {
            return () -> integrationService.getExchangeRates();
        } else {
            return () -> "not implemented";
        }

    }

    private void handleCommand(String recipientId, String command) throws IOException {
        String response = routeIntegration(command).get();
        while (response.length() >= 320) {
            String chunk = response.substring(0, 320);
            response = response.substring(320);
            facebookService.sendMessage(recipientId, new Message(chunk));
        }
        facebookService.sendMessage(recipientId, new Message(response));
    }

}
