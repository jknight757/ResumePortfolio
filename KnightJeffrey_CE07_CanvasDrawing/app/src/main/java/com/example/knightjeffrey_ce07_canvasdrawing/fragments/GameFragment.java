package com.example.knightjeffrey_ce07_canvasdrawing.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.knightjeffrey_ce07_canvasdrawing.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameFragment extends Fragment {

    private MenuListener listener;

    public GameFragment() {
        // Required empty public constructor
    }

    public static GameFragment newInstance() {

        Bundle args = new Bundle();

        GameFragment fragment = new GameFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface MenuListener{
        void goToInventory();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof MenuListener){
            listener = (MenuListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false);
    }




    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_game,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        listener.goToInventory();

        return super.onOptionsItemSelected(item);
    }

}
