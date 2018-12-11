package com.food.kuruyia.foodretriever.mainscreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.food.kuruyia.foodretriever.utils.DataType;
import com.food.kuruyia.foodretriever.utils.IDataChange;
import com.food.kuruyia.foodretriever.utils.IFabInteract;
import com.food.kuruyia.foodretriever.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ScreenStats extends Fragment implements IFabInteract, IDataChange {
    GraphView m_graphDogWeight;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.screen_stats, container, false);

        m_graphDogWeight = view.findViewById(R.id.graphDogWeight);

        return view;
    }

    @Override
    public void onFabInteract() {

    }

    @Override
    public boolean hasFab() {
        return false;
    }

    @Override
    public void onDataChanged(DataType dataType, JsonObject data) {
        switch (dataType) {
            case DATA_STATS_WEIGHT: {
                if (data.has("values")) {
                    JsonArray values = data.getAsJsonArray("values");

                    List<DataPoint> list = new ArrayList<>();
                    for (int i = 0; i < values.size(); i++) {
                        list.add(new DataPoint(i, values.get(i).getAsFloat()));
                    }

                    DataPoint[] dp = new DataPoint[list.size()];
                    dp = list.toArray(dp);
                    LineGraphSeries<DataPoint> pointsDogWeight = new LineGraphSeries<>(dp);
                    pointsDogWeight.setDrawBackground(true);
                    pointsDogWeight.setDrawDataPoints(true);

                    m_graphDogWeight.removeAllSeries();
                    m_graphDogWeight.addSeries(pointsDogWeight);
                    m_graphDogWeight.invalidate();
                }

                break;
            }
        }
    }

    @Override
    public void onChangeData(DataType dataType, JsonObject data) {

    }
}
