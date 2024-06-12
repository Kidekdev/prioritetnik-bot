package com.kidekdev.prioritetnik_bot.priority;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class ItemPair {
    private ItemHistory firstPlayer;
    private ItemHistory secondPlayer;

    @Override
    public String toString() {
        return firstPlayer.getValue() +" и "+ secondPlayer.getValue();
    }




    public ItemPair(List<ItemHistory> items) {
        if (items.size() != 2) {
            throw new RuntimeException("Ошибка в логике заполнения пар");
        }
        firstPlayer = items.get(0);
        secondPlayer = items.get(1);
    }
}
