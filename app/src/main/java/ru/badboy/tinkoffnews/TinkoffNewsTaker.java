package ru.badboy.tinkoffnews;

import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Евгений on 19.07.2016.
 */
public class TinkoffNewsTaker { //класс, отвечающий за загрузку

    public String getUrl(String urlSpec) throws IOException { //установка соединения
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return new String(out.toByteArray());
        } finally {
            connection.disconnect();
        }
    }

    public String takeNews() { //загрузка JSON-разметки всех новостей
        try {
            String url = Uri.parse(Constants.ENDPOINT_FOR_ALL_NEWS).toString();
            return getUrl(url);
        } catch (IOException ioe) {
            return null;
        }
    }

    public String takeDetailedNewsItem(String id) {//загрузка JSON-разметки конкретной новости
        try {
            String url = Uri.parse(Constants.ENDPOINT_FOR_DETAILED_INFO).buildUpon()
                    .appendQueryParameter("id", id)
                    .build().toString();
            return getUrl(url);
        } catch (IOException ioe) {
            return null;
        }
    }
}
