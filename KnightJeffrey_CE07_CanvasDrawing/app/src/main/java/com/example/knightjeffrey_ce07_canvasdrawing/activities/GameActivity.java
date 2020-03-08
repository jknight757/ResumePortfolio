package com.example.knightjeffrey_ce07_canvasdrawing.activities;

import android.os.Bundle;

import com.example.knightjeffrey_ce07_canvasdrawing.fragments.GameFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.knightjeffrey_ce07_canvasdrawing.R;
import com.example.knightjeffrey_ce07_canvasdrawing.fragments.InventoryFragment;

public class GameActivity extends AppCompatActivity implements GameFragment.MenuListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getIntent() != null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, GameFragment.newInstance())
                    .commit();
        }
    }

    @Override
    public void goToInventory() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, InventoryFragment.newInstance())
                .addToBackStack(InventoryFragment.class.getName())
                .commit();
    }
}
