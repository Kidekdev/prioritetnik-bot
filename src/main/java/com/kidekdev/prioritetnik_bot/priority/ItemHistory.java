package com.kidekdev.prioritetnik_bot.priority;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class ItemHistory {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemHistory that = (ItemHistory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public ItemHistory(Item item, Integer allSize) {
        this.item = item;
        this.id = item.getId();
        this.value = item.getValue();
        this.priority = item.getPriority();
        this.allSize = allSize;
        winnerItems = new HashSet<>();
        defeatedItems = new HashSet<>();
    }

    private UUID id;

    private String value;

    private Integer priority;

    private Item item;

    private final Integer allSize;

    private Set<ItemHistory> winnerItems;
    private Set<ItemHistory> defeatedItems;

    private boolean completed;

    public boolean tryComplete(int allSize) {
        completed = allSize - winnerItems.size() + defeatedItems.size() - 1 == 0;
        return completed;
    }

    public void setPriority() {
        if (winnerItems.size() + defeatedItems.size() == allSize) {
            throw new IllegalArgumentException();
        }
        priority = allSize - defeatedItems.size();
        item.setPriority(priority);
    }

    @Override
    public String toString() {
        return "ItemID: " + id + "\n" +
                "Item value: " + value + "\n" +
                "Item winner him: " + toString(winnerItems) + "\n" +
                "Item defeated him: " + toString(defeatedItems) + "\n";
    }

    String toString(Set<ItemHistory> items) {
        List<ItemHistory> itemsList = new ArrayList<>(items);
        StringBuilder builder = new StringBuilder();
        for (ItemHistory item : itemsList) {
            builder.append(item.getValue());
            builder.append(", ");
        }
        return builder.toString();
    }
}
