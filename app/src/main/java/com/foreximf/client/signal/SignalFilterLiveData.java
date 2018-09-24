package com.foreximf.client.signal;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import java.util.List;

/**
 * A custom live data class.
 * It uses model {@link SignalFilter}
 * It acts as a filter by extending {@link MediatorLiveData} and
 * declaring three method in the constructor which takes three parameters
 * (status, pair, group), each represents three spinners that can be found
 * in {@link SignalFragment}.
 */
class SignalFilterLiveData extends MediatorLiveData<SignalFilter> {
    SignalFilterLiveData(LiveData<List<Integer>> status, LiveData<List<Integer>> pair, LiveData<List<Integer>> group) {
        addSource(status, first ->
            setValue(new SignalFilter(first, pair.getValue(), group.getValue())));
        addSource(pair, second -> {
            setValue(new SignalFilter(status.getValue(), second, group.getValue()));
        });
        addSource(group, third -> {
            setValue(new SignalFilter(status.getValue(), pair.getValue(), third));
        });
    }
}
