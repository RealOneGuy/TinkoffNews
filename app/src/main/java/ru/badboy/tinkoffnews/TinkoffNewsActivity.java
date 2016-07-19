package ru.badboy.tinkoffnews;

public class TinkoffNewsActivity extends SingleFragmentActivity {
    @Override
    protected android.support.v4.app.Fragment createFragment(){
        return new TinkoffNewsFragment();
    }
}
