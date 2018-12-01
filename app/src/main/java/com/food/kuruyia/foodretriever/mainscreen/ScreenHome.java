package com.food.kuruyia.foodretriever.mainscreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.food.kuruyia.foodretriever.utils.IFabInteract;
import com.food.kuruyia.foodretriever.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ScreenHome extends Fragment implements IFabInteract {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.screen_home, container, false);

        return view;
    }

    @Override
    public void onFabInteract() {

    }

    @Override
    public boolean hasFab() {
        return false;
    }
}
