//Jeffrey Knight
//Java2 1911
// CE04 contacts
package com.example.knightjeffrey_ce04_java2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.knightjeffrey_ce04_java2.fragments.ContactListFragment;
import com.example.knightjeffrey_ce04_java2.fragments.DetailFragment;

public class MainActivity extends AppCompatActivity implements ContactListFragment.ContactListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.detail_fragment_container, DetailFragment.newInstance()).commit();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.contactlist_fragment_container, ContactListFragment.newInstance()).commit();
    }

    @Override
    public void sendContact(Contact contact) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_fragment_container, DetailFragment.newInstance(contact)).commit();
    }

    @Override
    public void sendFirstContact(Contact contact) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_fragment_container, DetailFragment.newInstance(contact)).commit();
    }
}
