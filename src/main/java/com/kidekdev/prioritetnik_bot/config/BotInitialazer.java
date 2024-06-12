package com.kidekdev.prioritetnik_bot.config;


import com.kidekdev.prioritetnik_bot.service.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class BotInitialazer {


    @Autowired
    TelegramBot telegramBot;


    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {


        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

        try {
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    @EventListener({ContextClosedEvent.class})
    public void init2() {
        System.out.println("close");
    }

}
