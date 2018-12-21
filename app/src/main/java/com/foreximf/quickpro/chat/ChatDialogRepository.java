package com.foreximf.quickpro.chat;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.foreximf.quickpro.chat.model.ChatDepartment;
import com.foreximf.quickpro.chat.model.ChatDepartmentDao;
import com.foreximf.quickpro.database.ForexImfAppDatabase;

import java.util.List;

public class ChatDialogRepository {
    private ChatDepartmentDao chatDepartmentDao;
    private LiveData<List<ChatDepartment>> listLiveData;
    private static Context _application;

    public ChatDialogRepository(Context application) {
        _application = application;
        ForexImfAppDatabase appDatabase = ForexImfAppDatabase.getDatabase(application);
        chatDepartmentDao = appDatabase.chatDepartmentDao();
        listLiveData = chatDepartmentDao.getAll();
    }

    public LiveData<List<ChatDepartment>> getAll() {
        if(listLiveData == null) {
            listLiveData = new MutableLiveData<>();
        }
        return listLiveData;
    }

    public void add(ChatDepartment chatDepartment) {
        new addAsyncTask(chatDepartmentDao).execute(chatDepartment);
    }

    public void add(ChatDepartment... chatDepartments) {
        new addAsyncTask(chatDepartmentDao).execute(chatDepartments);
    }

    private static class addAsyncTask extends AsyncTask<ChatDepartment, Void, Void> {
        private ChatDepartmentDao mAsyncTaskDao;
        addAsyncTask(ChatDepartmentDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(ChatDepartment... chatDepartments) {
            for(int i = 0; i < chatDepartments.length; i++) {
                mAsyncTaskDao.add(chatDepartments[i]);
            }
            return null;
        }
    }

    public void update(ChatDepartment chatDepartment) {
        new updateAsyncTask(chatDepartmentDao).execute(chatDepartment);
    }

    private static class updateAsyncTask extends AsyncTask<ChatDepartment, Void, Void> {
        private ChatDepartmentDao mAsyncTaskDao;
        updateAsyncTask(ChatDepartmentDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(ChatDepartment... chatDepartments) {
            for(int i = 0; i < chatDepartments.length; i++) {
                mAsyncTaskDao.update(chatDepartments[i]);
            }
            return null;
        }
    }
}
