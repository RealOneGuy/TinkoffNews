package ru.badboy.tinkoffnews;

/**
 * Created by Евгений on 19.07.2016.
 */
public class DetailedNewsItemActivity extends SingleFragmentActivity {
    @Override
    protected android.support.v4.app.Fragment createFragment(){
        //обработку интента вынес сюда, чтобы фрагмент не зависил напрямую от данной активности
        //фрагмент создается с аргументами.
        String id = getIntent().getStringExtra(DetailedNewsItemFragment.EXTRA_NEWS_ITEM_ID);
        return DetailedNewsItemFragment.newInstance(id);
    }
}
