package com.kidekdev.prioritetnik_bot.service;


import com.kidekdev.prioritetnik_bot.bd.LocalDataBase;
import com.kidekdev.prioritetnik_bot.config.BotConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.kidekdev.prioritetnik_bot.priority.ItemUtil.showInitList;
import static com.kidekdev.prioritetnik_bot.service.PriorityFactory.CurrentMode.*;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    BotConfig botConfig;
    Map<Long, PriorityFactory> db = LocalDataBase.database;

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        //чтение сообщения
        String text = null;
        Long chatId;
        String callbackData;
        Integer messageId;
        if (update.hasCallbackQuery()) {
            callbackData = update.getCallbackQuery().getData();
            messageId = update.getCallbackQuery().getMessage().getMessageId();
            chatId = update.getCallbackQuery().getMessage().getChatId();
//            text = callbackData;
            text = switch (callbackData) {
                case "sortButton" -> "/sort";
                case "restartButton" -> "/restart";
//                case "firstButton" -> "firstButton";
//                case "secondButton" -> "secondButton";
                default -> callbackData;
            };
        } else {
            text = update.getMessage().getText();
            chatId = update.getMessage().getChatId();
        }
        log.info("Получено сообщение от пользователя {}, текст: {}", chatId, text);
        //создание ответа
//        SendMessage sendMessage = new SendMessage();
//        sendMessage.setChatId(String.valueOf(chatId));
//        if (chatId != 1869489280) {
//            sendMessage.setText("forbidden");
//            execute(sendMessage);
//            return;
//        }
        PriorityFactory factory = db.get(chatId);
        try {

            level1(update, chatId, text, factory);
        } catch (Exception e) {
            e.printStackTrace();
            send(generateMassage(chatId), "Ошибка сервера. Нажмите /start для продолжения");
        }

//        if (text.equals("/sort")) {
//            sendMessage.setText(factory.getInitItems().toString());
//            execute(sendMessage);
//            Thread.sleep(300);
//            sendMessage.setText("Бот готов к сортировке");
//            execute(sendMessage);
//            Thread.sleep(300);
//            factory.setMode(SORT);
//            var answer = factory.initPairs();
//            sendMessage.setText(answer.getFirst() + " или " + answer.getSecond() + "?");
//            execute(sendMessage);
//        }
//
//        if (factory.getMode().equals(PriorityFactory.CurrentMode.SORT)) {
//            PriorityFactory.ProcessAnswer answer;
//            if (text.equals("1")) {
//                answer = factory.process(PriorityFactory.ProcessParam.ONE);
//            } else if (text.equals("2")) {
//                answer = factory.process(PriorityFactory.ProcessParam.TWO);
//            } else {
//                sendMessage.setText("Введите 1 или 2");
//                execute(sendMessage);
//                return;
//            }
//
//            if (!answer.completed) {
//                sendMessage.setText(answer.getFirst() + " или " + answer.getSecond() + "?");
//                execute(sendMessage);
//            } else {
//
//            }
//        }


//        if (!db.containsKey(chatId)) {
//            timerTask = new TimerTask();
//            timerTask.setMassage(text);
//
//            db.put(chatId, timerTask);
//            sendMessage.setText("Введите количество секунд");
//            execute(sendMessage);
//
//        } else {
//
//            if (db.get(chatId).getMassage() != null) {
//
//                timerTask = db.get(chatId);
//
//                Long timer = Long.parseLong(text);
//
//                sendMessage.setText("Установлен таймер на %S секунд".formatted(timer));
//                execute(sendMessage);
//                Thread.sleep(timer * 1000);
//
//
//                sendMessage.setText(timerTask.getMassage());
//                execute(sendMessage);
//                db.remove(chatId);
//
//            }
//
//        }


