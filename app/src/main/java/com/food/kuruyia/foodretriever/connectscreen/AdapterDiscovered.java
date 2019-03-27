package com.food.kuruyia.foodretriever.connectscreen;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.food.kuruyia.foodretriever.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterDiscovered extends RecyclerView.Adapter<AdapterDiscovered.ADViewHolder> {
    private final ArrayList<DiscoveredItem> m_dataset;
    private final Activity m_activity;

    static class ADViewHolder extends RecyclerView.ViewHolder {
        TextView m_discoveredDogNameText;
        TextView m_discoveredIpText;

        MaterialCardView m_cardView;
        View m_layout;

        ADViewHolder(View layout) {
            super(layout);

            m_discoveredDogNameText = layout.findViewById(R.id.discoveredDogName);
            m_discoveredIpText = layout.findViewById(R.id.discoveredIp);
            m_cardView = layout.findViewById(R.id.discoveredItemCard);
            m_layout = layout.findViewById(R.id.discoveredItemLayout);
        }
    }

    AdapterDiscovered(ArrayList<DiscoveredItem> dataset, Activity activity) {
        m_dataset = dataset;
        m_activity = activity;
    }

    @Override
    public void onBindViewHolder(@NonNull final ADViewHolder holder, final int position) {
        final DiscoveredItem currentItem = m_dataset.get(position);

        holder.m_discoveredDogNameText.setText(currentItem.getDogName());
        holder.m_discoveredIpText.setText(currentItem.getIp());

        holder.m_cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_activity instanceof IDiscoveryCallback)
                    ((IDiscoveryCallback)m_activity).onDiscoveredClick(currentItem.getIp());
            }
        });
    }

    @NonNull
    @Override
    public AdapterDiscovered.ADViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discovered_item, parent, false);

        return new AdapterDiscovered.ADViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return m_dataset.size();
    }
}

interface IDiscoveryCallback {
    public void onDiscoveredClick(String ip);
}
