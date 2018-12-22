package com.food.kuruyia.foodretriever.mainscreen.schedule;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.food.kuruyia.foodretriever.utils.DataType;
import com.food.kuruyia.foodretriever.utils.IDataChange;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;

public class DataSchedule implements Parcelable, IDataChange {
    private ArrayList<ScheduleItem> m_scheduledItems = new ArrayList<>();
    private ArrayList<IDataChange> m_dataChangedListeners = new ArrayList<>();

    final static String TAG = "DataSchedule";

    public DataSchedule() { }

    ArrayList<ScheduleItem> getScheduledItems() {
        return m_scheduledItems;
    }

    public int addItem(ScheduleItem item) {
        for (int i = 0; i < m_scheduledItems.size(); i++)
            if (item.compareTo(m_scheduledItems.get(i)) == 0)
                return -1;

        m_scheduledItems.add(item);
        Collections.sort(m_scheduledItems);

        for (int i = 0; i < m_scheduledItems.size(); i++)
            if (item.compareTo(m_scheduledItems.get(i)) == 0)
                return i;

        return m_scheduledItems.size() - 1;
    }

    void removeItem(int position) {
        m_scheduledItems.remove(position);
    }

    ScheduleItem getItem(int position) {
        return m_scheduledItems.get(position);
    }

    int findItemById(int id) {
        for (int i = 0; i < m_scheduledItems.size(); i++) {
            ScheduleItem currentItem = m_scheduledItems.get(i);
            if (currentItem.getId() == id)
                return i;
        }

        return -1;
    }

    int getUsedRatio(boolean filterEnabled) {
        int ratio = 0;

        for (int i = 0; i < m_scheduledItems.size(); i++) {
            ScheduleItem actualItem = m_scheduledItems.get(i);
            if ((actualItem.isEnabled() && filterEnabled) || !filterEnabled)
                ratio += actualItem.getRatio();
        }

        return ratio;
    }

    int getRemainingRatio() {
        int ratio = 100;

        for (int i = 0; i < m_scheduledItems.size(); i++) {
            ratio -= m_scheduledItems.get(i).getRatio();
        }

        return ratio;
    }

    void addDataChangeListener(IDataChange listener) {
        m_dataChangedListeners.add(listener);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeList(m_scheduledItems);
    }

    public static final Parcelable.Creator<DataSchedule> CREATOR
            = new Parcelable.Creator<DataSchedule>() {
        public DataSchedule createFromParcel(Parcel in) {
            return new DataSchedule(in);
        }

        public DataSchedule[] newArray(int size) {
            return new DataSchedule[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    private DataSchedule(Parcel in) {
        in.readList(m_scheduledItems, ScheduleItem.class.getClassLoader());
    }

    @Override
    public void onDataChanged(DataType dataType, JsonObject data) {
        for (int i = 0; i < m_dataChangedListeners.size(); i++)
            m_dataChangedListeners.get(i).onDataChanged(dataType, data);
    }

    @Override
    public void onChangeData(DataType dataType, JsonObject data) {
        for (int i = 0; i < m_dataChangedListeners.size(); i++)
            m_dataChangedListeners.get(i).onChangeData(dataType, data);
    }
}
