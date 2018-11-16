package com.foreximf.quickpro.camarilla;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.foreximf.quickpro.R;
import com.foreximf.quickpro.camarilla.CamarillaDiffCallback;

import java.util.ArrayList;
import java.util.List;

public class CamarillaRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Camarilla> camarillaList;
    private LayoutInflater inflater;
//    private CamarillaViewHolderListener listener;


    public CamarillaRecyclerViewAdapter(LayoutInflater inflater) {
        camarillaList = new ArrayList<>();
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.camarilla_item_layout, parent, false);
        return new CamarillaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Camarilla camarilla = camarillaList.get(position);
        if(camarilla != null) {
            CamarillaViewHolder viewHolder = (CamarillaViewHolder) holder;
            viewHolder.setContent(camarilla);
        }
    }

    @Override
    public int getItemCount() {
        return camarillaList.size();
    }

    public void setCamarilla(List<Camarilla> camarillaList) {
        List<Camarilla> oldList = new ArrayList<>(this.camarillaList);
        this.camarillaList.clear();
        this.camarillaList.addAll(camarillaList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new CamarillaDiffCallback(oldList, camarillaList));
        diffResult.dispatchUpdatesTo(this);
    }
}
