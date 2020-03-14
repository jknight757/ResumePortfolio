package com.example.knightjeffrey_ce03_java2.fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.knightjeffrey_ce03_java2.MainActivity;
import com.example.knightjeffrey_ce03_java2.R;
import com.example.knightjeffrey_ce03_java2.models.ImageGridAdapter;

import java.io.File;
import java.io.Serializable;
import java.util.Objects;

public class ImageGridFragment extends Fragment{


    private static final String ARG_FILES_NAME = "ARG_FOLDER_NAME";
    private File[] files;
    private ImageListener listener;

    public ImageGridFragment() {
        // Required empty public constructor
    }
    public interface ImageListener{
        void sendImage(File file);
    }
    public static ImageGridFragment newInstance(File[] files) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_FILES_NAME,files);
        ImageGridFragment fragment = new ImageGridFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof ImageListener){
            listener = (ImageListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getArguments() != null && getContext() != null){
            files = (File[]) getArguments().getSerializable(ARG_FILES_NAME);

            GridView grid = Objects.requireNonNull(getView()).findViewById(R.id.grid_fragment);

            if(grid != null){
                ImageGridAdapter IGA = new ImageGridAdapter(getContext(),files);
                grid.setAdapter(IGA);
                grid.setOnItemClickListener(itemClicked);
            }
            Objects.requireNonNull(grid).setVisibility(View.VISIBLE);

        }
    }

    private final AdapterView.OnItemClickListener itemClicked = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(MainActivity.sentFromAppB){
                Log.i("Send from app B", "onItemClick: ");
                listener.sendImage(files[position]);

            }else{

                Intent galleryIntent = new Intent(Intent.ACTION_VIEW);
                galleryIntent.setDataAndType(Uri.parse("file://" + files[position].toString()),"image/*");
                startActivityForResult(galleryIntent,5);

            }
        }
    };

}
