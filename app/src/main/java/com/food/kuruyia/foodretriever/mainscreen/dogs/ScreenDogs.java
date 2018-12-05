package com.food.kuruyia.foodretriever.mainscreen.dogs;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.food.kuruyia.foodretriever.mainscreen.MainActivity;
import com.food.kuruyia.foodretriever.utils.DataType;
import com.food.kuruyia.foodretriever.utils.IDataChange;
import com.food.kuruyia.foodretriever.utils.IFabInteract;
import com.food.kuruyia.foodretriever.R;
import com.food.kuruyia.foodretriever.websocket.RequestFormatter;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import org.w3c.dom.Text;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ScreenDogs extends Fragment implements IFabInteract, IDataChange {
    DataDogs m_dataDogs;
    
    TextInputLayout m_inputDogName;

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

        m_inputDogName = view.findViewById(R.id.inputDogName);

        TextView textDogWeight = view.findViewById(R.id.textDogWeight);
        CheckBox checkBoxWeightRegulation = view.findViewById(R.id.checkBoxWeightRegulation);
        final TextInputLayout inputWeightRegulation = view.findViewById(R.id.inputWeightRegulation);

        if (m_inputDogName.getEditText() != null) {
            m_inputDogName.getEditText().setText(m_dataDogs.getDogName());
            m_inputDogName.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!m_dataDogs.getDogName().equals(m_inputDogName.getEditText().getText().toString()))
                        m_inputDogName.setBoxStrokeColor(getResources().getColor(R.color.warningOrange));
                    else
                        m_inputDogName.setBoxStrokeColor(getResources().getColor(R.color.colorSecondary));
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            setupTextInput(DataType.DATA_DOG_NAME, m_inputDogName);
        }

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

    private void setupTextInput(final DataType dataType, final TextInputLayout inputLayout) {
        if (inputLayout.getEditText() != null) {
            inputLayout.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        processTextInput(dataType, inputLayout.getEditText().getText().toString());
                    }
                }
            });
            inputLayout.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                            event != null &&
                                    event.getAction() == KeyEvent.ACTION_DOWN &&
                                    event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        if (event == null || !event.isShiftPressed()) {
                            processTextInput(dataType, inputLayout.getEditText().getText().toString());

                            return true;
                        }
                    }
                    return false;
                }
            });
        }
    }
    
    private void processTextInput(DataType dataType, String value) {
        JsonObject data = new JsonObject();
        data.addProperty("value", value);
        
        onChangeData(dataType, data);
    }

    @Override
    public void onDataChanged(DataType dataType, JsonObject data) {
        switch (dataType) {
            case DATA_DOG_NAME: {
                if (data.has("value")) {
                    String newName = data.get("value").getAsString();

                    m_dataDogs.setDogName(newName);
                    m_inputDogName.setBoxStrokeColor(getResources().getColor(R.color.colorSecondary));
                }

                break;
            }
        }
    }

    @Override
    public void onChangeData(DataType dataType, JsonObject data) {
        sendRequest(RequestFormatter.format(dataType, data));
    }

    private void sendRequest(String req) {
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity)getActivity();
            activity.getServiceCommunicator().sendMessage(req);
        }
    }
}
