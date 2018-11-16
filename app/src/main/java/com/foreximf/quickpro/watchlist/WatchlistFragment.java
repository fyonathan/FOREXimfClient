package com.foreximf.quickpro.watchlist;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class WatchlistFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getActivity(), "Watchlist clicked", Toast.LENGTH_LONG).show();
    }
}
