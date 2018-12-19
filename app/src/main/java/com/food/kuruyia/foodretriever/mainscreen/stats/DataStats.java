package com.food.kuruyia.foodretriever.mainscreen.stats;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class DataStats implements Parcelable {
    private JsonArray m_dogWeightValues = new JsonArray();
    private JsonArray m_foodAvailabilityValues = new JsonArray();
    private JsonArray m_dogArrivalValues = new JsonArray();

    public DataStats() {
    }

    public JsonArray getDogWeightValues() {
        return m_dogWeightValues;
    }

    public void setDogWeightValues(JsonArray dogWeightValues) {
        m_dogWeightValues = dogWeightValues;
    }

    public JsonArray getFoodAvailabilityValues() {
        return m_foodAvailabilityValues;
    }

    public void setFoodAvailabilityValues(JsonArray foodAvailabilityValues) {
        m_foodAvailabilityValues = foodAvailabilityValues;
    }

    public JsonArray getDogArrivalValues() {
        return m_dogArrivalValues;
    }

    public void setDogArrivalValues(JsonArray dogArrivalValues) {
        m_dogArrivalValues = dogArrivalValues;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(m_dogWeightValues.toString());
        out.writeString(m_foodAvailabilityValues.toString());
        out.writeString(m_dogArrivalValues.toString());
    }

    public static final Parcelable.Creator<DataStats> CREATOR
            = new Parcelable.Creator<DataStats>() {
        public DataStats createFromParcel(Parcel in) {
            return new DataStats(in);
        }

        public DataStats[] newArray(int size) {
            return new DataStats[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    private DataStats(Parcel in) {
        JsonParser parser = new JsonParser();

        m_dogWeightValues = parser.parse(in.readString()).getAsJsonArray();
        m_foodAvailabilityValues = parser.parse(in.readString()).getAsJsonArray();
        m_dogArrivalValues = parser.parse(in.readString()).getAsJsonArray();
    }
}
