package com.foreximf.quickpro.signal;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.List;

/**
 * A utility class responsible to manage the change
 * of the data in {@link SignalRecyclerViewAdapter}
 */
public class SignalDiffCallback extends DiffUtil.Callback {

    private final List<Signal> oldSignal;
    private final List<Signal> newSignal;

    public SignalDiffCallback(List<Signal> oldSignal, List<Signal> newSignal) {
        this.oldSignal = oldSignal;
        this.newSignal = newSignal;
    }

    @Override
    public int getOldListSize() {
        return oldSignal.size();
    }

    @Override
    public int getNewListSize() {
        return newSignal.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldSignal.get(oldItemPosition).equals(newSignal.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldSignal.get(oldItemPosition).getContent().equals(newSignal.get(newItemPosition).getContent());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
