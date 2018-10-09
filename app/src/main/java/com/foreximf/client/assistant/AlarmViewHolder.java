package com.foreximf.client.assistant;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.foreximf.client.R;
import com.foreximf.client.util.DateFormatter;

public class AlarmViewHolder extends RecyclerView.ViewHolder {
    private ImageView icon;
    private TextView name;
    private TextView notes;
    private TextView time;
    private Switch toggle;
    private Alarm alarm;

    public AlarmViewHolder(View itemView) {
        super(itemView);
        icon = itemView.findViewById(R.id.assistant_item_icon);
        name = itemView.findViewById(R.id.assistant_item_title);
        time = itemView.findViewById(R.id.assistant_item_time);
        toggle = itemView.findViewById(R.id.assistant_item_switch);
        toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                //Alarm On
            }else {
                //Alarm Off
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Alarm Activity
            }
        });
    }

    public void setAlarm(Alarm _alarm) {
        alarm = _alarm;
        icon.setImageResource(R.mipmap.ic_alarm);
        name.setText(alarm.getName());
        time.setText(DateFormatter.format(alarm.getReminderTime(), "d MMM yyyy - HH:mm"));
    }
}
