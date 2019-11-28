package com.foreximf.quickpro.chat;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.foreximf.quickpro.chat.model.ChatThreadComplete;

import java.util.List;

public class ChatDialogViewModel extends AndroidViewModel {

    private ChatRepository repository;
    private LiveData<List<ChatThreadComplete>> listLiveData;

    public ChatDialogViewModel(@NonNull Application application) {
        super(application);
        repository = ChatRepository.getInstance(application);
        listLiveData = repository.getChatThreadComplete();
    }

    public void refresh() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication());
        repository.syncLastMessage(sharedPreferences.getString("user-id", ""));
    }

    public LiveData<List<ChatThreadComplete>> getAll() {
        return listLiveData;
    }
}
