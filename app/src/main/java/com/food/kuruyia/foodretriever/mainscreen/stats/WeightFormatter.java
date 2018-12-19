package com.food.kuruyia.foodretriever.mainscreen.stats;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

public class WeightFormatter implements IValueFormatter {
    private DecimalFormat m_decimalFormat;

    public WeightFormatter() {
        m_decimalFormat = new DecimalFormat("###,###,##0.0");;
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return m_decimalFormat.format(value) + "kg";
    }
}
