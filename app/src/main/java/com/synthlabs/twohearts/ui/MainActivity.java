package com.synthlabs.twohearts.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.synthlabs.twohearts.R;
import com.synthlabs.twohearts.ui.games.GamesFragment;
import com.synthlabs.twohearts.ui.home.HomeFragment;
import com.synthlabs.twohearts.ui.more.MoreFragment;
import com.synthlabs.twohearts.ui.notes.NotesFragment;
import com.synthlabs.twohearts.ui.us.UsFragment;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();

        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_us:
                    showFragment(new UsFragment());
                    return true;
                case R.id.nav_games:
                    showFragment(new GamesFragment());
                    return true;
                case R.id.nav_notes:
                    showFragment(new NotesFragment());
                    return true;
                case R.id.nav_more:
                    showFragment(new MoreFragment());
                    return true;
                case R.id.nav_home:
                default:
                    showFragment(new HomeFragment());
                    return true;
            }
        });

        // Select home by default
        if (savedInstanceState == null) {
            nav.setSelectedItemId(R.id.nav_home);
        }
    }

    private void showFragment(@NonNull Fragment fragment) {
        fm.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
