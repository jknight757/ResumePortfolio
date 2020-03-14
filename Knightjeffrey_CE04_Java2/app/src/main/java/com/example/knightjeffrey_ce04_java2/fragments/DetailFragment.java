//Jeffrey Knight
//Java2 1911
// CE04 contacts
package com.example.knightjeffrey_ce04_java2.fragments;


import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.knightjeffrey_ce04_java2.Contact;
import com.example.knightjeffrey_ce04_java2.R;

public class DetailFragment extends Fragment {
    private static final String ARG_ID = "ARG_ID";


    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance() {

        Bundle args = new Bundle();

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public static DetailFragment newInstance(Contact contact) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_ID,contact);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getArguments() != null && getContext() != null && getView() != null){
            Contact contact = (Contact) getArguments().getSerializable(ARG_ID);
            TextView name = getView().findViewById(R.id.fullname_textview);
            ImageView image = getView().findViewById(R.id.contact_image);
            ListView lv = getView().findViewById(R.id.numbers_list_view);


            if(contact != null) {
                name.setText(contact.getName());
                if(contact.getImgPath() == null){
                    image.setImageResource(R.drawable.profile);
                }else{
                    image.setImageURI(Uri.parse(contact.getImgPath()));
                }


                if (lv != null) {
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1, contact.getNums());
                    lv.setAdapter(arrayAdapter);
                }
            }

        }
    }
}
