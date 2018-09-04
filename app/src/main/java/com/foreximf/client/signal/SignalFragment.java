package com.foreximf.client.signal;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.foreximf.client.R;


import java.util.ArrayList;
import java.util.List;

import q.rorbin.badgeview.Badge;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignalFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignalFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView signalListView;
    SignalRecyclerViewAdapter signalAdapter;
    LinearLayoutManager layoutManager;
    LifecycleOwner owner = this;

    private SignalViewModel viewModel;

    private OnFragmentInteractionListener mListener;

    public SignalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SignalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignalFragment newInstance() {
        SignalFragment fragment = new SignalFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
//        Log.d("Signal Fragment", "UPILLLL");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

//        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_signal, container, false);

        signalListView = view.findViewById(R.id.signal_list_view);

        layoutManager = new LinearLayoutManager(getActivity(), SignalLinearLayoutManager.VERTICAL, false);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        signalListView.setLayoutManager(layoutManager);

        //News news = new News("Breaking News", "Yen Menguat", "Test Content");
//        ArrayList<Signal> _signalList = new ArrayList<>();
//        newsList.add(news);
        signalAdapter = new SignalRecyclerViewAdapter(getActivity(), getLayoutInflater());
//        signalAdapter.setHasStableIds(true);
//        signalAdapter.setOnBottomReachedListener(bottomReachedListener);

        signalListView.setAdapter(signalAdapter);
        signalListView.addOnScrollListener(scrollListener);
//        signalListView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        viewModel = ViewModelProviders.of(this).get(SignalViewModel.class);

        final Observer<List<Signal>> signalObserver = new Observer<List<Signal>>() {

            @Override
            public void onChanged(@Nullable List<Signal> signalList) {
                signalAdapter.setSignal(signalList);
            }
        };

        viewModel.getSignalByCount(10).observe(this, signalObserver);

//        SharedPreferences preferences = getActivity().getSharedPreferences("signal", Context.MODE_PRIVATE);
//        Log.d("Signal Fragment", "ARGH : " + preferences.getInt("current_spinner", -1));
//        if(preferences.getInt("current_spinner", -1) == -1) {
//            preferences.edit().putInt("current_spinner", 0).apply();
//            viewModel.getSignalByCount(5).observe(this, signalObserver);
//        }else{
//            int selectedValue = preferences.getInt("current_spinner", -1);
//            viewModel.getSignalByCount(selectedValue).observe(this, signalObserver);
//        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(int badgeValue);
    }

    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if(!recyclerView.canScrollVertically(-1)) {
                //Pass order to delete badge
                viewModel.updateSignalRead();
                mListener.onFragmentInteraction(0);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.signal_toolbar_menu, menu);
//
//        MenuItem item = menu.findItem(R.id.action_list);
//        Spinner spinner = (Spinner) item.getActionView();
//
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
//                R.array.spinner_list_item_array, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        spinner.setAdapter(adapter);
//        SharedPreferences preferences = getActivity().getSharedPreferences("signal", Context.MODE_PRIVATE);
//        int selectedValue = preferences.getInt("current_spinner", -1);
//        spinner.setSelection(selectedValue);
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Log.d("Signal Fragment", "Position : " + position + ", ID : " + id);
//                layoutManager = new LinearLayoutManager(getActivity(), SignalLinearLayoutManager.VERTICAL, false);
//                layoutManager.setReverseLayout(true);
//                layoutManager.setStackFromEnd(true);
//                signalListView.setLayoutManager(layoutManager);
//                signalAdapter = new SignalRecyclerViewAdapter(getActivity());
//
//                signalListView.setAdapter(signalAdapter);
//
//                final Observer<List<Signal>> signalObserver = new Observer<List<Signal>>() {
//
//                    @Override
//                    public void onChanged(@Nullable List<Signal> signalList) {
//                        signalAdapter.setSignal(signalList);
//                    }
//                };
//
//                SharedPreferences preferences = getActivity().getSharedPreferences("signal", Context.MODE_PRIVATE);
//                int selectedValue = preferences.getInt("current_spinner", -1);
//                viewModel.getSignalByCount((selectedValue + 1) * 5).removeObserver(signalObserver);
//                preferences.edit().putInt("current_spinner", position).apply();
//                Log.d("Signal Fragment", "ARGH : " + preferences.getInt("current_spinner", -1));
//                viewModel.getSignalByCount((position + 1) * 5).observe(owner, signalObserver);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    //    SignalRecyclerViewAdapter.OnBottomReachedListener bottomReachedListener = new SignalRecyclerViewAdapter.OnBottomReachedListener() {
//        @Override
//        public void onBottomReached(boolean removeBadge) {
//            mListener.onFragmentInteraction(removeBadge);
//        }
//    };
}
