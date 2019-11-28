package com.foreximf.quickpro.camarilla;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.List;

public class CamarillaDiffCallback extends DiffUtil.Callback {

    private final List<Camarilla> oldCamarilla;
    private final List<Camarilla> newCamarilla;

    public CamarillaDiffCallback(List<Camarilla> oldSignal, List<Camarilla> newSignal) {
        this.oldCamarilla = oldSignal;
        this.newCamarilla = newSignal;
    }

    @Override
    public int getOldListSize() {
        return oldCamarilla.size();
    }

    @Override
    public int getNewListSize() {
        return newCamarilla.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldCamarilla.get(oldItemPosition).equals(newCamarilla.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldCamarilla.get(oldItemPosition).getServerId() == newCamarilla.get(newItemPosition).getServerId();
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
