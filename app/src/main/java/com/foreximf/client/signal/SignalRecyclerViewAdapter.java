package com.foreximf.client.signal;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//import com.foreximf.client.OnLoadMoreListener;
import com.foreximf.client.R;

import java.util.ArrayList;
import java.util.List;

public class SignalRecyclerViewAdapter extends PagedListAdapter<Signal, SignalViewHolder> {
//    private boolean isLoadingAdded = false;
//
//    private final int ITEM = 1;
//    private final int LOADING = 0;

    private List<Signal> signalList;
    private LayoutInflater inflater;
    private SignalViewHolder.ViewHolderListener listener;

//    private int visibleThreshold = 10;
//    private int lastVisibleItem, totalItemCount;
//    private boolean loading;
//    private OnLoadMoreListener onLoadMoreListener;

    SignalRecyclerViewAdapter(LayoutInflater _inflater, SignalViewHolder.ViewHolderListener _listener) {
        super(DIFF_CALLBACK);
        this.signalList = new ArrayList<>();
        this.listener = _listener;
        this.inflater = _inflater;
    }

    @NonNull
    @Override
    public SignalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.signal_item_layout, parent, false);
        return new SignalViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull SignalViewHolder holder, int position) {
        Signal signal = signalList.get(position);
        holder.setContent(signal);
    }

    private static DiffUtil.ItemCallback<Signal> DIFF_CALLBACK =
        new DiffUtil.ItemCallback<Signal>() {
            // The ID property identifies when items are the same.
            @Override
            public boolean areItemsTheSame(Signal oldItem, Signal newItem) {
                return oldItem.getId() == newItem.getId();
            }

            // Use Object.equals() to know when an item's content changes.
            // Implement equals(), or write custom data comparison logic here.
            @Override
            public boolean areContentsTheSame(Signal oldItem, Signal newItem) {
                return oldItem.equals(newItem);
            }
        };

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
        return super.getItemViewType(position);
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
