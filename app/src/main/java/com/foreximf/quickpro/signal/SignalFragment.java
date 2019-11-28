package com.foreximf.quickpro.signal;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.foreximf.quickpro.R;
import com.foreximf.quickpro.util.ArchLifecycleApp;
import com.foreximf.quickpro.util.DateFormatter;
import com.foreximf.quickpro.util.ImfLinearLayoutManager;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignalFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignalFragment extends Fragment implements SignalViewHolderListener {
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

    private Spinner statusSpinner, currencySpinner, groupSpinner;
    private SignalViewHolderListener listener;
    private Observer<List<Signal>> signalObserver;
    SwipeRefreshLayout mSwipeRefreshLayout;

    private static final int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 3;
    private int currentPage = PAGE_START;

    private Tracker mTracker;

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
        listener = this;
        ArchLifecycleApp application = (ArchLifecycleApp) Objects.requireNonNull(getActivity()).getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Signal");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
//        Log.d("Signal Fragment", "UPILLLL");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

//        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_signal, container, false);

        signalListView = view.findViewById(R.id.signal_list_view);

        layoutManager = new LinearLayoutManager(getActivity(), ImfLinearLayoutManager.VERTICAL, false);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        signalListView.setLayoutManager(layoutManager);

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        //News news = new News("Breaking News", "Yen Menguat", "Test Content");
//        ArrayList<Signal> _signalList = new ArrayList<>();
//        newsList.add(news);
        signalAdapter = new SignalRecyclerViewAdapter(getLayoutInflater(), this);

        signalListView.setAdapter(signalAdapter);
        signalListView.addOnScrollListener(scrollListener);
//        signalListView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        viewModel = ViewModelProviders.of(this).get(SignalViewModel.class);

        signalObserver = new Observer<List<Signal>>() {
            @Override
            public void onChanged(@Nullable List<Signal> signalList) {
                signalAdapter.setSignal(signalList);
            }
        };

        viewModel.getAllSignal().observe(this, signalObserver);

        SharedPreferences preferences = getActivity().getSharedPreferences("spinner-select", Context.MODE_PRIVATE);

        statusSpinner = view.findViewById(R.id.signal_status_spinner);
        ArrayAdapter<CharSequence> sAdapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.spinner_status_list_item_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        statusSpinner.setAdapter(sAdapter);
        statusSpinner.setOnItemSelectedListener(statusSelected);
        statusSpinner.setSelection(preferences.getInt("status-selected", 0));

        currencySpinner = view.findViewById(R.id.signal_currency_spinner);
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(currencySpinner);

            // Set popupWindow height to 500px
            popupWindow.setHeight(740);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }
        ArrayAdapter<CharSequence> cAdapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.spinner_currency_list_item_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        cAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        currencySpinner.setAdapter(cAdapter);
        currencySpinner.setOnItemSelectedListener(pairSelected);
        currencySpinner.setSelection(preferences.getInt("pair-selected", 0));

        groupSpinner = view.findViewById(R.id.signal_group_spinner);
        ArrayAdapter<CharSequence> gAdapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.spinner_group_list_item_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        gAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        groupSpinner.setAdapter(gAdapter);
        groupSpinner.setOnItemSelectedListener(groupSelected);
        groupSpinner.setSelection(preferences.getInt("group-selected", 0));

        updateItems();

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

    SwipeRefreshLayout.OnRefreshListener onRefreshListener = () -> refreshItems();

    void refreshItems() {
        // Load items
        // ...
        updateItems();
        // Load complete
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...

        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void updateItems() {
        String url = "https://client.foreximf.com/update-signal";
        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
//        long time = preferences.getLong("last-update-time", 0);
//        preferences.edit().remove("last-update-time").apply();
        String time = preferences.getString("last-update-time", "");
        Map<String, String>  params = new HashMap<>();
//        Date date = DateConverter.toDate(time);
//        String dateString = DateFormatter.format(date, "yyyy-MM-dd HH:mm:ss");
        String token = preferences.getString("login-token", "");
//        Log.d("SignalFragment", "Token : " + token);
//        Log.d("Signal Fragment", "Update Time : " + time);
        params.put("token", token);
        params.put("update-time", time);
        JSONObject parameters = new JSONObject(params);
//        Log.d("SignalFragment", "Date : " + time);
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, parameters,
                response -> {
                    // response
                    try {
                        boolean error = response.getBoolean("error");
//                        Log.d("SignalFragment", "ERROR : " + error);
//                        Log.d("SignalFragment", "Error : " + error);
//                        Log.d("SignalFragment", "Eligible : " + response.getString("tes"));
                        if(!error) {
                            boolean needToUpdate = response.getBoolean("need-to-update");
//                            Log.d("SignalFragment", "Need to Update : " + needToUpdate);
//                            Log.d("SignalFragment", "Last Update Time : " + response.getJSONObject("last-update-time").getString("date"));
                            if(needToUpdate) {
                                String dateString = response.getJSONObject("last-update-time").getString("date");
//                                Date date = DateFormatter.format(dateString);
//                                String newDateString = DateFormatter.format(date, "dd MMM, HH:mm");
                                preferences.edit().putString("last-update-time", dateString).apply();
                                JSONArray signalArray = response.getJSONArray("signals");
                                for(int i = 0 ; i < signalArray.length() ; i++) {
                                    JSONObject signalJson = signalArray.getJSONObject(i);
                                    String signalTitle = signalJson.getString("title");
                                    Log.d("SignalFragment", "Status : " + signalJson.getInt("status"));
                                    String signalContent = signalJson.getString("content");
                                    Date signalDate = DateFormatter.format(signalJson.getString("created_at"));
                                    int signalRead = 0;
                                    int signalPair = signalJson.getInt("currency_pair");
                                    int signalOrderType = signalJson.getInt("order_type");
                                    int signalResult = signalJson.getInt("result");
                                    int signalStatus = signalJson.getInt("status");
                                    int signalGroup = signalJson.getInt("signal_group");
                                    int signalId = signalJson.getInt("id");
                                    String signalKeterangan = signalJson.getString("keterangan");
                                    SignalRepository repository = new SignalRepository(getContext());
//                                    Log.d("SignalFragment", "Status : " + signalStatus);
//                                    if(signalStatus == 2) {
//                                        Signal signal = new Signal(signalTitle, signalContent, signalDate, signalRead, signalPair, signalOrderType, signalResult, signalStatus, signalGroup, signalId);
//                                        repository.addSignal(signal);
//                                    }else{
                                    Signal temp = repository.getSignalByServerId(signalId);
                                    if(temp == null) {
                                        Signal signal = new Signal(signalPair, signalTitle, signalContent, signalDate, signalOrderType, signalResult, signalKeterangan, signalStatus, signalGroup, signalRead, signalId);
                                        repository.addSignal(signal);
                                    }else{
                                        Signal signal = new Signal(temp.getId(), signalPair, signalTitle, signalContent, signalDate, signalOrderType, signalResult, signalKeterangan, signalStatus, signalGroup, signalRead, signalId);
                                        repository.updateSignal(signal);
                                    }
//                                        Signal signal = new Signal(temp.getId(), signalTitle, signalContent, signalDate, signalRead, signalPair, signalOrderType, signalResult, signalStatus, signalGroup, signalId);
//                                        repository.updateSignal(signal);
//                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // error
//                    Log.d("Error.Response", "" + error.getMessage());
                }
        );
        queue.add(postRequest);
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

    @Override
    public void onItemClickListener(Signal item) {
        Intent signalDetailIntent = new Intent(getContext(), SignalDetailActivity.class);
        putExtra(signalDetailIntent, item);
        startActivity(signalDetailIntent);
    }

    private void putExtra(Intent intent, Signal item) {
        intent.putExtra("server-id", item.getServerId());
        intent.putExtra("title", item.getTitle());
        intent.putExtra("content", item.getContent());
        intent.putExtra("last-update", item.getCreatedTime().getTime());
        intent.putExtra("currency-pair", item.getCurrencyPair());
        intent.putExtra("result", item.getResult());
        intent.putExtra("order-type", item.getOrderType());
        intent.putExtra("status", item.getStatus());
        intent.putExtra("group", item.getSignalGroup());
        intent.putExtra("keterangan", item.getKeterangan());
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
//            if(!recyclerView.canScrollVertically(-1)) {
//                //Pass order to delete badge
//                viewModel.updateSignalRead();
//                mListener.onFragmentInteraction(0);
//            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int itemCount = layoutManager.getItemCount();
            int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
            layoutManager.getChildCount();
//            int visibleThreshold = 10;
            if(!isLoading && itemCount - 1 == lastVisiblePosition) {
                loadMoreItems();
            }
        }
    };

    public void loadMoreItems() {
        isLoading = true;
        currentPage += 1;
    }

    public int getTotalPageCount() {
        return TOTAL_PAGES;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    public boolean isLoading() {
        return isLoading;
    }

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
//                layoutManager = new LinearLayoutManager(getActivity(), ImfLinearLayoutManager.VERTICAL, false);
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

    AdapterView.OnItemSelectedListener statusSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            viewModel.setSignalFilter(position, currencySpinner.getSelectedItemPosition(), groupSpinner.getSelectedItemPosition());
            SharedPreferences preferences = getActivity().getSharedPreferences("spinner-select", Context.MODE_PRIVATE);
            preferences.edit().putInt("status-selected", position).apply();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    AdapterView.OnItemSelectedListener pairSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//            Log.d("SignalFragment", "Pair : " + position);
            viewModel.setSignalFilter(statusSpinner.getSelectedItemPosition(), position, groupSpinner.getSelectedItemPosition());
            SharedPreferences preferences = getActivity().getSharedPreferences("spinner-select", Context.MODE_PRIVATE);
            preferences.edit().putInt("pair-selected", position).apply();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    AdapterView.OnItemSelectedListener groupSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            viewModel.setSignalFilter(statusSpinner.getSelectedItemPosition(), currencySpinner.getSelectedItemPosition(), position);
            SharedPreferences preferences = getActivity().getSharedPreferences("spinner-select", Context.MODE_PRIVATE);
            preferences.edit().putInt("group-selected", position).apply();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

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
