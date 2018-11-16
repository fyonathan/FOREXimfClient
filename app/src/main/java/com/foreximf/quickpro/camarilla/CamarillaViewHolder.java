package com.foreximf.quickpro.camarilla;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.foreximf.quickpro.R;
import com.foreximf.quickpro.util.DateFormatter;

public class CamarillaViewHolder extends RecyclerView.ViewHolder {
    private TextView nameText;
    private TextView dateText;
    private TextView pivotText;
    private TextView sellAreaText;
    private TextView sellTp1Text;
    private TextView sellTp2Text;
    private TextView sellSlText;
    private TextView buyAreaText;
    private TextView buyTp1Text;
    private TextView buyTp2Text;
    private TextView buySlText;
    private TextView buyBreakoutAreaText;
    private TextView buyBreakoutTp1Text;
    private TextView buyBreakoutTp2Text;
    private TextView buyBreakoutSlText;
    private TextView sellBreakoutAreaText;
    private TextView sellBreakoutTp1Text;
    private TextView sellBreakoutTp2Text;
    private TextView sellBreakoutSlText;

    CamarillaViewHolder(View itemView) {
        super(itemView);
        pivotText = itemView.findViewById(R.id.camarilla_pivot_text);
        nameText = itemView.findViewById(R.id.camarilla_name_text);
        dateText = itemView.findViewById(R.id.camarilla_date_text);
        sellAreaText = itemView.findViewById(R.id.camarilla_sell_area_text);
        sellTp1Text = itemView.findViewById(R.id.camarilla_sell_tp1_text);
        sellTp2Text = itemView.findViewById(R.id.camarilla_sell_tp2_text);
        sellSlText = itemView.findViewById(R.id.camarilla_sell_sl_text);
        buyAreaText = itemView.findViewById(R.id.camarilla_buy_area_text);
        buyTp1Text = itemView.findViewById(R.id.camarilla_buy_tp1_text);
        buyTp2Text = itemView.findViewById(R.id.camarilla_buy_tp2_text);
        buySlText = itemView.findViewById(R.id.camarilla_buy_sl_text);
        buyBreakoutAreaText = itemView.findViewById(R.id.camarilla_buy_breakout_area_text);
        buyBreakoutTp1Text = itemView.findViewById(R.id.camarilla_buy_breakout_tp1_text);
        buyBreakoutTp2Text = itemView.findViewById(R.id.camarilla_buy_breakout_tp2_text);
        buyBreakoutSlText = itemView.findViewById(R.id.camarilla_buy_breakout_sl_text);
        sellBreakoutAreaText = itemView.findViewById(R.id.camarilla_sell_breakout_area_text);
        sellBreakoutTp1Text = itemView.findViewById(R.id.camarilla_sell_breakout_tp1_text);
        sellBreakoutTp2Text = itemView.findViewById(R.id.camarilla_sell_breakout_tp2_text);
        sellBreakoutSlText = itemView.findViewById(R.id.camarilla_sell_breakout_sl_text);
    }

    public void setContent(Camarilla _item) {
        nameText.setText(_item.getPairName());
        String dateString = DateFormatter.format(_item.getLastUpdate(), "dd MMM, HH:mm");
        dateText.setText(dateString);
        pivotText.setText(new StringBuilder(": ").append(String.valueOf(_item.getPivot())));
        sellAreaText.setText(new StringBuilder(": ").append(String.valueOf(_item.getSellArea())));
        sellTp1Text.setText(new StringBuilder(": ").append(String.valueOf(_item.getSellTp1())));
        sellTp2Text.setText(new StringBuilder(": ").append(String.valueOf(_item.getSellTp2())));
        sellSlText.setText(new StringBuilder(": ").append(String.valueOf(_item.getSellSl())));
        buyAreaText.setText(new StringBuilder(": ").append(String.valueOf(_item.getBuyArea())));
        buyTp1Text.setText(new StringBuilder(": ").append(String.valueOf(_item.getBuyTp1())));
        buyTp2Text.setText(new StringBuilder(": ").append(String.valueOf(_item.getBuyTp2())));
        buySlText.setText(new StringBuilder(": ").append(String.valueOf(_item.getBuySl())));
        buyBreakoutAreaText.setText(new StringBuilder(": ").append(String.valueOf(_item.getBuyBreakoutArea())));
        buyBreakoutTp1Text.setText(new StringBuilder(": ").append(String.valueOf(_item.getBuyBreakoutTp1())));
        buyBreakoutTp2Text.setText(new StringBuilder(": ").append(String.valueOf(_item.getBuyBreakoutTp2())));
        buyBreakoutSlText.setText(new StringBuilder(": ").append(String.valueOf(_item.getBuyBreakoutSl())));
        sellBreakoutAreaText.setText(new StringBuilder(": ").append(String.valueOf(_item.getSellBreakoutArea())));
        sellBreakoutTp1Text.setText(new StringBuilder(": ").append(String.valueOf(_item.getSellBreakoutTp1())));
        sellBreakoutTp2Text.setText(new StringBuilder(": ").append(String.valueOf(_item.getSellBreakoutTp2())));
        sellBreakoutSlText.setText(new StringBuilder(": ").append(String.valueOf(_item.getSellBreakoutSl())));
    }
}
