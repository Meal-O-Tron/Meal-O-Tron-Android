package com.food.kuruyia.foodretriever.mainscreen.schedule;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
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
    public void onDataChanged(DataType dataType, HashMap<String, Object> data) {
        // TODO: Handle schedule messages
        switch (dataType) {
            case DATA_SCHEDULE_ADD: {
                Object hour = data.get("hour");
                Object minute = data.get("minute");
                Object ratio = data.get("ratio");
                Object id = data.get("id");
                Object enabled = data.get("enabled");
                if (hour != null && minute != null && ratio != null && id != null && enabled != null) {
                    int pos = m_dataSchedule.addItem(new ScheduleItem(((Double)hour).intValue(), ((Double)minute).intValue(), ((Double)ratio).intValue(), ((Double)id).intValue(), (boolean)enabled));

                    if (pos >= 0)
                        m_adapter.notifyItemInserted(pos);
                }

                break;
            }
            case DATA_SCHEDULE_REMOVE: {
                Object id = data.get("id");
                if (id != null) {
                    final int pos = m_dataSchedule.findItemById(((Double)id).intValue());

                    if (pos >= 0) {
                        m_dataSchedule.removeItem(pos);
                        m_adapter.notifyItemRemoved(pos);
                    }
                }

                break;
            }
            case DATA_SCHEDULE_ENABLE: {
                Object id = data.get("id");
                Object value = data.get("value");
                if (id != null && value != null) {
                    final int pos = m_dataSchedule.findItemById(((Double)id).intValue());

                    if (pos >= 0) {
                        boolean val = (boolean)value;
                        ScheduleItem item = m_dataSchedule.getItem(pos);

                        if (item.isEnabled() != val) {
                            m_dataSchedule.getItem(pos).setEnabled((boolean)value);
                            m_adapter.notifyItemChanged(pos);

                            Log.d(TAG, "nskip");
                        } else {
                            Log.d(TAG, "skip");
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