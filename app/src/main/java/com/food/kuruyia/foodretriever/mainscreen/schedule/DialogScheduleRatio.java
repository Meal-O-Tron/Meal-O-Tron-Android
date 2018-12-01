package com.food.kuruyia.foodretriever.mainscreen.schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.food.kuruyia.foodretriever.R;
import com.food.kuruyia.foodretriever.utils.IDialogScheduleRatioInteract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogScheduleRatio extends DialogFragment {
    int m_actualRatio;
    int m_maxRatio;
    IDialogScheduleRatioInteract m_dialogScheduleRatioInteract;

    public static DialogScheduleRatio newInstance(int actualRatio, int maxRatio) {
        DialogScheduleRatio myFragment = new DialogScheduleRatio();

        Bundle args = new Bundle();
        args.putInt("actualRatio", actualRatio);
        args.putInt("maxRatio", maxRatio);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_schedule_ratio, container);

        Bundle args = getArguments();
        if (args != null) {
            m_actualRatio = args.getInt("actualRatio");
            m_maxRatio = args.getInt("maxRatio") + m_actualRatio;
        }

        final SeekBar ratioSeek = view.findViewById(R.id.ratioSeek);
        final TextView textRatio = view.findViewById(R.id.textRatio);
        Button okButton = view.findViewById(R.id.okButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        textRatio.setText(String.format(getResources().getString(R.string.schedule_item_ratio), m_actualRatio));
        ratioSeek.setProgress(m_actualRatio);
        ratioSeek.setSecondaryProgress(m_maxRatio);

        ratioSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress > m_maxRatio) {
                    ratioSeek.setProgress(m_maxRatio);
                    progress = m_maxRatio;
                }

                m_actualRatio = progress;
                textRatio.setText(String.format(getResources().getString(R.string.schedule_item_ratio), m_actualRatio));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_dialogScheduleRatioInteract != null)
                    m_dialogScheduleRatioInteract.onConfirm(m_actualRatio);

                dismiss();
            }
        });

        return view;
    }

    public void setConfirmListener(IDialogScheduleRatioInteract dialogScheduleRatioInteract) {
        m_dialogScheduleRatioInteract = dialogScheduleRatioInteract;
    }
}

