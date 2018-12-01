package com.food.kuruyia.foodretriever.utils;

import java.util.HashMap;

public interface IDataChange {
    void onDataChanged(DataType dataType, HashMap<String, Object> data);
    void onChangeData(DataType dataType, HashMap<String, Object> data);
}
