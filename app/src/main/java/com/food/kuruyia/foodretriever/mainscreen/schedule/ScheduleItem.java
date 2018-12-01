package com.food.kuruyia.foodretriever.mainscreen.schedule;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

import androidx.annotation.NonNull;

public class ScheduleItem implements Parcelable, Comparable<ScheduleItem> {
    private int m_hour;
    private int m_minutes;
    private int m_ratio;
    private boolean m_enabled = true;
    private int m_id;

    ScheduleItem(int hour, int minutes, int ratio, int id) {
        m_hour = hour;
        m_minutes = minutes;
        m_ratio = ratio;
        m_id = id;
    }

    ScheduleItem(ScheduleItem item) {
        m_hour = item.getHour();
        m_minutes = item.getMinutes();
        m_ratio = item.getRatio();
        m_enabled = item.isEnabled();
        m_id = item.getId();
    }

    int getHour() {
        return m_hour;
    }

    void setHour(int hour) {
        m_hour = hour;
    }

    int getMinutes() {
        return m_minutes;
    }

    void setMinutes(int minutes) {
        m_minutes = minutes;
    }

    public int getRatio() {
        return m_ratio;
    }

    public void setRatio(int ratio) {
        m_ratio = ratio;
    }

    public boolean isEnabled() {
        return m_enabled;
    }

    public void setEnabled(boolean enabled) {
        m_enabled = enabled;
    }

    private Calendar getCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(0, 0, 0, m_hour, m_minutes);

        return calendar;
    }

    public int getId() {
        return m_id;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(m_hour);
        out.writeInt(m_minutes);
        out.writeInt(m_ratio);
    }

    public static final Parcelable.Creator<ScheduleItem> CREATOR
            = new Parcelable.Creator<ScheduleItem>() {
        public ScheduleItem createFromParcel(Parcel in) {
            return new ScheduleItem(in);
        }

        public ScheduleItem[] newArray(int size) {
            return new ScheduleItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    private ScheduleItem(Parcel in) {
        m_hour = in.readInt();
        m_minutes = in.readInt();
        m_ratio = in.readInt();
    }

    @Override
    public int compareTo(@NonNull ScheduleItem o) {
        return getCalendar().compareTo(o.getCalendar());
    }
}
