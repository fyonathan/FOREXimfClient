package com.foreximf.quickpro.signal;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.foreximf.quickpro.R;
import com.foreximf.quickpro.util.DateFormatter;

/**
 * A holder class responsible in managing the layout
 * and the content of {@link SignalRecyclerViewAdapter}
 */
public class SignalSwitchingViewHolder extends RecyclerView.ViewHolder {
    //    private ConstraintLayout signalItemContainer;
    private TextView title;
    private TextView dateText;
    private TextView timeText;
    private TextView currencyText;
    private ImageView orderTypeText;
    //    private TextView resultText;
//    private TextView activeText;
    private Signal item;

    SignalSwitchingViewHolder(View itemView, final SignalViewHolderListener listener) {
        super(itemView);
//        signalItemContainer = itemView.findViewById(R.id.signal_item_container);
        title = itemView.findViewById(R.id.signal_item_title);
        dateText = itemView.findViewById(R.id.signal_item_date);
        timeText = itemView.findViewById(R.id.signal_item_time);
        currencyText = itemView.findViewById(R.id.signal_item_currency);
        orderTypeText = itemView.findViewById(R.id.signal_item_order_type);
//        resultText = itemView.findViewById(R.id.signal_item_result);
//        activeText = itemView.findViewById(R.id.active_text);
        itemView.setOnClickListener(v -> listener.onItemClickListener(item));
    }

    public void setContent(Signal _item) {
        item = _item;
        String head = item.getSignalGroupString()/*+" / "+item.getTitle()*/;
        title.setText(head);
        dateText.setText(DateFormatter.format(item.getCreatedTime(), "dd MMM"));
        timeText.setText(DateFormatter.format(item.getCreatedTime(), "HH:mm"));
        currencyText.setText(item.getCurrencyPairString());
//        orderTypeText.setText(item.getOrderTypeString());
        orderTypeText.setImageResource(item.getOrderTypeDrawable());
//        if(item.getStatus() == 3) {
//            signalItemContainer.setBackgroundColor(Color.parseColor("#dddddd"));
//            String result;
//            if(item.getResult() > 0) {
//                result = "+"+item.getResult()+" pips";
//                resultText.setTextColor(Color.parseColor("#27ae60"));
//            }else{
//                result = item.getResult()+" pips";
//                resultText.setTextColor(Color.RED);
//            }
//            resultText.setText(result);
//            activeText.setVisibility(View.INVISIBLE);
//        }
    }
}
