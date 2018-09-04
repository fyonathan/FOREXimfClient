package com.foreximf.client.assistant;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.foreximf.client.R;

public class AssistantRecyclerViewAdapter extends RecyclerView.Adapter<AssistantRecyclerViewAdapter.ViewHolder> {
    private String[] mDataSet = {"Ganteng", "Jelek", "Ampas"};
    private String[] timeDataSet = {"7 Mei 2018 - 20:00", "7 Mei 2018 - 14:00", "8 Mei 2018 - 16:00"};
    private ViewHolder viewHolder;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View itemView = inflater.inflate(R.layout.assistant_item_layout, parent, false);

        viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageView icon = viewHolder.icon;
        icon.setImageResource(R.mipmap.ic_alarm);

        TextView title = viewHolder.title;
        title.setText(mDataSet[position]);

        TextView time = viewHolder.time;
        time.setText(timeDataSet[position]);
    }

    @Override
    public int getItemCount() {
        return mDataSet.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView title;
        private TextView time;

        private ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.assistant_item_icon);
            title = itemView.findViewById(R.id.assistant_item_title);
            time = itemView.findViewById(R.id.assistant_item_time);
        }
    }
}
