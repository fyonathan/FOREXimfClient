package com.foreximf.quickpro.news;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.List;

public class NewsDiffCallback extends DiffUtil.Callback {

    private final List<News> oldNews;
    private final List<News> newNews;

    public NewsDiffCallback(List<News> oldNews, List<News> newNews) {
        this.oldNews = oldNews;
        this.newNews = newNews;
    }

    @Override
    public int getOldListSize() {
        return oldNews.size();
    }

    @Override
    public int getNewListSize() {
        return newNews.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldNews.get(oldItemPosition).id == newNews.get(newItemPosition).id;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldNews.get(oldItemPosition).equals(newNews.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
