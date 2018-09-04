package com.foreximf.client.signal;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.foreximf.client.R;
import com.foreximf.client.news.NewsDiffCallback;
import com.foreximf.client.util.ImageDisplayActivity;
import com.foreximf.client.util.ImageUtils;
import com.vanniktech.emoji.EmojiTextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SignalRecyclerViewAdapter extends RecyclerView.Adapter<SignalRecyclerViewAdapter.ViewHolder> {
    private List<Signal> signalList;
    private WeakReference<Activity> weakActivity;
    private String[] mTypeSet = {"Signal Trading", "Breaking News", "Analisa Harian"};
    private String[] mTitleSet = {"Harga Minyak Naik Tipis", "Yen Menguat", "EURUSD Masih Tertekan"};
    private LayoutInflater inflater;
//    OnBottomReachedListener onBottomReachedListener;

    public SignalRecyclerViewAdapter(Activity activity, LayoutInflater _inflater) {
        this.signalList = new ArrayList<>();
        this.weakActivity = new WeakReference<>(activity);
        this.inflater = _inflater;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.signal_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Signal signal = signalList.get(position);
        ViewHolder vh = holder;
        vh.setContent(signal);
    }

//    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener) {
//        this.onBottomReachedListener = onBottomReachedListener;
//    }

    @Override
    public int getItemCount() {
        return signalList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    //    @Override
//    public long getItemId(int position) {
//        return signalList.get(position).hashCode();
//    }

    public void setSignal(List<Signal> signalList) {
        List<Signal> oldList = new ArrayList<>(this.signalList);
        this.signalList.clear();
        this.signalList.addAll(signalList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new SignalDiffCallback(oldList, signalList));
//        this.signalList.add(signalList.get(signalList.size() - 1));
        diffResult.dispatchUpdatesTo(this);
//        Log.i("Signal", " Observer Called");
//        notifyDataSetChanged();
    }

//    public interface OnBottomReachedListener {
//        void onBottomReached(boolean removeBadge);
//    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private EmojiTextView content;
        private Signal item;

        private ViewHolder(View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.signal_item_content);
        }

        public void setContent(Signal _item) {
            item = _item;
            if(item.getContent() != null) {
                Log.d("SAVA", "Isi Signal : " + item.getContent());
                Spannable html = ImageUtils.getSpannableHtmlWithImageGetter(content, item.getContent());
                ImageUtils.setClickListenerOnHtmlImageGetter(html, new ImageUtils.Callback() {
                    @Override
                    public void onImageClick(String imageUrl) {
//                    showAlertDialogWithImage(imageUrl);
                        Intent intent = new Intent(weakActivity.get(), ImageDisplayActivity.class);
                        intent.putExtra("image-uri", imageUrl);
                        intent.putExtra("title", item.getTitle());
//                    context.startActivity(intent);
                        if(Build.VERSION.SDK_INT >= 21) {
                            weakActivity.get().startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(weakActivity.get()).toBundle());
                        }
                    }
                });
                content.setText(html);
                content.setMovementMethod(LinkMovementMethod.getInstance());
//            content.setText(Html.fromHtml(signal.getContent()));
            }
        }
    }
}
