package com.food.kuruyia.foodretriever.mainscreen.schedule;

import android.app.TimePickerDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.food.kuruyia.foodretriever.R;
import com.food.kuruyia.foodretriever.utils.DataType;
import com.food.kuruyia.foodretriever.utils.IDataChange;
import com.food.kuruyia.foodretriever.utils.IDialogScheduleRatioInteract;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterSchedule extends RecyclerView.Adapter<AdapterSchedule.ASViewHolder> {
    private final ArrayList<ScheduleItem> m_dataset;
    private DataSchedule m_dataSchedule;

    private final Fragment m_activity;

    static class ASViewHolder extends RecyclerView.ViewHolder {
        TextView m_clockText;
        TextView m_ratioText;
        Switch m_enableSwitch;

        ConstraintLayout m_layout;
        LinearLayout m_ratioLayout;

        ASViewHolder(View layout) {
            super(layout);

            m_clockText = layout.findViewById(R.id.textClock);
            m_ratioText = layout.findViewById(R.id.textRatio);
            m_enableSwitch = layout.findViewById(R.id.scheduleItemSwitch);

            m_layout = layout.findViewById(R.id.scheduleItemLayout);
            m_ratioLayout = layout.findViewById(R.id.ratioContainer);
        }
    }

    AdapterSchedule(DataSchedule dataSchedule, Fragment activity) {
        m_dataset = dataSchedule.getScheduledItems();
        m_dataSchedule = dataSchedule;

        m_activity = activity;
    }

    @Override
    public void onBindViewHolder(@NonNull final ASViewHolder holder, final int position) {
        final ScheduleItem currentItem = m_dataset.get(position);

        holder.m_clockText.setText(String.format(m_activity.getResources().getString(R.string.schedule_item_time), currentItem.getHour(), currentItem.getMinutes()));
        holder.m_ratioText.setText(String.format(m_activity.getResources().getString(R.string.schedule_item_ratio), currentItem.getRatio()));

        holder.m_clockText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = m_activity.getContext();
                new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("hour", hourOfDay);
                        hashMap.put("minute", minute);

                        notifyChangeToFragment(DataType.DATA_SCHEDULE_DATE, getRequestData(currentItem.getId(), hashMap));
                    }
                }, currentItem.getHour(), currentItem.getMinutes(), DateFormat.is24HourFormat(context)).show();
            }
        });

        holder.m_ratioLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogScheduleRatio dialogScheduleRatio = DialogScheduleRatio.newInstance(currentItem.getRatio(), m_dataSchedule.getRemainingRatio());
                dialogScheduleRatio.setConfirmListener(new IDialogScheduleRatioInteract() {
                    @Override
                    public void onConfirm(int ratio) {
                        notifyChangeToFragment(DataType.DATA_SCHEDULE_RATIO, getRequestData(currentItem.getId(), ratio));
                    }
                });

                FragmentManager fragmentManager = m_activity.getFragmentManager();
                if (fragmentManager != null)
                    dialogScheduleRatio.show(fragmentManager, "");
            }
        });

        holder.m_enableSwitch.setChecked(currentItem.isEnabled());
        holder.m_enableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                notifyChangeToFragment(DataType.DATA_SCHEDULE_ENABLE, getRequestData(currentItem.getId(), isChecked));
            }
        });
    }

    @NonNull
    @Override
    public ASViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_item, parent, false);

        return new ASViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return m_dataset.size();
    }

    private void notifyChangeToFragment(DataType dataType, HashMap<String, Object> data) {
        if (m_activity instanceof IDataChange)
            ((IDataChange)m_activity).onChangeData(dataType, data);
    }

    private <T> HashMap<String, Object> getRequestData(int id, T data) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("value", data);

        return hashMap;
    }
}
