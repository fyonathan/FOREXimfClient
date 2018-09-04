package com.foreximf.client.signal;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.foreximf.client.database.ForexImfAppDatabase;

import java.util.List;

public class SignalRepository {
    private SignalDao signalDao;
    private LiveData<List<Signal>> signalLiveData;

    SignalRepository(Application application) {
        ForexImfAppDatabase appDatabase = ForexImfAppDatabase.getDatabase(application);
        signalDao = appDatabase.signalModel();
        signalLiveData = signalDao.getAllSignal();
    }

    public LiveData<List<Signal>> getAllSignal() {
        if(signalLiveData == null) {
            signalLiveData = new MutableLiveData<>();
        }
        return signalLiveData;
    }

    public LiveData<List<Signal>> getSignalByCount(int count) {
        signalLiveData = signalDao.getSignalByCount(count);
        return signalLiveData;
    }

    public void updateSignalRead() {
        new updateSignalReadAsyncTask(signalDao).execute();
    }

    private static class updateSignalReadAsyncTask extends AsyncTask<Void, Void, Void> {
        private SignalDao mAsyncTaskDao;

        updateSignalReadAsyncTask(SignalDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.updateSignal();
            return null;
        }
    }
}
