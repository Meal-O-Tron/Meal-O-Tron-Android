package com.food.kuruyia.foodretriever.websocket;

import com.food.kuruyia.foodretriever.utils.DataType;
import com.google.gson.Gson;

import java.util.HashMap;

public class RequestFormatter {
    public static String format(DataType dataType, HashMap data) {
        HashMap<String, Object> formatted = new HashMap<>();
        formatted.put("type", dataType.ordinal());
        formatted.put("data", data);

        Gson gson = new Gson();
        return gson.toJson(formatted);
    }
}
