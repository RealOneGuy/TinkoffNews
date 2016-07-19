package ru.badboy.tinkoffnews;

/**
 * Created by Евгений on 19.07.2016.
 */
public class NewsItem {
    private String id, text;
    private long publicationDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(long publicationDate) {
        this.publicationDate = publicationDate;
    }
}
