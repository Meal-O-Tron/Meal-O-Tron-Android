package com.food.kuruyia.foodretriever.mainscreen.dogs;

import android.os.Parcel;
import android.os.Parcelable;

public class DataDogs implements Parcelable {
    String m_dogName;

    int m_actualWeight;
    boolean m_weightRegulated;
    int m_expectedWeight;

    public DataDogs(String dogName, int actualWeight, boolean weightRegulated, int expectedWeight) {
        m_dogName = dogName;

        m_actualWeight = actualWeight;
        m_weightRegulated = weightRegulated;
        m_expectedWeight = expectedWeight;
    }

    public String getDogName() {
        return m_dogName;
    }

    public void setDogName(String dogName) {
        m_dogName = dogName;
    }

    public int getActualWeight() {
        return m_actualWeight;
    }

    public void setActualWeight(int actualWeight) {
        m_actualWeight = actualWeight;
    }

    public boolean isWeightRegulated() {
        return m_weightRegulated;
    }

    public void setWeightRegulated(boolean weightRegulated) {
        m_weightRegulated = weightRegulated;
    }

    public int getExpectedWeight() {
        return m_expectedWeight;
    }

    public void setExpectedWeight(int expectedWeight) {
        m_expectedWeight = expectedWeight;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(m_dogName);

        out.writeInt(m_actualWeight);
        out.writeByte((byte) (m_weightRegulated ? 1 : 0));
        out.writeInt(m_expectedWeight);
    }

    public static final Parcelable.Creator<DataDogs> CREATOR
            = new Parcelable.Creator<DataDogs>() {
        public DataDogs createFromParcel(Parcel in) {
            return new DataDogs(in);
        }

        public DataDogs[] newArray(int size) {
            return new DataDogs[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    private DataDogs(Parcel in) {
        m_dogName = in.readString();

        m_actualWeight = in.readInt();
        m_weightRegulated = in.readByte() != 0;
        m_expectedWeight = in.readInt();
    }
}