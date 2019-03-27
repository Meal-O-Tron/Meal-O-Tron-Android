package com.food.kuruyia.foodretriever.setupscreen;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.food.kuruyia.foodretriever.R;
import com.food.kuruyia.foodretriever.connectscreen.WrapContentLinearLayoutManager;
import com.food.kuruyia.foodretriever.utils.DataType;
import com.food.kuruyia.foodretriever.websocket.ResponseParser;
import com.food.kuruyia.foodretriever.websocket.WebSocketServiceCommunicator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import static com.food.kuruyia.foodretriever.mainscreen.MainActivity.EXTRA_ADDRESS;

public class SetupActivity extends AppCompatActivity
        implements WebSocketServiceCommunicator.IWebSocketStateChange, WebSocketServiceCommunicator.IWebSocketMessage {

    private static final String TAG = "SetupActivity";

    private AdapterNetwork m_adapter;
    private ArrayList<NetworkItem> m_networkList = new ArrayList<>();

    String m_serverAddress;

    WebSocketServiceCommunicator m_serviceCommunicator = new WebSocketServiceCommunicator(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        RecyclerView recyclerView = findViewById(R.id.setupRecycler);

        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this));

        m_adapter = new AdapterNetwork(m_networkList, this);
        recyclerView.setAdapter(m_adapter);

        m_serverAddress = getIntent().getStringExtra(EXTRA_ADDRESS);

        m_serviceCommunicator.setMessageCallback(this);
        m_serviceCommunicator.setStateChangedCallback(this);
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
        // TODO: Close MainActivity, go to ConnectActivity
        Toast.makeText(this, "Server disconnected", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onWSConnectionStatusChanged(boolean isConnected) {
        if (!isConnected)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    m_serviceCommunicator.connectWebsocket(m_serverAddress);
                }
            }, 1000);
    }

    @Override
    public void onWSBindingChanged(boolean isBound) {
        if (isBound && !m_serviceCommunicator.isConnected())
            m_serviceCommunicator.connectWebsocket(m_serverAddress);
    }

    @Override
    public void onWSServiceStopped() {

    }

    @Override
    public void onWSMessage(String message) {
        JsonElement jsonElement = new JsonParser().parse(message);
        if (jsonElement.isJsonObject()) {
            JsonObject json = jsonElement.getAsJsonObject();
            if (json.has("type") && json.has("data")) {
                int type = json.get("type").getAsInt();
                if (type == DataType.DATA_ESP_WIFI_SCAN.ordinal()) {
                    JsonArray networks = json.getAsJsonArray("data");
                    m_networkList.clear();

                    for (int i = 0; i < networks.size(); i++) {
                        JsonObject currentNetwork = networks.get(i).getAsJsonObject();

                        if (currentNetwork.has("ssid") && currentNetwork.has("rssi") && currentNetwork.has("encryption")) {
                            String ssid = currentNetwork.get("ssid").getAsString();
                            long rssi = currentNetwork.get("rssi").getAsLong();
                            int encryption = currentNetwork.get("encryption").getAsInt();

                            m_networkList.add(new NetworkItem(ssid, rssi, encryption));
                        } else {
                            Log.e(TAG, "Incomplete information for network #" + i);
                        }
                    }

                    m_adapter.notifyDataSetChanged();
                }
            }
        }
    }
}
