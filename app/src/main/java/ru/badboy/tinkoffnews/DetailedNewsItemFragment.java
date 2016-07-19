package ru.badboy.tinkoffnews;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Евгений on 19.07.2016.
 */
public class DetailedNewsItemFragment extends Fragment {

    public static final String EXTRA_NEWS_ITEM_ID = "ru.badboy.tinkoffnews.news_id";

    private TextView mDetailedInfoTextView;
    private String mId = "";

    public static DetailedNewsItemFragment newInstance(String id) {//создание фрагмента с аргументами
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_NEWS_ITEM_ID, id);
        DetailedNewsItemFragment fragment = new DetailedNewsItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mId = getArguments().getString(EXTRA_NEWS_ITEM_ID);
        new TakeDetailedInfoTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detailed_info, parent, false);

        mDetailedInfoTextView = (TextView) v.findViewById(R.id.detailed_info_textView);

        return v;
    }

    private class TakeDetailedInfoTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return new TinkoffNewsTaker().takeDetailedNewsItem(mId);
        }

        @Override
        protected void onPostExecute(String newsInJSON) {
            super.onPostExecute(newsInJSON);
            //парсим из JSON
            try {
                JSONObject dataJSONObj = new JSONObject(newsInJSON);
                JSONObject detailedInfoJSON = dataJSONObj.getJSONObject("payload");
                String content = detailedInfoJSON.getString("content");
                mDetailedInfoTextView.setText(Html.fromHtml(content));
            } catch (JSONException e) {
                //just something
                e.printStackTrace();
            }
        }
    }
}
