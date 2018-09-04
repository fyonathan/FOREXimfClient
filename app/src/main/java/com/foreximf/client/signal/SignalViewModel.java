package com.foreximf.client.signal;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class SignalViewModel extends AndroidViewModel {

    private SignalRepository repository;
    private LiveData<List<Signal>> signalLiveData;

    public SignalViewModel(@NonNull Application application) {
        super(application);
        repository = new SignalRepository(application);
        signalLiveData = repository.getAllSignal();
    }

    public LiveData<List<Signal>> getAllSignal() {
        return signalLiveData;
    }

    public LiveData<List<Signal>> getSignalByCount(int number) {
        return repository.getSignalByCount(number);
    }

    public void updateSignalRead() {
        repository.updateSignalRead();
    }
}
