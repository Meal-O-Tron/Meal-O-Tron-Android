package com.food.kuruyia.foodretriever.mainscreen.dogs;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.food.kuruyia.foodretriever.utils.IFabInteract;
import com.food.kuruyia.foodretriever.R;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ScreenDogs extends Fragment implements IFabInteract {
    DataDogs m_dataDogs;

    final static String TAG = "ScreenDogs";

    public static ScreenDogs newInstance(DataDogs data) {
        ScreenDogs myFragment = new ScreenDogs();

        Bundle args = new Bundle();
        args.putParcelable("scheduleData", data);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.screen_dogs, container, false);

        Bundle args = getArguments();
        if (args != null) {
            m_dataDogs = args.getParcelable("scheduleData");
        }

        final TextInputLayout inputDogName = view.findViewById(R.id.inputDogName);

        TextView textDogWeight = view.findViewById(R.id.textDogWeight);
        CheckBox checkBoxWeightRegulation = view.findViewById(R.id.checkBoxWeightRegulation);
        final TextInputLayout inputWeightRegulation = view.findViewById(R.id.inputWeightRegulation);

        inputDogName.getEditText().setText(m_dataDogs.getDogName());
        inputDogName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                m_dataDogs.setDogName(String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        textDogWeight.setText(String.format(getResources().getString(R.string.dog_weight), m_dataDogs.getActualWeight()));

        checkBoxWeightRegulation.setChecked(m_dataDogs.isWeightRegulated());
        checkBoxWeightRegulation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                inputWeightRegulation.setEnabled(isChecked);
                m_dataDogs.setWeightRegulated(isChecked);
            }
        });

        inputWeightRegulation.setEnabled(checkBoxWeightRegulation.isChecked());
        inputWeightRegulation.getEditText().setText(m_dataDogs.getExpectedWeight() > 0 ? String.valueOf(m_dataDogs.getExpectedWeight()) : "");
        inputWeightRegulation.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 3)
                    inputWeightRegulation.getEditText().setText("999");

                String weight = inputWeightRegulation.getEditText().getText().toString();
                m_dataDogs.setExpectedWeight(weight.length() > 0 ? Integer.valueOf(weight) : 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
