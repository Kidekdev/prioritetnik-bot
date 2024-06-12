package com.kidekdev.prioritetnik_bot.priority;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class ItemService {

    List<Item> process(List<Item> itemsFromDB) {
        List<ItemHistory> allHistory = new ArrayList<>(itemsFromDB.stream().map(x -> new ItemHistory(x, itemsFromDB.size())).toList());
        List<ItemPair> matchList = new ArrayList<>(ItemUtil.generatePair(allHistory));
        final int itemsCount = allHistory.size();
        boolean complete = false;
        int matchListSize = matchList.size();


        for (int i = 0; i < matchListSize; i++) {
//            matchList.forEach(System.out::println);
            Collections.shuffle(matchList);
            ItemPair currentPair = matchList.get(0);
            ItemHistory historyA = currentPair.getFirstPlayer();
            ItemHistory historyB = currentPair.getSecondPlayer();

            if (historyA.getDefeatedItems().contains(historyB)) {
                log.info(historyA.getValue() + " уже побеждал " + historyB.getValue());
                ItemUtil.updateHistory(historyA, historyB);
            } else if (historyB.getDefeatedItems().contains(historyA)) {
                log.info(historyB.getValue() + " уже побеждал " + historyA.getValue());
                ItemUtil.updateHistory(historyB, historyA);
            } else {
                System.out.println(historyA.getValue() + " или " + historyB.getValue() + "?");
                if (firstWin()) {
                    ItemUtil.updateHistory(historyA, historyB);
                } else {
                    ItemUtil.updateHistory(historyB, historyA);
                }
            }

//            completeHistory(itemsCount, historyA, historyB, allHistory, completedHistory);
//            log.info(historyA.toString());
//            log.info(historyB.toString());
            System.out.println();
//            if (completedHistory.size() == itemsCount) {
//                complete = true;
//            }

            matchList.remove(0);
        }

        allHistory.forEach(ItemHistory::setPriority);

//        itemsFromDB.forEach(System.out::println);
//        allHistory.forEach(System.out::println);
       ItemUtil.showPriority(itemsFromDB);
        return itemsFromDB;
    }


    private boolean firstWin() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.next();
            System.out.println(input);
            if (Objects.equals(input, "1")) {
                return true;
            }
            if (Objects.equals(input, "2")) {
                return false;
            }
            if (Objects.equals(input, "0")) {
                throw new RuntimeException("Выход из приоритетника");
            }

        }
    }


//    private void completeHistory(int itemsCount, ItemHistory itemHistoryA, ItemHistory itemHistoryB, List<ItemHistory> history, List<ItemHistory> completedHistory) {
//        itemHistoryA.tryComplete(itemsCount);
//        itemHistoryB.tryComplete(itemsCount);
//
//        if (itemHistoryA.isCompleted()) {
//            history.remove(itemHistoryA);
//            completedHistory.add(itemHistoryA);
//        }
//        if (itemHistoryB.isCompleted()) {
//            history.remove(itemHistoryB);
//            completedHistory.add(itemHistoryB);
//        }
//    }
}
