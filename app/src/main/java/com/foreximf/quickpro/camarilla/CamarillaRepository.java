package com.foreximf.quickpro.camarilla;


import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.foreximf.quickpro.database.ForexImfAppDatabase;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A repository class, as the mediator of query called in
 * {@link CamarillaViewModel} to {@link CamarillaDao}
 * for CRUD operations.
 */
public class CamarillaRepository {
    private CamarillaDao camarillaDao;
    private LiveData<List<Camarilla>> camarillaLiveData;
    private static Context _application;
    private ForexImfAppDatabase appDatabase;

    public CamarillaRepository(Context application) {
        _application = application;
        appDatabase = ForexImfAppDatabase.getDatabase(application);
        camarillaDao = appDatabase.camarillaModel();
        camarillaLiveData = camarillaDao.getAllCamarilla();
    }

    public LiveData<List<Camarilla>> getAllCamarilla() {
        if(camarillaLiveData == null) {
            camarillaLiveData = new MutableLiveData<>();
        }
        return camarillaLiveData;
    }

    public void addCamarilla(Camarilla camarilla) {
        new addCamarillaAsyncTask(camarillaDao).execute(camarilla);
    }

    private static class addCamarillaAsyncTask extends AsyncTask<Camarilla, Void, Void>{
        private CamarillaDao mAsyncTaskDao;
        addCamarillaAsyncTask(CamarillaDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Camarilla... camarillas) {
            mAsyncTaskDao.addCamarilla(camarillas[0]);
            return null;
        }
    }

    public void updateCamarilla(Camarilla camarilla) {
        new updateCamarillaAsyncTask(camarillaDao).execute(camarilla);
    }

    private static class updateCamarillaAsyncTask extends AsyncTask<Camarilla, Void, Void>{
        private CamarillaDao mAsyncTaskDao;
        updateCamarillaAsyncTask(CamarillaDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Camarilla... camarillas) {
            mAsyncTaskDao.updateCamarilla(camarillas[0]);
            return null;
        }
    }

    public Camarilla getCamarillaByServerId(int serverId) throws ExecutionException, InterruptedException {
        //        Log.d("SignalRepository", signal.getTitle());
        return new getCamarillaByServerIdAsyncTask().execute(serverId).get();
    }

    private static class getCamarillaByServerIdAsyncTask extends AsyncTask<Integer, Void, Camarilla>{

        @Override
        protected Camarilla doInBackground(Integer... integers) {
            ForexImfAppDatabase appDatabase = ForexImfAppDatabase.getDatabase(_application);
            CamarillaDao camarillaDao = appDatabase.camarillaModel();
//            Log.d("SignalRepository", "Server Id : " + integers[0]);
            //            Log.d("SignalRepository", "Null : " + (signal == null));
            return camarillaDao.getCamarillaByServerId(integers[0]);
        }
    }
}
