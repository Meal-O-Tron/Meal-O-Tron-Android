package com.food.kuruyia.foodretriever.setupscreen;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.food.kuruyia.foodretriever.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterNetwork extends RecyclerView.Adapter<AdapterNetwork.ANViewHolder> {
    private final ArrayList<NetworkItem> m_dataset;
    private final Activity m_activity;

    static class ANViewHolder extends RecyclerView.ViewHolder {
        TextView m_setupNetworkName;
        TextView m_setupNetworkEncryption;

        ANViewHolder(View layout) {
            super(layout);

            m_setupNetworkName = layout.findViewById(R.id.setupNetworkName);
            m_setupNetworkEncryption = layout.findViewById(R.id.setupNetworkEncryption);
        }
    }

    AdapterNetwork(ArrayList<NetworkItem> dataset, Activity activity) {
        m_dataset = dataset;
        m_activity = activity;
    }

    @Override
    public void onBindViewHolder(@NonNull final ANViewHolder holder, final int position) {
        final NetworkItem currentItem = m_dataset.get(position);

        holder.m_setupNetworkName.setText(currentItem.getSsid());
        holder.m_setupNetworkEncryption.setText(currentItem.getEncryptionAsString());
    }

    @NonNull
    @Override
    public AdapterNetwork.ANViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.setup_item, parent, false);

        return new AdapterNetwork.ANViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return m_dataset.size();
    }
}