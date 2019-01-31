package com.food.kuruyia.foodretriever.mainscreen.dogs;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ScreenDogs extends Fragment implements IFabInteract, IDataChange {
    private DataDogs m_dataDogs;
    
    private TextInputLayout m_inputDogName;
    private TextInputLayout m_inputWeightRegulation;
    private CheckBox m_checkWeightRegulation;
    private TextView m_textDogWeight;

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
        m_textDogWeight = view.findViewById(R.id.textDogWeight);
        m_checkWeightRegulation = view.findViewById(R.id.checkBoxWeightRegulation);
        m_inputWeightRegulation = view.findViewById(R.id.inputWeightRegulation);
        Button buttonWeigh = view.findViewById(R.id.buttonWeigh);

        if (m_inputDogName.getEditText() != null) {
            m_inputDogName.getEditText().setText(m_dataDogs.getDogName());
            m_inputDogName.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (isAdded()) {
                        // Changer la couleur si le nom diffère de celui présent dans le serveur
                        if (!m_dataDogs.getDogName().equals(m_inputDogName.getEditText().getText().toString()))
                            m_inputDogName.setBoxStrokeColor(getResources().getColor(R.color.warningOrange));
                        else
                            m_inputDogName.setBoxStrokeColor(getResources().getColor(R.color.colorSecondary));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            setupTextInput(DataType.DATA_DOG_NAME, m_inputDogName);
        }

        String formattedWeight = new DecimalFormat("#.#").format(m_dataDogs.getActualWeight());
        m_textDogWeight.setText(String.format(getResources().getString(R.string.dog_weight), formattedWeight));

        m_checkWeightRegulation.setChecked(m_dataDogs.isWeightRegulated());
        m_checkWeightRegulation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked != m_dataDogs.isWeightRegulated()) {
                    JsonObject data = new JsonObject();
                    data.addProperty("value", isChecked);

                    onChangeData(DataType.DATA_DOG_REGULATION_ENABLE, data);
                }
            }
        });

        m_inputWeightRegulation.setEnabled(m_checkWeightRegulation.isChecked());
        if (m_inputWeightRegulation.getEditText() != null) {
            m_inputWeightRegulation.getEditText().setText(m_dataDogs.getExpectedWeight() > 0 ? String.valueOf(m_dataDogs.getExpectedWeight()) : "");
            m_inputWeightRegulation.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    EditText editText = m_inputWeightRegulation.getEditText();
                    if (s.length() > 3)
                        editText.setText("999");

                    if (isAdded()) {
                        if (m_dataDogs.getExpectedWeight() != Integer.valueOf(editText.getText().toString()))
                            m_inputWeightRegulation.setBoxStrokeColor(getResources().getColor(R.color.warningOrange));
                        else
                            m_inputWeightRegulation.setBoxStrokeColor(getResources().getColor(R.color.colorSecondary));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            setupTextInput(DataType.DATA_DOG_REGULATION_VALUE, m_inputWeightRegulation);
        }

        buttonWeigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChangeData(DataType.DATA_DOG_WEIGHT, new JsonObject());
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
                        String text = inputLayout.getEditText().getText().toString();
                        String actualValue = getValueFromDataType(dataType);

                        if (actualValue != null) {
                            try {
                                int textNum = Integer.parseInt(text);
                                int actualValueNum = Integer.parseInt(actualValue);

                                if (textNum != actualValueNum)
                                    processTextInput(dataType, textNum);
                            } catch (NumberFormatException e) {
                                if (!actualValue.equals(text))
                                    processTextInput(dataType, text);
                            }
                        }
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
                            String text = inputLayout.getEditText().getText().toString();
                            String actualValue = getValueFromDataType(dataType);

                            if (actualValue != null) {
                                try {
                                    int textNum = Integer.parseInt(text);
                                    int actualValueNum = Integer.parseInt(actualValue);

                                    if (textNum != actualValueNum)
                                        processTextInput(dataType, textNum);
                                } catch (NumberFormatException e) {
                                    if (!actualValue.equals(text))
                                        processTextInput(dataType, text);
                                }

                                return true;
                            }
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

    private void processTextInput(DataType dataType, int value) {
        JsonObject data = new JsonObject();
        data.addProperty("value", value);

        onChangeData(dataType, data);
    }

    private String getValueFromDataType(DataType dataType) {
        switch (dataType) {
            case DATA_DOG_NAME:
                return m_dataDogs.getDogName();
            case DATA_DOG_WEIGHT:
                return String.valueOf(m_dataDogs.getActualWeight());
            case DATA_DOG_REGULATION_VALUE:
                return String.valueOf(m_dataDogs.getExpectedWeight());
            default:
                return null;
        }
    }

    @Override
    public void onDataChanged(DataType dataType, JsonObject data) {
        switch (dataType) {
            case DATA_DOG_NAME: {
                if (data.has("value") && m_inputDogName.getEditText() != null) {
                    String newName = data.get("value").getAsString();

                    m_dataDogs.setDogName(newName);
                    m_inputDogName.getEditText().setText(newName);
                }

                break;
            }
            case DATA_DOG_REGULATION_VALUE: {
                if (data.has("value") && m_inputWeightRegulation.getEditText() != null) {
                    int newVal = data.get("value").getAsInt();

                    m_dataDogs.setExpectedWeight(newVal);
                    m_inputWeightRegulation.getEditText().setText(String.valueOf(newVal));
                }

                break;
            }
            case DATA_DOG_REGULATION_ENABLE: {
                if (data.has("value")) {
                    boolean val = data.get("value").getAsBoolean();
                    
                    setWeightRegulation(val);
                    m_dataDogs.setWeightRegulated(val);
                }
                
                break;
            }
            case DATA_DOG_WEIGHT: {
                if (data.has("value")) {
                    int val = data.get("value").getAsInt();

                    String formattedWeight = new DecimalFormat("#.#").format(m_dataDogs.getActualWeight());
                    m_textDogWeight.setText(String.format(getResources().getString(R.string.dog_weight), formattedWeight));
                    m_dataDogs.setActualWeight(val);
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

    public void loadData() {
        if (m_inputDogName.getEditText() != null)
            m_inputDogName.getEditText().setText(m_dataDogs.getDogName());

        if (m_inputWeightRegulation.getEditText() != null)
            m_inputWeightRegulation.getEditText().setText(String.valueOf(m_dataDogs.getExpectedWeight()));

        String formattedWeight = new DecimalFormat("#.#").format(m_dataDogs.getActualWeight());
        m_textDogWeight.setText(String.format(getResources().getString(R.string.dog_weight), formattedWeight));

        setWeightRegulation(m_dataDogs.isWeightRegulated());
    }

    private void setWeightRegulation(boolean enabled) {
        m_checkWeightRegulation.setChecked(enabled);
        m_inputWeightRegulation.setEnabled(enabled);
    }
}
