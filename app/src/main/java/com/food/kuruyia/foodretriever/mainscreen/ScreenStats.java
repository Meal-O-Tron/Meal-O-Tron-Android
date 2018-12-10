package com.food.kuruyia.foodretriever.mainscreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.food.kuruyia.foodretriever.utils.DataType;
import com.food.kuruyia.foodretriever.utils.IDataChange;
import com.food.kuruyia.foodretriever.utils.IFabInteract;
import com.food.kuruyia.foodretriever.R;
import com.google.gson.JsonObject;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ScreenStats extends Fragment implements IFabInteract, IDataChange {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.screen_stats, container, false);

        GraphView graphDogWeight = view.findViewById(R.id.graphDogWeight);

        LineGraphSeries<DataPoint> pointsDogWeight = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(1, 1),
                new DataPoint(2, 2),
                new DataPoint(3, 3)
        });
        pointsDogWeight.setDrawBackground(true);
        graphDogWeight.addSeries(pointsDogWeight);

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

    }

    @Override
    public void onChangeData(DataType dataType, JsonObject data) {

    }
}