//        taskScheduler.schedule(new MyTask(), new CronTrigger(cronExpression));

    }

    void level1(Update update, Long chatId, String text, PriorityFactory factory) throws TelegramApiException {
        if (text.equals("/info")) {
            send(generateMassage(chatId), "Приоритетник поможет вам быстро отсортировать ваш список по степени важности. Бот реализует алгоритм сортировки вопросом, то есть вам предстоит самостоятельно отвечать на вопросы бота. Сравнивая между собой элементы списка напрямую вы получите реалистичную цепочку приоритетов. Это полезно, если список большой, и у вас возникают трудности с его сортировкой");
            return;
        }
        if (text.equals("/start") | text.equals("/restart")) {
            log.info("Создана новая фабрика");
            db.remove(chatId);
            factory = db.computeIfAbsent(chatId, k -> new PriorityFactory());
            factory.setMode(FILLING);

            if (text.equals("/start")) {
                send(generateMassage(chatId), "Бот приоритетник позволяет вам быстро отсортировать большое количество вещей по степени важности.");
            }
            //todo рефакторинг нужен

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            var sortButton = new InlineKeyboardButton();
            sortButton.setText("Сортировать");
            sortButton.setCallbackData("sortButton");
            rowInline.add(sortButton);
            rowsInline.add(rowInline);
            markup.setKeyboard(rowsInline);


            send(generateMassage(chatId, markup), "Добавляйте элементы списком или по-одному. Минимальное количество - 3 элемента. После завершения списка нажмите Сортировать.");
            return;
        }
        if (factory.getMode().equals(DONE)) {
//            send(generateMassage(chatId), "Для продолжения нажмите /restart");
            return;
        }
        level2(update, chatId, text, factory);
    }

    void level2(Update update, Long chatId, String text, PriorityFactory factory) throws TelegramApiException {
        if (factory.getMode().equals(FILLING) && !text.equals("/sort")) {
            factory.addAllItem(text);
            //todo добавить защиту от неправильного порядка команд
            return;
        }
        if (text.equals("/sort")) {
            if (factory.getInitItems().size() < 3) {
                send(generateMassage(chatId), "Недостаточно элементов. Минимум - 3");
                return;
            }
            factory.setMode(SORT);
            level3(update, chatId, "init-pairs", factory);
            return;
        }
        level3(update, chatId, text, factory);
        return;
    }

    void level3(Update update, Long chatId, String text, PriorityFactory factory) throws TelegramApiException {
        if (text.equals("init-pairs")) {
            send(generateMassage(chatId), showInitList(factory.getInitItems()));
            var answer = factory.initPairs();
            send(generateMassage(chatId), "Максимум вопросов: " + factory.getMatchList().size());

            //todo рефакторинг нужен
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();

            var firstButton = new InlineKeyboardButton();
            firstButton.setText(answer.getFirst());
            firstButton.setCallbackData("firstButton");
            rowInline.add(firstButton);

            var secondButton = new InlineKeyboardButton();
            secondButton.setText(answer.getSecond());
            secondButton.setCallbackData("secondButton");
            rowInline.add(secondButton);

            rowsInline.add(rowInline);
            markup.setKeyboard(rowsInline);

            send(generateMassage(chatId, markup), answer.getFirst() + " или " + answer.getSecond() + "?");
            return;
        }


        PriorityFactory.ProcessParam param;
        if (text.equals("firstButton")) {
            param = PriorityFactory.ProcessParam.ONE;
        } else if (text.equals("secondButton")) {
            param = PriorityFactory.ProcessParam.TWO;
        } else {
//            send(generateMassage(chatId), "Введите 1 или 2");
            return;
        }
        PriorityFactory.ProcessAnswer answer = factory.process(param);
        if (answer.completed) {
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            var sortButton = new InlineKeyboardButton();
            sortButton.setText("Начать заново");
            sortButton.setCallbackData("restartButton");
            rowInline.add(sortButton);
            rowsInline.add(rowInline);
            markup.setKeyboard(rowsInline);

            send(generateMassage(chatId, markup), answer.getPriorityList());
            factory.setMode(DONE);
//            send(generateMassage(chatId), "Для продолжения нажмите /restart");
        } else {
            //todo рефакторинг нужен
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();

            var firstButton = new InlineKeyboardButton();
            firstButton.setText(answer.getFirst());
            firstButton.setCallbackData("firstButton");
            rowInline.add(firstButton);

            var secondButton = new InlineKeyboardButton();
            secondButton.setText(answer.getSecond());
            secondButton.setCallbackData("secondButton");
            rowInline.add(secondButton);

            rowsInline.add(rowInline);
            markup.setKeyboard(rowsInline);
            send(generateMassage(chatId, markup), answer.getFirst() + " или " + answer.getSecond() + "?");
        }
        return;
    }


    void send(SendMessage message, String text) throws TelegramApiException {
        message.setText(text);
        execute(message);
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    public TelegramBot(BotConfig botConfig) throws TelegramApiException {
        super(botConfig.getToken());

        List<BotCommand> listCommands = new ArrayList<>();
        listCommands.add(new BotCommand("/start", "Начало работы"));
        listCommands.add(new BotCommand("/info", "Помощь"));
        listCommands.add(new BotCommand("/restart", "Продолжение работы"));
        listCommands.add(new BotCommand("/sort", "Начать сортировку"));
        execute(new SetMyCommands(listCommands, new BotCommandScopeDefault(), null));
        this.botConfig = botConfig;
    }


    SendMessage generateMassage(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        return sendMessage;
    }

    SendMessage generateMassage(Long chatId, ReplyKeyboard replyMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(replyMarkup);
        return sendMessage;
    }

}
