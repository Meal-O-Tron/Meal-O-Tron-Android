package com.food.kuruyia.foodretriever.mainscreen;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.food.kuruyia.foodretriever.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MenuBottomSheet extends BottomSheetDialogFragment {
    private Activity m_activity;

    private static final String TAG = "MenuBottomSheet";

    public static MenuBottomSheet newInstance(int actualScreen) {
        MenuBottomSheet myFragment = new MenuBottomSheet();

        Bundle args = new Bundle();
        args.putInt("actualScreen", actualScreen);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            m_activity = (Activity)context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.nav_bottom_sheet, container, false);

        NavigationView navigationView = view.findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                dismiss();

                if (m_activity instanceof NavigationItemSelected)
                    ((NavigationItemSelected)m_activity).onItemSelected(menuItem);

                return false;
            }
        });

        Bundle args = getArguments();
        if (args != null) {
            final int actualScreen = args.getInt("actualScreen");
            final MenuItem selectedItem = navigationView.getMenu().getItem(actualScreen);
            selectedItem.setChecked(true);
            selectedItem.getIcon().setTint(getResources().getColor(R.color.colorSecondary));
        }

        return view;
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

    public interface NavigationItemSelected {
        void onItemSelected(MenuItem item);
    }
}
