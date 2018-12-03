package com.food.kuruyia.foodretriever.mainscreen.schedule;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.food.kuruyia.foodretriever.mainscreen.MainActivity;
import com.food.kuruyia.foodretriever.utils.DataType;
import com.food.kuruyia.foodretriever.utils.IDataChange;
import com.food.kuruyia.foodretriever.utils.IFabInteract;
import com.food.kuruyia.foodretriever.R;
import com.food.kuruyia.foodretriever.websocket.RequestFormatter;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import java.util.Calendar;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ScreenSchedule extends Fragment implements IFabInteract, IDataChange {
    private RecyclerView.Adapter m_adapter;
    private TextView m_textOverallRatio;
    private ProgressBar m_progressOverallRatio;

    private DataSchedule m_dataSchedule;

    final static String TAG = "ScreenSchedule";

    public static ScreenSchedule newInstance(DataSchedule data) {
        ScreenSchedule myFragment = new ScreenSchedule();

        Bundle args = new Bundle();
        args.putParcelable("scheduleData", data);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.screen_schedule, container, false);

        Bundle args = getArguments();
        if (args != null) {
            m_dataSchedule = args.getParcelable("scheduleData");
        } else {
            m_dataSchedule = new DataSchedule();
        }

        if (m_dataSchedule != null)
            m_dataSchedule.addDataChangeListener(this);

        RecyclerView recyclerView = view.findViewById(R.id.scheduleRecycler);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        m_adapter = new AdapterSchedule(m_dataSchedule, this);
        recyclerView.setAdapter(m_adapter);

        RecyclerView.ItemAnimator itemAnimator = recyclerView.getItemAnimator();
        if (itemAnimator instanceof DefaultItemAnimator)
        ((DefaultItemAnimator)itemAnimator).setSupportsChangeAnimations(false);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();

                final ScheduleItem savedItem = new ScheduleItem(m_dataSchedule.getItem(position));

                HashMap<String, Object> data = new HashMap<>();
                data.put("id", savedItem.getId());

                onChangeData(DataType.DATA_SCHEDULE_REMOVE, data);

                m_dataSchedule.removeItem(position);
                m_adapter.notifyItemRemoved(position);

                Snackbar deleteSnackbar = Snackbar.make(view, R.string.schedule_delete_snackbar, Snackbar.LENGTH_LONG);
                deleteSnackbar.setAction(R.string.string_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, Object> data = new HashMap<>();
                        data.put("hour", savedItem.getHour());
                        data.put("minute", savedItem.getMinutes());
                        data.put("ratio", savedItem.getRatio());
                        data.put("enabled", savedItem.isEnabled());

                        onChangeData(DataType.DATA_SCHEDULE_ADD, data);
                    }
                });
                deleteSnackbar.show();
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        m_textOverallRatio = view.findViewById(R.id.textOverallRatio);
        m_progressOverallRatio = view.findViewById(R.id.progressOverallRatio);
        setOverallRatioData(m_dataSchedule.getUsedRatio());

        return view;
    }

    @Override
    public void onFabInteract() {
        new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                HashMap<String, Object> data = new HashMap<>();
                data.put("hour", hourOfDay);
                data.put("minute", minute);

                sendRequest(RequestFormatter.format(DataType.DATA_SCHEDULE_ADD, data));
            }
        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), DateFormat.is24HourFormat(getActivity())).show();
    }

    @Override
    public boolean hasFab() {
        return true;
    }

    @Override
    public void onDataChanged(DataType dataType, JsonObject data) {
        // TODO: Handle schedule messages
        switch (dataType) {
            case DATA_SCHEDULE_ADD: {
                if (data.has("hour") && data.has("minute") && data.has("ratio") && data.has("id") && data.has("enabled")) {
                    int hour = data.get("hour").getAsInt();
                    int minute = data.get("minute").getAsInt();
                    int ratio = data.get("ratio").getAsInt();
                    int id = data.get("id").getAsInt();
                    boolean enabled = data.get("enabled").getAsBoolean();

                    int pos = m_dataSchedule.addItem(new ScheduleItem(hour, minute, ratio, id, enabled));

                    if (pos >= 0)
                        m_adapter.notifyItemInserted(pos);
                }

                break;
            }
            case DATA_SCHEDULE_REMOVE: {
                if (data.has("id")) {
                    int id = data.get("id").getAsInt();

                    final int pos = m_dataSchedule.findItemById(id);

                    if (pos >= 0) {
                        m_dataSchedule.removeItem(pos);
                        m_adapter.notifyItemRemoved(pos);
                    }
                }

                break;
            }
            case DATA_SCHEDULE_ENABLE: {
                if (data.has("id") && data.has("value")) {
                    int id = data.get("id").getAsInt();
                    boolean value = data.get("value").getAsBoolean();

                    final int pos = m_dataSchedule.findItemById(id);

                    if (pos >= 0) {
                        ScheduleItem item = m_dataSchedule.getItem(pos);

                        if (item.isEnabled() != value) {
                            m_dataSchedule.getItem(pos).setEnabled(value);
                            m_adapter.notifyItemChanged(pos);
                        }
                    }
                }

                break;
            }
            case DATA_SCHEDULE_RATIO: {
                setOverallRatioData(m_dataSchedule.getUsedRatio());
                break;
            }
        }
    }

    @Override
    public void onChangeData(DataType dataType, HashMap<String, Object> data) {
        sendRequest(RequestFormatter.format(dataType, data));
    }

    private void setOverallRatioData(int ratio) {
        if (getActivity() != null)
            m_textOverallRatio.setText(String.format(getActivity().getResources().getString(R.string.schedule_item_ratio), ratio));

        m_progressOverallRatio.setProgress(ratio);
    }

    private void sendRequest(String req) {
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity)getActivity();
            activity.getServiceCommunicator().sendMessage(req);
        }
    }
}