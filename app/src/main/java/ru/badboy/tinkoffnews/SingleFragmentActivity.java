package ru.badboy.tinkoffnews;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Евгений on 01.05.2016.
 */
public abstract class SingleFragmentActivity extends AppCompatActivity { //заготовка для активностей с одним фрагментом
    protected abstract android.support.v4.app.Fragment createFragment();

    //вынесено в отдельную функцию на случай реализации списка/детализации для планшетов
    protected int getLayoutResId() {
        return R.layout.activity_tinkoff_news;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }
}
