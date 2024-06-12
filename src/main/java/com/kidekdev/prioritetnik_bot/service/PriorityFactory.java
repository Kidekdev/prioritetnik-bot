package com.kidekdev.prioritetnik_bot.service;

import com.kidekdev.prioritetnik_bot.priority.Item;
import com.kidekdev.prioritetnik_bot.priority.ItemHistory;
import com.kidekdev.prioritetnik_bot.priority.ItemPair;
import com.kidekdev.prioritetnik_bot.priority.ItemUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static com.kidekdev.prioritetnik_bot.priority.ItemUtil.getPriorityText;
import static com.kidekdev.prioritetnik_bot.service.PriorityFactory.CurrentMode.OFF;
import static com.kidekdev.prioritetnik_bot.service.PriorityFactory.ProcessParam.*;


@Slf4j
@Getter
@Setter
public class PriorityFactory {
    public PriorityFactory() {
        initItems = new ArrayList<>();
        mode = OFF;
    }


    List<String> commands = List.of("/sort", "/start", "/help", "/restart");
    private UUID fabricId;
    private Long chatId;

    private CurrentMode mode;

    private List<Item> items;
    private List<ItemHistory> allHistory;
    private List<ItemPair> matchList;

    private final ArrayList<String> initItems;

    private ItemPair currentPair;

//    void addItem(String item) {
//        if (!commands.contains(item)) {
//            initItems.add(item.trim());
//        }
//    }
    boolean addAllItem(String item) {
        if (!commands.contains(item)) {
            var items = Arrays.stream(item.split("\n")).filter(s -> s != null && !s.isEmpty()).map(String::trim).toList();
            initItems.addAll(items);
            return true;
        }
        return false;
    }

    void clearFabric() {
        initItems.clear();
        items.clear();
        allHistory.clear();
        matchList.clear();
        currentPair = null;
    }

    String getCurrentPair() {
        return "";
    }

    public enum CurrentMode {
        OFF, FILLING, SORT, DONE
    }

    //инициализация процесса, вернет первый вопрос.
    public ProcessAnswer initPairs() {
        items = Item.generateItem(initItems);
        allHistory = new ArrayList<>(items.stream().map(x -> new ItemHistory(x, items.size())).toList());
        matchList = new ArrayList<>(ItemUtil.generatePair(allHistory));

        Collections.shuffle(matchList);
        currentPair = matchList.get(0);
        ItemHistory historyA = currentPair.getFirstPlayer();
        ItemHistory historyB = currentPair.getSecondPlayer();
        return new ProcessAnswer(false, historyA.getValue(), historyB.getValue(), null);
    }

    ProcessAnswer process(PriorityFactory.ProcessParam param) {
        if (param.equals(ONE) | param.equals(TWO)) {
            //логика ответа на ввод 1 или 2
            ItemHistory historyA = currentPair.getFirstPlayer();
            ItemHistory historyB = currentPair.getSecondPlayer();

            if (param.equals(ONE)) {
                ItemUtil.updateHistory(historyA, historyB);
            } else {
                ItemUtil.updateHistory(historyB, historyA);
            }
            matchList.remove(0);
            if (matchList.isEmpty()) {
                allHistory.stream().peek(x->System.out.println(x.getPriority())).forEach(ItemHistory::setPriority);

                return new ProcessAnswer(true, historyA.getValue(), historyB.getValue(), getPriorityText(items));
            }

            //логика обмена историей
            while (true) {
                if (matchList.isEmpty()) {
                    allHistory.stream().peek(x->System.out.println(x.getPriority())).forEach(ItemHistory::setPriority);
                    return new ProcessAnswer(true, historyA.getValue(), historyB.getValue(), getPriorityText(items));
                }
                currentPair = matchList.get(0);
                historyA = currentPair.getFirstPlayer();
                historyB = currentPair.getSecondPlayer();

                if (historyA.getDefeatedItems().contains(historyB)) {
                    log.info(historyA.getValue() + " уже побеждал " + historyB.getValue());
                    ItemUtil.updateHistory(historyA, historyB);
                    matchList.remove(0);
                } else if (historyB.getDefeatedItems().contains(historyA)) {
                    log.info(historyB.getValue() + " уже побеждал " + historyA.getValue());
                    ItemUtil.updateHistory(historyB, historyA);
                    matchList.remove(0);
                } else {
                    //мы не знаем кто победит, надо спросить пользователя
                    return new ProcessAnswer(false, historyA.getValue(), historyB.getValue(), null);
                }
            }
        }
        throw new RuntimeException("Ошибка в логике ");
    }



    enum ProcessParam {
        START, ONE, TWO, CLOSE
    }

    @Data
    @AllArgsConstructor
    public class ProcessAnswer {

        boolean completed;
        String first;
        String second;
        String priorityList;
    }


}
