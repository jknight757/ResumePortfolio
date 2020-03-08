package com.example.knightjeffrey_ce07_canvasdrawing.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.knightjeffrey_ce07_canvasdrawing.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements View.OnClickListener {


    private MainMenuButtonListener listener;


    public MainFragment() {
        // Required empty public constructor
    }



    public interface MainMenuButtonListener{
         void startGame();
         void viewCredits();
    }

    public static MainFragment newInstance() {

        Bundle args = new Bundle();

        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof MainMenuButtonListener){
            listener = (MainMenuButtonListener) context;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();
        if(view != null){
            (view.findViewById(R.id.start_btn)).setOnClickListener(this);
            (view.findViewById(R.id.credits_btn)).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id){
            case R.id.start_btn:
                listener.startGame();
                break;
            case R.id.credits_btn:
                listener.viewCredits();
                break;
        }
    }
}
