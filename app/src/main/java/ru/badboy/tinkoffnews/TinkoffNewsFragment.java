package ru.badboy.tinkoffnews;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Евгений on 19.07.2016.
 */
//Использованы фрагменты в обеих активностях на случай, если, например, понадобится сделать интерфейс Список/Детализация
//для больших экранов
public class TinkoffNewsFragment extends Fragment {

    private List<NewsItem> newsItems = new ArrayList<>();
    private TinkoffNewsAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        refreshNews();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tinkoff_news, parent, false);

        RecyclerView mNewsRecycler = (RecyclerView) v.findViewById(R.id.news_recyclerView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {//реализация pull to refresh
            @Override
            public void onRefresh() {
                refreshNews();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        mAdapter = new TinkoffNewsAdapter(newsItems, new TinkoffNewsAdapter.ItemClickListener() {
            @Override
            public void onItemClick(NewsItem item) {
                if (isOnline()) {//запуск активности с детализированным описанием по id
                    Intent i = new Intent(getActivity(), DetailedNewsItemActivity.class);
                    i.putExtra(DetailedNewsItemFragment.EXTRA_NEWS_ITEM_ID, item.getId());
                    startActivity(i);
                }
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mNewsRecycler.setLayoutManager(mLayoutManager);
        mNewsRecycler.setItemAnimator(new DefaultItemAnimator());
        mNewsRecycler.setAdapter(mAdapter);

        return v;
    }

    private class TakeNewsTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return new TinkoffNewsTaker().takeNews();
        }

        @Override
        protected void onPostExecute(String newsInJSON) {
            super.onPostExecute(newsInJSON);
            //парсим из JSON
            try {
                JSONObject dataJSONObj = new JSONObject(newsInJSON);
                //момент для кеширования
                try {
                    saveNews(dataJSONObj);
                } catch (IOException e) {
                    //just something
                    e.printStackTrace();
                }

                parseFromJSON(dataJSONObj);//главное
            } catch (JSONException e) {
                //just something
                e.printStackTrace();
            }
        }
    }

    private void sortNews() { //сортировка по убыванию
        Collections.sort(newsItems, new Comparator<NewsItem>() {
            @Override
            public int compare(NewsItem newsItem1, NewsItem newsItem2) {
                return (int) (newsItem2.getPublicationDate() - newsItem1.getPublicationDate());
            }
        });
    }

    //простейшее кэширование
    //можно было сделать через БД, но для данного случая наверно нет особого смысла в этом) (тем более без сторонних библиотек - ORMLite)
    private void saveNews(JSONObject dataJSONObject) throws IOException {
        Writer writer = null;
        try {
            OutputStream out = getActivity().openFileOutput(Constants.FILENAME_FOR_CASH, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(dataJSONObject.toString());
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    //загрузка из кэша
    private JSONObject loadNews() throws IOException, JSONException {
        BufferedReader reader = null;
        try {
            InputStream in = getActivity().openFileInput(Constants.FILENAME_FOR_CASH);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            return new JSONObject(jsonString.toString());
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    //Функция обновления новостей
    private void refreshNews() {
        if (isOnline()) {//если есть Интернет, то грузим
            new TakeNewsTask().execute();
        } else {//если нет, то берем из кэша
            JSONObject dataJSONObj;
            try {
                dataJSONObj = loadNews();
                parseFromJSON(dataJSONObj);
            } catch (Exception e) {
                //just something
                e.printStackTrace();
            }
        }
    }

    //функция проверки Интернет-соединения
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    //функция обработки получаемой JSON-разметки
    private void parseFromJSON(JSONObject dataJSONObj) throws JSONException {
        newsItems.clear();
        JSONArray newsItemsInJSON = dataJSONObj.getJSONArray("payload");

        for (int i = 0; i < newsItemsInJSON.length(); i++) {
            JSONObject itemInJSON = newsItemsInJSON.getJSONObject(i); //берем информацию о каждой новости

            JSONObject publicationDateInJSON = itemInJSON.getJSONObject("publicationDate");
            //получаем нужные данные о новости
            String id = itemInJSON.getString("id");
            String text = itemInJSON.getString("text");
            long publicationDate = Long.parseLong(publicationDateInJSON.getString("milliseconds"));

            NewsItem item = new NewsItem(); //добавляем новость
            item.setId(id);
            item.setText(text);
            item.setPublicationDate(publicationDate);
            newsItems.add(item);
        }
        sortNews();
        mAdapter.notifyDataSetChanged();
    }
}
