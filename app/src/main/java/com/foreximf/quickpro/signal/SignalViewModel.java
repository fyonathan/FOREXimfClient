package com.foreximf.quickpro.signal;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that works as a view model for recycler view in {@link SignalFragment}
 * It receives the CRUD command from interfaces and passes it to {@link SignalRepository}.
 * The class also responsible to manage the filter logic for the display.
 */
public class SignalViewModel extends AndroidViewModel {

    private SignalRepository repository;
    private LiveData<List<Signal>> signalLiveData;
    private SignalFilterLiveData signalFilterLiveData;

    public SignalViewModel(@NonNull Application application) {
        super(application);
        repository = new SignalRepository(application);
        MutableLiveData<List<Integer>> status = new MutableLiveData<>();
        MutableLiveData<List<Integer>> pair = new MutableLiveData<>();
        MutableLiveData<List<Integer>> group = new MutableLiveData<>();
        signalFilterLiveData = new SignalFilterLiveData(status, pair, group);
        signalLiveData = Transformations.switchMap(signalFilterLiveData, input -> repository.getSignalByCondition(input));
    }

    /**
     *
     * @param status filter parameter
     * @param pair filter parameter
     * @param group filter parameter
     * called in {@link SignalFragment} to set the filter
     * and pass it to the {@link SignalFilterLiveData}
     */
    public void setSignalFilter(int status, int pair, int group) {
        List<Integer> tempStatus = new ArrayList<>();
        List<Integer> tempPair = new ArrayList<>();
        List<Integer> tempGroup = new ArrayList<>();
        if(status == 0) {
            tempStatus.add(2);
            tempStatus.add(3);
            tempStatus.add(4);
        }else{
            tempStatus.add(status + 1);
        }
        if(pair == 0) {
            for(int i = 1 ; i <= 20 ; i++) {
                tempPair.add(i);
            }
        }else{
            tempPair.add(pair);
        }
        if(group == 0) {
            tempGroup.add(1);
            tempGroup.add(2);
            tempGroup.add(3);
            tempGroup.add(4);
            tempGroup.add(5);
        }else{
            tempGroup.add(group);
        }

        this.signalFilterLiveData.setValue(new SignalFilter(tempStatus, tempPair, tempGroup));
    }

    public LiveData<List<Signal>> getAllSignal() {
        return signalLiveData;
    }

//    public LiveData<List<Signal>> getSignalByCount(int number) {
//        return repository.getSignalByCount(number);
//    }

    public void updateSignalRead() {
        repository.updateSignalRead();
    }
}
