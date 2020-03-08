package com.example.knightjeffrey_ce07_canvasdrawing.fragments;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.knightjeffrey_ce07_canvasdrawing.R;
import com.example.knightjeffrey_ce07_canvasdrawing.models.ItemsManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class InventoryFragment extends ListFragment {

    public InventoryFragment() {
        // Required empty public constructor
    }

    public static InventoryFragment newInstance() {
        
        Bundle args = new Bundle();
        
        InventoryFragment fragment = new InventoryFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inventory, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ItemsManager mgr = ItemsManager.getInstance();
        if(mgr != null){
            if(mgr.getMyItems().size() > 0){
                int goldValue = 0;
                String[] names =  new String[mgr.getMyItems().size()];

                for (int i = 0; i < mgr.getMyItems().size(); i++) {
                    names[i] = mgr.getMyItems().get(i).getName() + "  " + mgr.getMyItems().get(i).getValue();
                    if(mgr.getMyItems().get(i).getName().equals("Gold")){
                        goldValue += mgr.getMyItems().get(i).getValue();
                    }

                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,names);
                ListView lv = getView().findViewById(android.R.id.list);
                if(lv != null){
                    lv.setAdapter(arrayAdapter);
                }


                ((TextView)getView().findViewById(R.id.gold_value_lbl)).setText("Gold Value: " + goldValue);
            }
        }
    }
}
