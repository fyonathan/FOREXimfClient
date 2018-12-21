package com.foreximf.quickpro.chat;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.foreximf.quickpro.chat.model.ChatDepartment;

import java.util.List;

public class ChatDialogViewModel extends AndroidViewModel {

    private ChatDialogRepository repository;
    private LiveData<List<ChatDepartment>> listLiveData;

    public ChatDialogViewModel(@NonNull Application application) {
        super(application);
        repository = new ChatDialogRepository(application);
        listLiveData = repository.getAll();
    }

    public LiveData<List<ChatDepartment>> getAll() {
        return listLiveData;
    }
}
