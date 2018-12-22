package com.food.kuruyia.foodretriever.mainscreen.stats;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.food.kuruyia.foodretriever.utils.DataType;
import com.food.kuruyia.foodretriever.utils.IDataChange;
import com.food.kuruyia.foodretriever.utils.IFabInteract;
import com.food.kuruyia.foodretriever.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ScreenStats extends Fragment implements IFabInteract, IDataChange {
    private DataStats m_dataStats;

    private LineChart m_graphDogWeight;
    private LineChart m_graphFoodAvailability;
    private BarChart m_graphDogArrival;

    private int m_colorDogWeight;
    private int m_colorFoodAvailability;
    private int m_colorDogArrival;

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

        m_graphDogWeight = view.findViewById(R.id.graphDogWeight);
        m_graphFoodAvailability = view.findViewById(R.id.graphFoodAvail);
        m_graphDogArrival = view.findViewById(R.id.graphDogArrival);

        setupGraph(m_graphDogWeight);
        setupGraph(m_graphFoodAvailability);
        setupGraph(m_graphDogArrival);

        m_colorDogWeight = getResources().getColor(R.color.graphBlueBackground);
        m_colorFoodAvailability = getResources().getColor(R.color.graphOrangeBackground);
        m_colorDogArrival = getResources().getColor(R.color.graphGreenBackground);

        Bundle args = getArguments();
        if (args != null) {
            m_dataStats = args.getParcelable("statsData");
            loadGraphs();
        }

        return view;
    }

    private void setupGraph(Chart graph) {
        graph.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        graph.getDescription().setEnabled(false);
        graph.getLegend().setEnabled(false);

        if (graph instanceof LineChart)
            ((LineChart)graph).getAxisLeft().setEnabled(false);
        else if (graph instanceof BarChart)
            ((BarChart)graph).setEnabled(false);
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

    private void manageBarGraph(JsonArray values, BarChart graph, @ColorInt int color) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < values.size(); i++)
            entries.add(new BarEntry(i, values.get(i).getAsInt()));

        BarDataSet barDataSet = new BarDataSet(entries, "");
        barDataSet.setColor(color);

        BarData barData = new BarData(barDataSet);

        graph.setData(barData);
        graph.invalidate();
    }

    public void loadGraphs() {
        manageLineGraph(m_dataStats.getDogWeightValues(), m_graphDogWeight, m_colorDogWeight, new WeightFormatter());
        manageLineGraph(m_dataStats.getFoodAvailabilityValues(), m_graphFoodAvailability, m_colorFoodAvailability, new PercentFormatter(), 100);
        manageBarGraph(m_dataStats.getDogArrivalValues(), m_graphDogArrival, m_colorDogArrival);
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
                    manageLineGraph(m_dataStats.getDogWeightValues(), m_graphDogWeight, m_colorDogWeight, new WeightFormatter());
                }

                break;
            }
            case DATA_STATS_REMAINING_FOOD: {
                if (data.has("values")) {
                    m_dataStats.setFoodAvailabilityValues(data.getAsJsonArray("values"));
                    manageLineGraph(m_dataStats.getFoodAvailabilityValues(), m_graphFoodAvailability, m_colorFoodAvailability, new PercentFormatter(), 100);
                }

                break;
            }
            case DATA_STATS_DOG_ARRIVAL: {
                if (data.has("values")) {
                    m_dataStats.setDogArrivalValues(data.getAsJsonArray("values"));
                    manageBarGraph(m_dataStats.getDogArrivalValues(), m_graphDogArrival, m_colorDogArrival);
                }

                break;
            }
        }
    }

    @Override
    public void onChangeData(DataType dataType, JsonObject data) {

    }
}
