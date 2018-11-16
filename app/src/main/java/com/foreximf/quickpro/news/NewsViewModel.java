package com.foreximf.quickpro.news;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class NewsViewModel extends AndroidViewModel {

    private NewsRepository repository;
    private LiveData<List<News>> newsLiveData;

    public NewsViewModel(@NonNull Application application) {
        super(application);
        repository = new NewsRepository(application);
        newsLiveData = repository.getAllNews();
    }

    public LiveData<List<News>> getAllNews() {
        return newsLiveData;
    }

    public void addNews(News news) {
        repository.addNews(news);
    }

    public void updateNews(News news) {
        repository.updateNews(news);
    }

    public News getNewsByType(String type) {
        return repository.getNewsByType(type);
    }
}
