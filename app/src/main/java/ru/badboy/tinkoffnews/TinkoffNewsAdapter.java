package ru.badboy.tinkoffnews;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Евгений on 19.07.2016.
 */
//адаптер для RecyclerView
public class TinkoffNewsAdapter extends RecyclerView.Adapter<TinkoffNewsAdapter.MyViewHolder> {

    private List<NewsItem> newsItems;
    private ItemClickListener itemClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView newsItemTextView;

        public MyViewHolder(View view) {
            super(view);
            newsItemTextView = (TextView) view.findViewById(R.id.news_item_textView);
        }
    }

    public TinkoffNewsAdapter(List<NewsItem> newsItems, ItemClickListener itemClickListener) {
        this.newsItems = newsItems;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_item_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final NewsItem newsItem = newsItems.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onItemClick(newsItem);
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) { //устаревший метод до 24 api
            holder.newsItemTextView.setText(Html.fromHtml(newsItem.getText(), Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.newsItemTextView.setText(Html.fromHtml(newsItem.getText()));
        }

    }

    @Override
    public int getItemCount() {
        return newsItems.size();
    }

    public interface ItemClickListener {
        void onItemClick(NewsItem item);
    }

}
