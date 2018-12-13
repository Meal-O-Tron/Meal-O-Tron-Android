package com.food.kuruyia.foodretriever.mainscreen;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.food.kuruyia.foodretriever.utils.DataType;
import com.food.kuruyia.foodretriever.utils.IDataChange;
import com.food.kuruyia.foodretriever.utils.IFabInteract;
import com.food.kuruyia.foodretriever.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ScreenStats extends Fragment implements IFabInteract, IDataChange {
    private GraphView m_graphDogWeight;
    private GraphView m_graphFoodAvailability;
    private GraphView m_graphDogArrival;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.screen_stats, container, false);

        m_graphDogWeight = view.findViewById(R.id.graphDogWeight);
        m_graphFoodAvailability = view.findViewById(R.id.graphFoodAvail);
        m_graphDogArrival = view.findViewById(R.id.graphDogArrival);

        return view;
    }

    private void manageLineGraph(JsonArray values, GraphView graph, @ColorInt int backgroundColor, @ColorInt int dotColor) {
        List<DataPoint> list = new ArrayList<>();
        for (int i = 0; i < values.size(); i++)
            list.add(new DataPoint(i, values.get(i).getAsFloat()));

        DataPoint[] dp = new DataPoint[list.size()];
        dp = list.toArray(dp);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dp);
        series.setDrawBackground(true);
        series.setDrawDataPoints(true);
        series.setColor(dotColor);
        series.setBackgroundColor(backgroundColor);

        graph.removeAllSeries();
        graph.addSeries(series);
        graph.invalidate();
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

                    manageLineGraph(values, m_graphDogWeight, getResources().getColor(R.color.graphBlueBackground), getResources().getColor(R.color.graphBlueDots));
                }

                break;
            }
            case DATA_STATS_REMAINING_FOOD: {
                if (data.has("values")) {
                    JsonArray values = data.getAsJsonArray("values");

                    manageLineGraph(values, m_graphFoodAvailability, getResources().getColor(R.color.graphOrangeBackground), getResources().getColor(R.color.graphOrangeDots));
                }

                break;
            }
            case DATA_STATS_DOG_ARRIVAL: {
                if (data.has("values")) {
                    JsonArray values = data.getAsJsonArray("values");

                    List<DataPoint> list = new ArrayList<>();
                    for (int i = 0; i < values.size(); i++)
                        list.add(new DataPoint(i, values.get(i).getAsFloat()));

                    DataPoint[] dp = new DataPoint[list.size()];
                    dp = list.toArray(dp);
                    BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dp);

                    series.setColor(getResources().getColor(R.color.graphGreenBackground));
                    series.setDrawValuesOnTop(true);
                    series.setValuesOnTopColor(Color.BLACK);
                    series.setSpacing(8);

                    m_graphDogArrival.removeAllSeries();
                    m_graphDogArrival.addSeries(series);
                    m_graphDogArrival.invalidate();
                }

                break;
            }
        }
    }

    @Override
    public void onChangeData(DataType dataType, JsonObject data) {

    }
}
