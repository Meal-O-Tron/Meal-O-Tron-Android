package com.food.kuruyia.foodretriever.connectscreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.food.kuruyia.foodretriever.R;
import com.food.kuruyia.foodretriever.mainscreen.MainActivity;
import com.food.kuruyia.foodretriever.mainscreen.schedule.AdapterSchedule;
import com.food.kuruyia.foodretriever.websocket.WebSocketServiceCommunicator;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class ConnectActivity extends AppCompatActivity
    implements WebSocketServiceCommunicator.IWebSocketStateChange, WebSocketServiceCommunicator.IWebSocketMessage, ServiceDiscoveryCallback, IDiscoveryCallback {

    private static final String TAG = "ConnectActivity";

    TextInputLayout m_inputIP;
    Button m_buttonConnect;
    private RecyclerView.Adapter m_adapter;

    WebSocketServiceCommunicator m_serviceCommunicator = new WebSocketServiceCommunicator(this);
    ServiceDiscovery m_serviceDiscovery;

    private ArrayList<DiscoveredItem> m_discoveredItemsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        RecyclerView recyclerView = findViewById(R.id.discoveredRecycler);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        m_adapter = new AdapterDiscovered(m_discoveredItemsList, this);
        recyclerView.setAdapter(m_adapter);

        m_inputIP = findViewById(R.id.inputIP);
        m_buttonConnect = findViewById(R.id.buttonConnect);

        m_serviceDiscovery = new ServiceDiscovery(this);

        if (savedInstanceState != null) {
            String savedIP = savedInstanceState.getString("ConnAct.IP");

            m_inputIP.getEditText().setText(savedIP);
            m_buttonConnect.setEnabled(!savedIP.trim().isEmpty());
        }

        m_buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect(m_inputIP.getEditText().getText().toString());
            }
        });

        m_inputIP.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                m_buttonConnect.setEnabled(!s.toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        m_serviceCommunicator.setMessageCallback(this);
        m_serviceCommunicator.setStateChangedCallback(this);

        m_serviceDiscovery.setCallback(this);
        m_serviceDiscovery.start();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("ConnAct.IP", m_inputIP.getEditText().getText().toString());
    }

    private void connect(String ip) {
        if (m_serviceCommunicator.isBound())
            m_serviceCommunicator.connectWebsocket(formatAddress(ip));
    }

    private String formatAddress(String ip) {
        StringBuilder builder = new StringBuilder();
        builder.append("ws://");
        builder.append(ip);
        builder.append(":8000/");

        return builder.toString();
    }

    @Override
    protected void onStart() {
        super.onStart();

        m_serviceCommunicator.bind();
    }

    protected void onStop() {
        if (!isFinishing())
            m_serviceCommunicator.unbind();

        super.onStop();
    }

    @Override
    public void finish() {
        super.finish();

        m_serviceCommunicator.unbind();
    }

    @Override
    public void onWSFailure() {

    }

    @Override
    public void onWSConnectionStatusChanged(boolean isConnected) {
        if (isConnected) {
            Intent mainLaunch = new Intent(ConnectActivity.this, MainActivity.class);
            mainLaunch.putExtra(MainActivity.EXTRA_ADDRESS, formatAddress(m_inputIP.getEditText().getText().toString()));

            startActivity(mainLaunch);
            m_serviceCommunicator.unbind();
        }
    }

    @Override
    public void onWSBindingChanged(boolean isBound) {

    }

    @Override
    public void onWSServiceStopped() {

    }

    @Override
    public void onWSMessage(String message) {

    }

    @Override
    public void onServiceDiscoveryMessage(String dogName, String ip) {
        for (int i = 0; i < m_discoveredItemsList.size(); i++) {
            if (m_discoveredItemsList.get(i).getIp().equals(ip))
                return;
        }

        m_discoveredItemsList.add(new DiscoveredItem(ip, dogName));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                m_adapter.notifyItemInserted(m_discoveredItemsList.size() - 1);
            }
        });
    }

    @Override
    public void onDiscoveredClick(String ip) {
        connect(ip);
    }
}
