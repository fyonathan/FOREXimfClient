package com.foreximf.client.news;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.foreximf.client.R;
import com.foreximf.client.util.DateConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsRecyclerViewAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private List<News> newsList;
    private String[] mTypeSet = {"Signal Trading", "Breaking News", "Analisa Harian"};
    private String[] mTitleSet = {"Harga Minyak Naik Tipis", "Yen Menguat", "EURUSD Masih Tertekan"};
    private NewsRecyclerViewAdapter.ViewHolder viewHolder;
    private Context context;

    public NewsRecyclerViewAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        newsList = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.daily_item_layout, parent, false);
        viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if(newsList != null) {
            News news = newsList.get(position);
            TextView type = viewHolder.type;
            type.setText(news.getType());

            TextView title = viewHolder.title;
            title.setText(news.getTitle());

            TextView seeMore = viewHolder.seeMore;
            seeMore.setText("See More");

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent newsDetailIntent = new Intent(context, NewsDetailActivity.class);
                    newsDetailIntent.putExtra("type", newsList.get(position).getType());
                    newsDetailIntent.putExtra("title", newsList.get(position).getTitle());
                    newsDetailIntent.putExtra("content", newsList.get(position).getContent());
                    newsDetailIntent.putExtra("author", newsList.get(position).getAuthor());
                    Date date = newsList.get(position).getLastUpdate();
                    long timeStamp = DateConverter.toTimestamp(date);
                    newsDetailIntent.putExtra("last-update-time", timeStamp);
                    context.startActivity(newsDetailIntent);

                }
            });
        } else {
            TextView type = viewHolder.type;
            type.setText("");

            TextView title = viewHolder.title;
            title.setText("");

            TextView seeMore = viewHolder.seeMore;
            seeMore.setText("See More");
        }
    }

    @Override
    public int getItemCount() {
        if(newsList != null) {
            return newsList.size();
        } else {
            return 0;
        }
    }

//    @Override
//    public int getItemViewType(int position) {
//        return position;
//    }

    public void setNews(List<News> newsList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new NewsDiffCallback(this.newsList, newsList));
//        this.newsList = newsList;
        this.newsList.clear();
        this.newsList.addAll(newsList);
        diffResult.dispatchUpdatesTo(this);
//        notifyDataSetChanged();
    }

//    public void setNews(List<News> newsList) {
//        this.newsList = newsList;
////        Log.d("NR View Adapter", "Content Signal List : " + newsList.get(0).getTitle());
////        Log.d("NR View Adapter", "Content News List : " + newsList.get(1).getTitle());
////        Log.d("NR View Adapter", "Content Analysis List : " + newsList.get(2).getTitle());
//        Log.i("News", " Observer Called");
//        notifyDataSetChanged();
//    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView type;
        private TextView title;
        private TextView seeMore;

        private ViewHolder(View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.daily_item_type);
            title = itemView.findViewById(R.id.daily_item_title);
            seeMore = itemView.findViewById(R.id.daily_item_see_more);
        }
    }
}
