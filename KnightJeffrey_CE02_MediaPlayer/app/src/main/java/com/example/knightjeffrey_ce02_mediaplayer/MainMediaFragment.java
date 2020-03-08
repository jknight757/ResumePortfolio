// Jeffrey knight
// MDF3
// CE02 Media player
package com.example.knightjeffrey_ce02_mediaplayer;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import java.util.Objects;



public class MainMediaFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener,  Switch.OnCheckedChangeListener {

    // member variables
    private int playListPosition = 0;
    private final int playListSize = 4;
    private SeekBar mediaPlayTime;
    private TextView currentTime;
    private TextView songDuration;
    private Switch onRepeat;
    private final SeekReceiver receiver = new SeekReceiver();
    private PlaybackCommandListener commandListener = null;

    // intent actions and extras
    private static final String EXTRA_SONG_DURATION = "EXTRA_SONG_DURATION";
    private static final String EXTRA_PLAYLIST_POSITION = "EXTRA_PLAYLIST_POSITION";
    private static final String ACTION_SEND_SEEKBAR = "com.example.knightjeffrey_ce02_mediaplayer.action.UPDATE_SEEK";
    private static final String ACTION_SONG_COMPLETE = "com.example.knightjeffrey_ce02_mediaplayer.action.NEXT_SONG";
    private static final String ACTION_UPDATE_UI = "com.example.knightjeffrey_ce02_mediaplayer.action.UPDATE_UI";




    public MainMediaFragment() {
        // Required empty public constructor
    }

    public static MainMediaFragment newInstance() {
        
        Bundle args = new Bundle();
        
        MainMediaFragment fragment = new MainMediaFragment();
        fragment.setArguments(args);
        return fragment;
    }


