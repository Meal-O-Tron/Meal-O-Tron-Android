package com.food.kuruyia.foodretriever.mainscreen.stats;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.food.kuruyia.foodretriever.mainscreen.dogs.DataDogs;
import com.food.kuruyia.foodretriever.mainscreen.dogs.ScreenDogs;
import com.food.kuruyia.foodretriever.utils.DataType;
import com.food.kuruyia.foodretriever.utils.IDataChange;
import com.food.kuruyia.foodretriever.utils.IFabInteract;
import com.food.kuruyia.foodretriever.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
    private DataStats m_dataStats;

    private LineChart m_graphDogWeight;
    private LineChart m_graphFoodAvailability;
    private GraphView m_graphDogArrival;

    private final String TAG = "ScreenStats";

    public static ScreenStats newInstance(DataStats data) {
        ScreenStats myFragment = new ScreenStats();

        Bundle args = new Bundle();
        args.putParcelable("statsData", data);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.screen_stats, container, false);

        m_graphFoodAvailability = view.findViewById(R.id.graphFoodAvail);
        m_graphDogWeight = view.findViewById(R.id.graphDogWeight);
        m_graphDogArrival = view.findViewById(R.id.graphDogArrival);

        setupLineGraph(m_graphDogWeight);
        setupLineGraph(m_graphFoodAvailability);

        Bundle args = getArguments();
        if (args != null) {
            m_dataStats = args.getParcelable("statsData");

            loadGraphs();
        }

        return view;
    }

    private void setupLineGraph(LineChart graph) {
        graph.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        graph.getAxisLeft().setEnabled(false);
        graph.getDescription().setEnabled(false);
        graph.getLegend().setEnabled(false);
    }

    private void manageLineGraph(JsonArray values, LineChart graph, @ColorInt int color, IValueFormatter valueFormatter, int multiplier) {
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < values.size(); i++)
            entries.add(new Entry(i, values.get(i).getAsFloat() * multiplier));

        LineDataSet lineDataSet = new LineDataSet(entries, "");
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(color);
        lineDataSet.setFillColor(color);

        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setValueFormatter(valueFormatter);

        LineData lineData = new LineData(lineDataSet);

        graph.setData(lineData);
        graph.invalidate();
    }

    private void manageLineGraph(JsonArray values, LineChart graph, @ColorInt int color, IValueFormatter valueFormatter) {
        manageLineGraph(values, graph, color, valueFormatter, 1);
    }

    private void manageBarGraph(JsonArray values, GraphView graph, @ColorInt int backgroundColor) {
        List<DataPoint> list = new ArrayList<>();
        for (int i = 0; i < values.size(); i++)
            list.add(new DataPoint(i, values.get(i).getAsFloat()));

        DataPoint[] dp = new DataPoint[list.size()];
        dp = list.toArray(dp);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dp);

        series.setColor(backgroundColor);
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.BLACK);
        series.setSpacing(8);

        graph.removeAllSeries();
        graph.addSeries(series);
        graph.invalidate();
    }

    private void loadGraphs() {
        manageLineGraph(m_dataStats.getDogWeightValues(), m_graphDogWeight, getResources().getColor(R.color.graphBlueBackground), new WeightFormatter());
        manageLineGraph(m_dataStats.getFoodAvailabilityValues(), m_graphFoodAvailability, getResources().getColor(R.color.graphOrangeBackground), new PercentFormatter(), 100);
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
                    m_dataStats.setDogWeightValues(data.getAsJsonArray("values"));
                    manageLineGraph(m_dataStats.getDogWeightValues(), m_graphDogWeight, getResources().getColor(R.color.graphBlueBackground), new WeightFormatter());
                }

                break;
            }
            case DATA_STATS_REMAINING_FOOD: {
                if (data.has("values")) {
                    m_dataStats.setFoodAvailabilityValues(data.getAsJsonArray("values"));
                    manageLineGraph(m_dataStats.getFoodAvailabilityValues(), m_graphFoodAvailability, getResources().getColor(R.color.graphOrangeBackground), new PercentFormatter(), 100);
                }

                break;
            }
            case DATA_STATS_DOG_ARRIVAL: {
                if (data.has("values")) {
                    m_dataStats.setDogArrivalValues(data.getAsJsonArray("values"));
                    manageBarGraph(m_dataStats.getDogArrivalValues(), m_graphDogArrival, getResources().getColor(R.color.graphGreenBackground));
                }

                break;
            }
        }
    }

    @Override
    public void onChangeData(DataType dataType, JsonObject data) {

    }

    @Override
    public void reloadData(JsonObject data) {
        if (data.has("weight") && data.has("food") && data.has("arrival")) {
            m_dataStats.setDogWeightValues(data.getAsJsonArray("weight"));
            Log.d(TAG, m_dataStats.getDogWeightValues().toString());
            manageLineGraph(m_dataStats.getDogWeightValues(), m_graphDogWeight, getResources().getColor(R.color.graphBlueBackground), new WeightFormatter());

            m_dataStats.setFoodAvailabilityValues(data.getAsJsonArray("food"));
            manageLineGraph(m_dataStats.getFoodAvailabilityValues(), m_graphFoodAvailability, getResources().getColor(R.color.graphOrangeBackground), new PercentFormatter(), 100);

            m_dataStats.setDogArrivalValues(data.getAsJsonArray("arrival"));
            manageBarGraph(m_dataStats.getDogArrivalValues(), m_graphDogArrival, getResources().getColor(R.color.graphGreenBackground));
        }
    }
}
