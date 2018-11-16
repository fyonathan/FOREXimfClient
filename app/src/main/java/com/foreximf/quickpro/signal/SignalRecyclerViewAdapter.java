package com.foreximf.quickpro.signal;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//import com.foreximf.client.OnLoadMoreListener;
import com.foreximf.quickpro.R;

import java.util.ArrayList;
import java.util.List;

public class SignalRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//    private boolean isLoadingAdded = false;
//
//    private final int ITEM = 1;
//    private final int LOADING = 0;

    private List<Signal> signalList;
    private LayoutInflater inflater;
    private SignalViewHolderListener listener;

    private final int ACTIVE = 2;
    private final int DONE = 3;

//    private int visibleThreshold = 10;
//    private int lastVisibleItem, totalItemCount;
//    private boolean loading;
//    private OnLoadMoreListener onLoadMoreListener;

    SignalRecyclerViewAdapter(LayoutInflater _inflater, SignalViewHolderListener _listener) {
        this.signalList = new ArrayList<>();
        this.listener = _listener;
        this.inflater = _inflater;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        switch(viewType) {
            case ACTIVE :
                itemView = inflater.inflate(R.layout.signal_item_layout, parent, false);
                return new SignalViewHolder(itemView, listener);
            default :
                itemView = inflater.inflate(R.layout.signal_item_done_layout, parent, false);
                return new SignalDoneViewHolder(itemView, listener);
        }
//        View itemView = inflater.inflate(R.layout.signal_item_layout, parent, false);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Signal signal = signalList.get(position);
        if(signal != null) {
            switch(signal.getStatus()) {
                case ACTIVE : {
                    SignalViewHolder viewHolder = (SignalViewHolder) holder;
//                    holder.setContent(signal);
                    viewHolder.setContent(signal);
                    break;
                }
                case DONE : {
                    SignalDoneViewHolder viewHolder = (SignalDoneViewHolder) holder;
//                    holder.setContent(signal);
                    viewHolder.setContent(signal);
                    break;
                }
            }
        }
    }

//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        Signal signal = signalList.get(position);
//        SignalViewHolder viewHolder = (SignalViewHolder) holder;
//        viewHolder.setContent(signal);
//    }

    @Override
    public int getItemCount() {
        return signalList.size();
    }

    @Override
    public int getItemViewType(int position) {
//        return super.getItemViewType(position);
        switch(signalList.get(position).getStatus()) {
            case 2 :
                return ACTIVE;
            case 3 :
                return DONE;
            default :
                return 0;
        }
//        return (position == signalList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void setSignal(List<Signal> signalList) {
        List<Signal> oldList = new ArrayList<>(this.signalList);
        this.signalList.clear();
        this.signalList.addAll(signalList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new SignalDiffCallback(oldList, signalList));
        diffResult.dispatchUpdatesTo(this);
    }

//    public void add(Signal signal) {
//        signalList.add(signal);
//        notifyItemInserted(signalList.size() - 1);
//    }
//
//    public void addAll(List<Signal> signals) {
//        for (Signal signal : signals) {
//            add(signal);
//        }
//    }
//
//    public void remove(Signal signal) {
//        int position = signalList.indexOf(signal);
//        if(position > -1) {
//            signalList.remove(signal);
//            notifyItemRemoved(position);
//        }
//    }
//
//    public void clear() {
//        isLoadingAdded = false;
//        while(getItemCount() > 0) {
//            remove(getSignal(0));
//        }
//    }
//
//    public boolean isEmpty() {
//        return getItemCount() == 0;
//    }
//
//    public void addLoadingFooter() {
//        isLoadingAdded = true;
//        add(new Signal());
//    }
//
//    public void removeLoadingFooter() {
//        isLoadingAdded = false;
//
//        int position = signalList.size() - 1;
//        Signal item = getSignal(position);
//
//        if(item != null) {
//            signalList.remove(position);
//            notifyItemRemoved(position);
//        }
//    }
//
//    public Signal getSignal(int position) {
//        return signalList.get(position);
//    }
}
