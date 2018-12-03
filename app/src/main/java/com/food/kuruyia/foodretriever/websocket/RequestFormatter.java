package com.food.kuruyia.foodretriever.websocket;

import com.food.kuruyia.foodretriever.utils.DataType;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class RequestFormatter {
    public static String format(DataType dataType, JsonObject data) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", dataType.ordinal());
        jsonObject.add("data", data);

        Gson gson = new Gson();
        return gson.toJson(jsonObject);
    }
}
