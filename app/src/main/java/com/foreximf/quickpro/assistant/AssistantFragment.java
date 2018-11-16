package com.foreximf.quickpro.assistant;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.foreximf.quickpro.R;

public class AssistantFragment extends Fragment {
    RecyclerView assistantListView;
    RecyclerView.Adapter assistantAdapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getActivity(), "Assistant clicked", Toast.LENGTH_LONG).show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View assistantFragmentView = inflater.inflate(R.layout.fragment_assistant, container, false);
        assistantListView = assistantFragmentView.findViewById(R.id.assistant_list_view);

        assistantListView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        assistantListView.setLayoutManager(layoutManager);

        assistantAdapter = new AssistantRecyclerViewAdapter();
        assistantListView.setAdapter(assistantAdapter);
        assistantListView.addItemDecoration(new DividerItemDecoration(assistantListView.getContext(), DividerItemDecoration.VERTICAL));
        return assistantFragmentView;
    }
}
