package com.foreximf.client.signal;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.foreximf.client.database.ForexImfAppDatabase;

import java.util.List;

/**
 * A repository class, as the mediator of query called in
 * {@link SignalViewModel} to {@link SignalDao}
 * for CRUD operations.
 */
public class SignalRepository {
    private SignalDao signalDao;
    private LiveData<List<Signal>> signalLiveData;

    public SignalRepository(Application application) {
        ForexImfAppDatabase appDatabase = ForexImfAppDatabase.getDatabase(application);
        signalDao = appDatabase.signalModel();
        signalLiveData = signalDao.getAllSignal();
    }

//    public LiveData<List<Signal>> getAllSignal() {
//        if(signalLiveData == null) {
//            signalLiveData = new MutableLiveData<>();
//        }
//        return signalLiveData;
//    }
//
//    public LiveData<List<Signal>> getSignalByCount(int count) {
//        signalLiveData = signalDao.getSignalByCount(count);
//        return signalLiveData;
//    }

//    public LiveData<List<Signal>> getSignalByStatus(List<Integer> status) {
//        signalLiveData = signalDao.getSignalByStatus(status);
//        return signalLiveData;
//    }

    public LiveData<List<Signal>> getSignalByCondition(SignalFilter filter) {
        signalLiveData = signalDao.getSignalByCondition(filter.getStatus(), filter.getPair(), filter.getGroup());
        return signalLiveData;
    }

    public Signal getSignalByServerId(int serverId) {
        return signalDao.getSignalByServerId(serverId);
    }

    public void addSignal(Signal signal) {
        new addSignalAsyncTask(signalDao).execute(signal);
    }

    private static class addSignalAsyncTask extends AsyncTask<Signal, Void, Void>{
        private SignalDao mAsyncTaskDao;
        addSignalAsyncTask(SignalDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Signal... signals) {
            mAsyncTaskDao.addSignal(signals[0]);
            return null;
        }
    }

    public void updateSignal(Signal signal) {
        new updateSignalAsyncTask(signalDao).execute(signal);
    }

    private static class updateSignalAsyncTask extends AsyncTask<Signal, Void, Void>{
        private SignalDao mAsyncTaskDao;
        updateSignalAsyncTask(SignalDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Signal... signals) {
            mAsyncTaskDao.updateSignal(signals[0]);
            return null;
        }
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
