package com.foreximf.quickpro.signal;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.foreximf.quickpro.database.ForexImfAppDatabase;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A repository class, as the mediator of query called in
 * {@link SignalViewModel} to {@link SignalDao}
 * for CRUD operations.
 */
public class SignalRepository {
    private SignalDao signalDao;
    private LiveData<List<Signal>> signalLiveData;
    private static Context _application;

    public SignalRepository(Context application) {
        _application = application;
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

    public Signal getSignalByServerId(int serverId) throws ExecutionException, InterruptedException {
        //        Log.d("SignalRepository", signal.getTitle());
        return new getSignalByServerIdAsyncTask().execute(serverId).get();
    }

    private static class getSignalByServerIdAsyncTask extends AsyncTask<Integer, Void, Signal>{

        @Override
        protected Signal doInBackground(Integer... integers) {
            ForexImfAppDatabase appDatabase = ForexImfAppDatabase.getDatabase(_application);
            SignalDao signalDao = appDatabase.signalModel();
//            Log.d("SignalRepository", "Server Id : " + integers[0]);
            //            Log.d("SignalRepository", "Null : " + (signal == null));
            return signalDao.getSignalByServerId(integers[0]);
        }
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
