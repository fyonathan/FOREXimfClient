package com.foreximf.client.signal;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.foreximf.client.R;
import com.foreximf.client.util.DateFormatter;
import com.foreximf.client.util.ImageDisplayActivity;
import com.foreximf.client.util.ImageUtils;
import com.vanniktech.emoji.EmojiTextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SignalRecyclerViewAdapter extends RecyclerView.Adapter {
    private List<Signal> signalList;
    private LayoutInflater inflater;
    private SignalViewHolder.ViewHolderListener listener;

    public SignalRecyclerViewAdapter(LayoutInflater _inflater, SignalViewHolder.ViewHolderListener _listener) {
        this.signalList = new ArrayList<>();
        this.listener = _listener;
        this.inflater = _inflater;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.signal_item_layout, parent, false);
        return new SignalViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Signal signal = signalList.get(position);
        SignalViewHolder viewHolder = (SignalViewHolder) holder;
        viewHolder.setContent(signal);
    }

    @Override
    public int getItemCount() {
        return signalList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void setSignal(List<Signal> signalList) {
        List<Signal> oldList = new ArrayList<>(this.signalList);
        this.signalList.clear();
        this.signalList.addAll(signalList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new SignalDiffCallback(oldList, signalList));
        diffResult.dispatchUpdatesTo(this);
    }
}
