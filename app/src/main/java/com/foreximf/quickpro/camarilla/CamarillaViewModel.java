package com.foreximf.quickpro.camarilla;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class CamarillaViewModel extends AndroidViewModel {

    private CamarillaRepository repository;
    private LiveData<List<Camarilla>> camarillaLiveData;

    public CamarillaViewModel(@NonNull Application application) {
        super(application);
        repository = new CamarillaRepository(application);
        camarillaLiveData = repository.getAllCamarilla();
    }

    public LiveData<List<Camarilla>> getAllCamarilla() {
        return camarillaLiveData;
    }

    public void addCamarilla(Camarilla camarilla) {
        repository.addCamarilla(camarilla);
    }

    public void updateCamarilla(Camarilla camarilla) {
        repository.updateCamarilla(camarilla);
    }
}
