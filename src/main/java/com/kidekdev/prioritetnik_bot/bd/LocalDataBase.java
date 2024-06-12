package com.kidekdev.prioritetnik_bot.bd;

import com.kidekdev.prioritetnik_bot.service.PriorityFactory;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
@Data
public class LocalDataBase {
    public static Map<Long, PriorityFactory> database = new HashMap<>();
}
