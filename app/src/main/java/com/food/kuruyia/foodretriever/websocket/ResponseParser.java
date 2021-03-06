package com.food.kuruyia.foodretriever.websocket;

import com.food.kuruyia.foodretriever.utils.DataType;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class ResponseParser {
    private boolean m_ready;
    private JsonObject m_json;

    public ResponseParser(String message) {
        JsonElement jsonElement = new JsonParser().parse(message);
        if (jsonElement.isJsonObject()) {
            m_json = jsonElement.getAsJsonObject();
            m_ready = m_json.has("type") && m_json.has("data");
        }
    }

    public boolean isReady() {
        return m_ready;
    }

    public DataType getType() {
        return DataType.values()[m_json.get("type").getAsInt()];
    }

    public JsonObject getData() {
        return m_json.get("data").getAsJsonObject();
    }
}
