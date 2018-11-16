package com.foreximf.quickpro.camarilla;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CamarillaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CamarillaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CamarillaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView camarillaListView;
    CamarillaRecyclerViewAdapter adapter;
    LinearLayoutManager layoutManager;
    SwipeRefreshLayout swipeRefreshLayout;

    CamarillaRecyclerViewAdapter camarillaAdapter;
    CamarillaViewModel viewModel;
    private Observer<List<Camarilla>> camarillaObserver;

    private OnFragmentInteractionListener mListener;

    private Tracker mTracker;

    public CamarillaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
     * @return A new instance of fragment CamarillaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CamarillaFragment newInstance(/*String param1, String param2*/) {
        CamarillaFragment fragment = new CamarillaFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        ArchLifecycleApp application = (ArchLifecycleApp) Objects.requireNonNull(getActivity()).getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Camarilla");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_camarilla, container, false);
        View view = inflater.inflate(R.layout.fragment_camarilla, container, false);
        camarillaListView = view.findViewById(R.id.camarilla_list_view);
        layoutManager = new LinearLayoutManager(getActivity(), ImfLinearLayoutManager.VERTICAL, false);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        camarillaListView.setLayoutManager(layoutManager);

        swipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        camarillaAdapter = new CamarillaRecyclerViewAdapter(getLayoutInflater());

        camarillaListView.setAdapter(camarillaAdapter);
        viewModel = ViewModelProviders.of(this).get(CamarillaViewModel.class);
        camarillaObserver = camarillas -> camarillaAdapter.setCamarilla(camarillas);
        viewModel.getAllCamarilla().observe(this, camarillaObserver);

        updateItems();
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
        swipeRefreshLayout.setRefreshing(false);
    }

    private void updateItems() {
        String url = "https://client.foreximf.com/update-camarilla";
        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
//        long time = preferences.getLong("last-update-time", 0);
//        preferences.edit().remove("last-update-time").apply();
        String time = preferences.getString("last-camarilla-update-time", "");
//        Log.d("CamarillaFragment", "Time AAAA : " + time);
        Map<String, String> params = new HashMap<>();
//        Date date = DateConverter.toDate(time);
//        String dateString = DateFormatter.format(date, "yyyy-MM-dd HH:mm:ss");
        String token = preferences.getString("login-token", "");
//        Log.d("CamarillaFragment", "Token : " + token);
        params.put("token", token);
        params.put("update-time", time);
        JSONObject parameters = new JSONObject(params);
//        Log.d("CamarillaFragment", "Date : " + time);
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, parameters,
                response -> {
                    // response
                    try {
                        boolean error = response.getBoolean("error");
//                        Log.d("CamarillaFragment", "Error : " + error);
//                        Log.d("SignalFragment", "Eligible : " + response.getString("tes"));
                        if(!error) {
                            boolean needToUpdate = response.getBoolean("need-to-update");
                            Log.d("CamarillaFragment", "Need to Update : " + needToUpdate);
                            if(needToUpdate) {
                                String dateString = response.getJSONObject("last-update-time").getString("date");
//                                Date date = DateFormatter.format(dateString);
//                                String newDateString = DateFormatter.format(date, "dd MMM, HH:mm");
                                preferences.edit().putString("last-camarilla-update-time", dateString).apply();
                                Log.d("CamarillaFragment", "Time after update : " + dateString);
                                JSONArray camarillaArray = response.getJSONArray("camarilla");
                                for(int i = 0 ; i < camarillaArray.length() ; i++) {
                                    JSONObject camarillaJson = camarillaArray.getJSONObject(i);
                                    int serverId = camarillaJson.getInt("id");
                                    String camarillaPairName = camarillaJson.getString("currency_pair_name");
                                    Date camarillaDate = DateFormatter.format(camarillaJson.getString("updated_at"));
                                    float pivot = (float) camarillaJson.getDouble("pivot");
                                    float sellArea = (float) camarillaJson.getDouble("sell_area");
                                    float sellTp1 = (float) camarillaJson.getDouble("sell_tp1");
                                    float sellTp2 = (float) camarillaJson.getDouble("sell_tp2");
                                    float sellSl = (float) camarillaJson.getDouble("sell_sl");
                                    float buyArea = (float) camarillaJson.getDouble("buy_area");
                                    float buyTp1 = (float) camarillaJson.getDouble("buy_tp1");
                                    float buyTp2 = (float) camarillaJson.getDouble("buy_tp2");
                                    float buySl = (float) camarillaJson.getDouble("buy_sl");
                                    float buyBreakoutArea = (float) camarillaJson.getDouble("buy_breakout_area");
                                    float buyBreakoutTp1 = (float) camarillaJson.getDouble("buy_breakout_tp1");
                                    float buyBreakoutTp2 = (float) camarillaJson.getDouble("buy_breakout_tp2");
                                    float buyBreakoutSl = (float) camarillaJson.getDouble("buy_breakout_sl");
                                    float sellBreakoutArea = (float) camarillaJson.getDouble("sell_breakout_area");
                                    float sellBreakoutTp1 = (float) camarillaJson.getDouble("sell_breakout_tp1");
                                    float sellBreakoutTp2 = (float) camarillaJson.getDouble("sell_breakout_tp2");
                                    float sellBreakoutSl = (float) camarillaJson.getDouble("sell_breakout_sl");
//                                    int camarillaId = camarillaJson.getInt("id");

                                    CamarillaRepository repository = new CamarillaRepository(getContext());
//                                    if(signalStatus == 2) {
//                                        Signal signal = new Signal(signalTitle, signalContent, signalDate, signalRead, signalPair, signalOrderType, signalResult, signalStatus, signalGroup, signalId);
//                                        repository.addSignal(signal);
//                                    }else{
                                    Camarilla temp = repository.getCamarillaByServerId(serverId);
                                    if(temp == null) {
                                        Camarilla camarilla = new Camarilla(camarillaPairName, pivot, sellArea, sellTp1, sellTp2, sellSl, buyArea, buyTp1, buyTp2, buySl, buyBreakoutArea, buyBreakoutTp1, buyBreakoutTp2, buyBreakoutSl, sellBreakoutArea, sellBreakoutTp1, sellBreakoutTp2, sellBreakoutSl, camarillaDate, serverId);
                                        repository.addCamarilla(camarilla);
                                    }else{
                                        Camarilla camarilla = new Camarilla(temp.id, camarillaPairName, pivot, sellArea, sellTp1, sellTp2, sellSl, buyArea, buyTp1, buyTp2, buySl, buyBreakoutArea, buyBreakoutTp1, buyBreakoutTp2, buyBreakoutSl, sellBreakoutArea, sellBreakoutTp1, sellBreakoutTp2, sellBreakoutSl, camarillaDate, serverId);
                                        repository.updateCamarilla(camarilla);
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
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        void onFragmentInteraction(Uri uri);
    }
}
