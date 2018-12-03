package com.food.kuruyia.foodretriever.utils;

import com.google.gson.JsonObject;

import java.util.HashMap;

public interface IDataChange {
    void onDataChanged(DataType dataType, JsonObject data);
    void onChangeData(DataType dataType, HashMap<String, Object> data);
}
