package com.kidekdev.prioritetnik_bot.priority;

import java.util.*;

public class ItemUtil {

    public static Set<ItemPair> generatePair(List<ItemHistory> itemsFromDB) {
        Set<ItemPair> pairList = new HashSet<>();
        for (ItemHistory x : itemsFromDB) {
            for (ItemHistory y : itemsFromDB) {
                if (x.equals(y)) {
                    continue;
                }
                List<ItemHistory> itemList = new ArrayList<>();
                itemList.add(x);
                itemList.add(y);
                Collections.shuffle(itemList);
                pairList.add(new ItemPair(itemList));

            }
        }
        System.out.println("Максимум вопросов: " + pairList.size());
        return pairList;
    }

    public static void updateHistory(ItemHistory winner, ItemHistory loser) {
        winner.getDefeatedItems().add(loser); //добавляет к побежденным лузера
        winner.getDefeatedItems().addAll(loser.getDefeatedItems()); //присваивает себе победы лузера
        winner.getWinnerItems().stream().peek(x->x.getDefeatedItems().add(loser)).peek(x->x.getDefeatedItems().addAll(loser.getDefeatedItems()));

        loser.getWinnerItems().add(winner);
        loser.getWinnerItems().addAll(winner.getWinnerItems());
        loser.getDefeatedItems().stream().peek(x->x.getDefeatedItems().add(winner)).peek(x->x.getDefeatedItems().addAll(winner.getDefeatedItems()));
    }

  public static void showPriority(List<Item> itemsFromDB) {
       itemsFromDB.stream().sorted(Comparator.comparing(Item::getPriority)).forEach(x-> System.out.println(x.getPriority()  + ") " + x.getValue()));
   }

    public static String showInitList(List<String> list) {
        StringBuilder builder = new StringBuilder();
        builder.append("Ваш список: \n");
        builder.append(" \n");

        for (String item: list) {
            builder.append(item).append("\n");
        }
        return builder.toString();
    }

    public static String getPriorityText(List<Item> itemsFromDB) {
        StringBuilder builder = new StringBuilder();
        builder.append("Готово! \n");
        builder.append(" \n");
        itemsFromDB.stream()
                .sorted(Comparator.comparing(Item::getPriority))
                .map(x -> x.getPriority() + ") " + x.getValue()+"\n").
                forEach(builder::append);
        return builder.toString();
    }
}
