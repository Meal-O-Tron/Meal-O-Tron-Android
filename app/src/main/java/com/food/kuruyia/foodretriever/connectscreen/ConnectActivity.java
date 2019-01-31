package com.food.kuruyia.foodretriever.connectscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.food.kuruyia.foodretriever.R;
import com.google.android.material.textfield.TextInputLayout;

public class ConnectActivity extends AppCompatActivity {

    TextInputLayout m_inputIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        m_inputIP = findViewById(R.id.inputIP);

        if (savedInstanceState != null) {
            m_inputIP.getEditText().setText(savedInstanceState.getString("ConnAct.IP"));
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("ConnAct.IP", m_inputIP.getEditText().getText().toString());
    }
}
