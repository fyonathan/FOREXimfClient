package com.foreximf.quickpro.news;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.foreximf.quickpro.database.ForexImfAppDatabase;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class NewsRepository {
    private NewsDao newsDao;
    private LiveData<List<News>> newsLiveData;

    public NewsRepository(Application application) {
        ForexImfAppDatabase appDatabase = ForexImfAppDatabase.getDatabase(application);
        newsDao = appDatabase.newsModel();
        newsLiveData = newsDao.getAllNews();
    }

    public LiveData<List<News>> getAllNews() {
        if(newsLiveData == null) {
            newsLiveData = new MutableLiveData<>();
        }
        return newsLiveData;
    }

    public void addNews(News news) {
        new addNewsAsyncTask(newsDao).execute(news);
    }

    private static class addNewsAsyncTask extends AsyncTask<News, Void, Void> {
        private NewsDao mAsyncTaskDao;

        addNewsAsyncTask(NewsDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(News... news) {
            mAsyncTaskDao.addNews(news[0]);
            return null;
        }
    }

    public void updateNews(News news) {
        new updateNewsAsyncTask(newsDao).execute(news);
    }

    private static class updateNewsAsyncTask extends AsyncTask<News, Void, Void> {
        private NewsDao mAsyncTaskDao;

        updateNewsAsyncTask(NewsDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(News... news) {
            mAsyncTaskDao.updateNews(news[0]);
            return null;
        }
    }

    public News getNewsByType(String type) {
        try {
            News news = new getNewsByTypeAsyncTask(newsDao).execute(type).get();
//            Log.d("News Repo", "Type : " + news.getType());
            return news;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected static class getNewsByTypeAsyncTask extends AsyncTask<String, Void, News> {
        private NewsDao mAsyncTaskDao;

        getNewsByTypeAsyncTask(NewsDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected News doInBackground(String... params) {
            return mAsyncTaskDao.getNewsByType(params[0]);
        }
    }
}
