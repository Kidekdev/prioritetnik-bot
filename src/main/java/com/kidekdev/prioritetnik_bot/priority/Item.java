package com.kidekdev.prioritetnik_bot.priority;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Item {

    private UUID id;
    private String value;

    private Integer priority;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


 public static List<Item> generateItem(List<String> list) {
        return list.stream().map(v->new Item(UUID.randomUUID(),v,0)).toList();
    }
}