    // interface used to invoke callback methods
    public interface PlaybackCommandListener{
        void previous(MediaItem song);
        void play(MediaItem song);
        void next(MediaItem song);
        void repeat(boolean repeat);
        void pause();
        void stop();
        void seekBarChanged(int progress);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_media, container, false);
    }

    // when fragment is attached, check that the main activity is an instance of the interface
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof  PlaybackCommandListener){
            commandListener = (PlaybackCommandListener) context;
        }
    }

    //when activity is created setup event listeners, get all views, and register receiver
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View root = getView();
        if(root != null) {
            root.findViewById(R.id.button_play).setOnClickListener(this);
            root.findViewById(R.id.button_stop).setOnClickListener(this);
            root.findViewById(R.id.button_pause).setOnClickListener(this);
            root.findViewById(R.id.button_previous).setOnClickListener(this);
            root.findViewById(R.id.button_next).setOnClickListener(this);
            currentTime = root.findViewById(R.id.current_time_lbl);
            songDuration = root.findViewById(R.id.song_duration_lbl);
            onRepeat = root.findViewById(R.id.repeat_switch);
            onRepeat.setOnCheckedChangeListener(this);
            mediaPlayTime = root.findViewById(R.id.song_duration_SB);
            mediaPlayTime.setOnSeekBarChangeListener(this);

            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_SEND_SEEKBAR);
            filter.addAction(ACTION_SONG_COMPLETE);
            filter.addAction(ACTION_UPDATE_UI);
            Objects.requireNonNull(getContext()).registerReceiver(receiver,filter);

        }


    }

    // when the user interacts with the seek bar, invoke call back method
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser) {
            Objects.requireNonNull(getContext()).unregisterReceiver(receiver);
            commandListener.seekBarChanged(progress * 1000);
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_SEND_SEEKBAR);
            filter.addAction(ACTION_SONG_COMPLETE);
            filter.addAction(ACTION_UPDATE_UI);
            getContext().registerReceiver(receiver,filter);
        }
    }

    // unused overrides
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {


    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    // on any button click invoke corresponding methods
    @Override
    public void onClick(View v) {
        MediaItem item;
        if(commandListener != null) {
            switch (v.getId()) {
                case R.id.button_previous:
                    if(playListPosition >0) {
                        playListPosition--;
                        item = queryPlaylist(playListPosition);
                        updateUIForSong(item);

                        commandListener.previous(item);
                    }
                    break;
                case R.id.button_play:
                     item = queryPlaylist(playListPosition);
                    updateUIForSong(item);

                    commandListener.play(item);
                    break;
                case R.id.button_next:
                    if(playListPosition < (playListSize - 1)) {
                        playListPosition++;
                        item = queryPlaylist(playListPosition);
                        updateUIForSong(item);
                        commandListener.next(item);
                    }
                    break;
                case R.id.button_pause:
                    commandListener.pause();
                    break;
                case R.id.button_stop:
                    commandListener.stop();
                    break;

            }
        }
    }
    // when the repeat switch is clicked invoke callback method passing switch state
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        commandListener.repeat(isChecked);
    }

    // return MediaItem that was queried from the database based on the passed position
    private MediaItem queryPlaylist(int position){
        DatabaseHelper dbh = DatabaseHelper.getInstance(getContext());
        Cursor c = dbh.getAllSongs();
        c.moveToPosition(position);
        int titleIndex = c.getColumnIndex(DatabaseHelper.COLUMN_TITLE);
        int mp3AssetIndex = c.getColumnIndex(DatabaseHelper.COLUMN_MP3_PATH);
        int imageIndex = c.getColumnIndex(DatabaseHelper.COLUMN_ALBUM_ART);
        int durationIndex = c.getColumnIndex(DatabaseHelper.COLUMN_SONG_DURATION);

        String title = c.getString(titleIndex);
        Uri mp3Asset = Uri.parse(c.getString(mp3AssetIndex));
        byte[] image = c.getBlob(imageIndex);
        Double duration = c.getDouble(durationIndex);
        return new MediaItem(title,mp3Asset,image,duration);

    }
    // update the UI when something changes, accepts mediaItem to populate views
    private void updateUIForSong(MediaItem item){
        if(item != null && getView() != null){
            ((TextView)getView().findViewById(R.id.title_lbl)).setText(item.getTitle());
            Bitmap bmp = BitmapFactory.decodeByteArray(item.getAlbumArt(),0,item.getAlbumArt().length);
            ((ImageView)getView().findViewById(R.id.album_art)).setImageBitmap(bmp);

            double duration = item.getDuration();
            int intDuration = (int)Math.round(duration / 1000);
            int mns = (int)(duration / 60000) % 60000;
            int scs = (int)(duration % 60000 / 1000);

            String songTime = String.format("%02d:%02d",   mns, scs);
            mediaPlayTime.setMax(intDuration);
            songDuration.setText(songTime);
            currentTime.setText("00:00");

        }

    }

    // receiver is used to receive broadcasts from AudioPlayBackService
    public class SeekReceiver extends BroadcastReceiver{

        // when broadcast is received determine which action invoked it respond accordingly
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!= null){

                MediaItem song;
                switch (Objects.requireNonNull(intent.getAction())){
                    case ACTION_SEND_SEEKBAR:
                        int currentPosition = intent.getIntExtra(EXTRA_SONG_DURATION, 0);
                        if (currentPosition != 0) {
                            mediaPlayTime.setProgress(currentPosition);
                            updateTime(currentPosition);
                        }
                        break;

                    case ACTION_SONG_COMPLETE:
                        if(!onRepeat.isChecked()){
                            if(playListPosition < (playListSize -1)){ playListPosition++; }
                            else{ playListPosition = 0;}
                        }
                        song = queryPlaylist(playListPosition);
                        updateUIForSong(song);
                        commandListener.next(song);
                        break;

                    case ACTION_UPDATE_UI:
                        int receivedPosition = intent.getIntExtra(EXTRA_PLAYLIST_POSITION,0);
                        song = queryPlaylist(receivedPosition);
                        playListPosition = receivedPosition;
                        updateUIForSong(song);
                        break;
                }

            }
        }

        // updates the displayed time with the current position the media player is at
        void updateTime(int currentPosition){
            String timeStr;
            int minutes = currentPosition/60;
            int seconds = currentPosition%60;
            timeStr = minutes + ":";
            if(seconds < 10){
                timeStr += "0"+seconds;
            }else{
                timeStr += seconds;
            }
            currentTime.setText(timeStr);
        }
    }



}
