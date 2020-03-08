//Jeffrey Knight
// MDF 3
// Canvas drawing CE07
package com.example.knightjeffrey_ce07_canvasdrawing.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;

import com.example.knightjeffrey_ce07_canvasdrawing.R;
import com.example.knightjeffrey_ce07_canvasdrawing.fragments.CreditsFragment;
import com.example.knightjeffrey_ce07_canvasdrawing.fragments.MainFragment;
import com.example.knightjeffrey_ce07_canvasdrawing.models.Item;
import com.example.knightjeffrey_ce07_canvasdrawing.models.ItemsManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainFragment.MainMenuButtonListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, MainFragment.newInstance())
                .addToBackStack(MainFragment.class.getName())
                .commit();
    }

    @Override
    public void startGame() {
        Intent gameIntent = new Intent(this, GameActivity.class);
        startActivity(gameIntent);

        // do initial game setup
        ItemsManager itemsManager = ItemsManager.getInstance();
        InputStream is = getResources().openRawResource(R.raw.items);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8")));

        String line;
        ArrayList<Item> allItems = new ArrayList<>();

        try{
            while((line = reader.readLine()) != null){
                String[] tokens = line.split(",");
                allItems.add(new Item(tokens[0], Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2])));
                Log.i("File", "Read line: " + tokens[0] + ", " + tokens[1] + ", " + tokens[2]);
            }

        }catch (IOException e){
            e.printStackTrace();
        }

        itemsManager.populateAllItems(allItems);
        itemsManager.setClickPoints(new ArrayList<Point>());

    }

    @Override
    public void viewCredits() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, CreditsFragment.newInstance())
                .addToBackStack(CreditsFragment.class.getName())
                .commit();


    }
}
