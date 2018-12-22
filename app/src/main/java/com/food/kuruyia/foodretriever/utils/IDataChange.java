package com.food.kuruyia.foodretriever.utils;

import com.google.gson.JsonObject;

public interface IDataChange {
    void onDataChanged(DataType dataType, JsonObject data);
    void onChangeData(DataType dataType, JsonObject data);
}

