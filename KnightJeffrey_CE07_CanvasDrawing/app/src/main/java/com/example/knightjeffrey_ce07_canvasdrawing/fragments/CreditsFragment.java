package com.example.knightjeffrey_ce07_canvasdrawing.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.knightjeffrey_ce07_canvasdrawing.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreditsFragment extends Fragment {


    public CreditsFragment() {
        // Required empty public constructor
    }

    public static CreditsFragment newInstance() {
        
        Bundle args = new Bundle();
        
        CreditsFragment fragment = new CreditsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_credits, container, false);
    }

}
