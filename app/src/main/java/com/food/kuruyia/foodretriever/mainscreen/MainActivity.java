package com.food.kuruyia.foodretriever.mainscreen;

import android.os.Bundle;

import com.food.kuruyia.foodretriever.R;
import com.food.kuruyia.foodretriever.mainscreen.dogs.DataDogs;
import com.food.kuruyia.foodretriever.mainscreen.dogs.ScreenDogs;
import com.food.kuruyia.foodretriever.mainscreen.schedule.DataSchedule;
import com.food.kuruyia.foodretriever.mainscreen.schedule.ScheduleItem;
import com.food.kuruyia.foodretriever.mainscreen.schedule.ScreenSchedule;
import com.food.kuruyia.foodretriever.mainscreen.stats.DataStats;
import com.food.kuruyia.foodretriever.mainscreen.stats.ScreenStats;
import com.food.kuruyia.foodretriever.mainscreen.stats.WeightFormatter;
import com.food.kuruyia.foodretriever.utils.DataType;
import com.food.kuruyia.foodretriever.utils.IFabInteract;
import com.food.kuruyia.foodretriever.websocket.RequestFormatter;
import com.food.kuruyia.foodretriever.websocket.ResponseParser;
import com.food.kuruyia.foodretriever.websocket.WebSocketServiceCommunicator;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements MenuBottomSheet.NavigationItemSelected, WebSocketServiceCommunicator.IWebSocketStateChange, WebSocketServiceCommunicator.IWebSocketMessage {

    private static final String TAG = "MainActivity";
    private static final String MENU_BOTTOM_SHEET = "Tag.BottomNavigation";
    public static final String EXTRA_ADDRESS = "Extra.Address";

    ScreenHome m_screenHome = new ScreenHome();

    ScreenStats m_screenStats;
    DataStats m_dataStats = new DataStats();

    ScreenSchedule m_screenSchedule;
    DataSchedule m_dataSchedule = new DataSchedule();

    ScreenDogs m_screenDogs;
    DataDogs m_dataDogs = new DataDogs();

    String m_serverAddress;
    WebSocketServiceCommunicator m_serviceCommunicator = new WebSocketServiceCommunicator(this);

    int m_selectedScreen = 0;
    boolean m_hasLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            m_dataStats = savedInstanceState.getParcelable("Save.DataStats");
            m_dataSchedule = savedInstanceState.getParcelable("Save.DataSchedule");
            m_dataDogs = savedInstanceState.getParcelable("Save.DataDogs");

            m_hasLoaded = savedInstanceState.getBoolean("Save.HasLoaded");
        }

        m_serverAddress = getIntent().getStringExtra(EXTRA_ADDRESS);

        m_screenStats = ScreenStats.newInstance(m_dataStats);
        m_screenSchedule = ScreenSchedule.newInstance(m_dataSchedule);
        m_screenDogs = ScreenDogs.newInstance(m_dataDogs);

        setFragmentFromSelectedScreen();

        BottomAppBar appBar = findViewById(R.id.bar);
        setSupportActionBar(appBar);
        appBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuBottomSheet.newInstance(m_selectedScreen).show(getSupportFragmentManager(), MENU_BOTTOM_SHEET);
            }
        });

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

        m_serviceCommunicator.disconnectWebsocket();
        m_serviceCommunicator.unbind();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Snackbar.make(findViewById(R.id.mainActivityLayout), "Test", Snackbar.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("Save.DataStats", m_dataStats);
        outState.putParcelable("Save.DataSchedule", m_dataSchedule);
        outState.putParcelable("Save.DataDogs", m_dataDogs);

        outState.putBoolean("Save.HasLoaded", m_hasLoaded);
    }

    @Override
    public void onItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                m_selectedScreen = 0;
                break;
            case R.id.nav_graph:
                m_selectedScreen = 1;
                break;
            case R.id.nav_alarm:
                m_selectedScreen = 2;
                break;
            case R.id.nav_dogs:
                m_selectedScreen = 3;
                break;
        }

        setFragmentFromSelectedScreen();
    }

    private void setFragmentFromSelectedScreen() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        FloatingActionButton fab = findViewById(R.id.fab);
        Fragment selectedFragment = null;

        if (m_selectedScreen == 0) {
            selectedFragment = m_screenHome;
        } else if (m_selectedScreen == 1) {
            selectedFragment = m_screenStats;
        } else if (m_selectedScreen == 2) {
            selectedFragment = m_screenSchedule;
        } else if (m_selectedScreen == 3) {
            selectedFragment = m_screenDogs;
        }

        if (selectedFragment != null) {
            transaction.replace(R.id.mainContentLayout, selectedFragment);

            if (selectedFragment instanceof IFabInteract) {
                final IFabInteract fabFragment = (IFabInteract)selectedFragment;

                if (fabFragment.hasFab()) fab.show(); else fab.hide();
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fabFragment.onFabInteract();
                    }
                });
            }
        }

        transaction.commit();
    }

    public WebSocketServiceCommunicator getServiceCommunicator() {
        return m_serviceCommunicator;
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

        if (!m_hasLoaded && isBound && m_serviceCommunicator.isConnected())
            m_serviceCommunicator.sendMessage(RequestFormatter.format(DataType.DATA_GLOBAL_RELOAD, new JsonObject()));
    }

    @Override
    public void onWSServiceStopped() {

    }

    @Override
    public void onWSMessage(String message) {
        Log.d(TAG, "WS Received: " + message);

        ResponseParser parser = new ResponseParser(message);
        if (parser.isReady()) {
            final int responseType = parser.getType().ordinal();

            if (responseType > DataType.DATA_STATS_START.ordinal() && responseType < DataType.DATA_STATS_END.ordinal()) {
                m_screenStats.onDataChanged(parser.getType(), parser.getData());
            } else if (responseType > DataType.DATA_SCHEDULE_START.ordinal() && responseType < DataType.DATA_SCHEDULE_END.ordinal()) {
                m_screenSchedule.onDataChanged(parser.getType(), parser.getData());
            } else if (responseType > DataType.DATA_DOG_START.ordinal() && responseType < DataType.DATA_DOG_END.ordinal()) {
                m_screenDogs.onDataChanged(parser.getType(), parser.getData());
            } else if (parser.getType() == DataType.DATA_GLOBAL_RELOAD) {
                m_hasLoaded = true;

                JsonObject data = parser.getData();
                if (data.has("stats")) {
                    JsonObject statsObject = data.getAsJsonObject("stats");
                    if (statsObject.has("weight") && statsObject.has("food") && statsObject.has("arrival")) {
                        m_dataStats.setDogWeightValues(statsObject.getAsJsonArray("weight"));
                        m_dataStats.setFoodAvailabilityValues(statsObject.getAsJsonArray("food"));
                        m_dataStats.setDogArrivalValues(statsObject.getAsJsonArray("arrival"));
                    }

                    if (m_screenStats.getView() != null)
                        m_screenStats.loadGraphs();
                }

                if (data.has("schedule")) {
                    JsonArray scheduleArray = data.getAsJsonArray("schedule");
                    for (int i = 0; i < scheduleArray.size(); i++) {
                        JsonObject actualSchedule = scheduleArray.get(i).getAsJsonObject();
                        if (actualSchedule.has("hour") && actualSchedule.has("minute") && actualSchedule.has("enabled") && actualSchedule.has("ratio")) {
                            m_dataSchedule.addItem(new ScheduleItem(actualSchedule.get("hour").getAsInt(),
                                    actualSchedule.get("minute").getAsInt(),
                                    actualSchedule.get("ratio").getAsInt(),
                                    i,
                                    actualSchedule.get("enabled").getAsBoolean()
                            ));
                        }
                    }

                    if (m_screenSchedule.getView() != null)
                        m_screenSchedule.loadSchedule();
                }

                if (data.has("dog")) {
                    JsonObject dogObject = data.getAsJsonObject("dog");
                    if (dogObject.has("name") && dogObject.has("weight") && dogObject.has("weight_reg") && dogObject.has("weight_reg_value")) {
                        m_dataDogs.setDogName(dogObject.get("name").getAsString());
                        m_dataDogs.setActualWeight(dogObject.get("weight").getAsFloat());
                        m_dataDogs.setWeightRegulated(dogObject.get("weight_reg").getAsBoolean());
                        m_dataDogs.setExpectedWeight(dogObject.get("weight_reg_value").getAsInt());
                    }

                    if (m_screenDogs.getView() != null)
                        m_screenDogs.loadData();
                }
            }
        }
    }
}
