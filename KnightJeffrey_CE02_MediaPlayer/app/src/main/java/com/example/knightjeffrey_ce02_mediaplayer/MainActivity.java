// Jeffrey knight
// MDF3
// CE02 Media player
package com.example.knightjeffrey_ce02_mediaplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ServiceConnection,  MainMediaFragment.PlaybackCommandListener {

    // member variables
    private AudioPlayBackService mAudioService = null;
    public static boolean isActive = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Media Player");
        // populate the song database
        populateSongDatabase();

        // load the main media fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, MainMediaFragment.newInstance()).commit();
    }

    // if the current database is empty then add some new songs
    private void populateSongDatabase(){
        DatabaseHelper dbh = DatabaseHelper.getInstance(this);
        Cursor c= dbh.getAllSongs();

        ArrayList<Uri> songUris = new ArrayList<>();
        songUris.add(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drifter_hippiesabatage));
        songUris.add(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.mai_tai_jeff));
        songUris.add(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.barbeque_music));
        songUris.add(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.not_the_king));
        if(c.getCount() <= 0){
            for (Uri song: songUris) {
                MediaItem item = getMetaData(song);
                if (item.getTitle() == null) {
                    item.setTitle("No title Found");
                }
                if (item.getAlbumArt() == null) {
                    Uri defaultUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.default_music_icon);
                    byte[] byteArray = null;
                    try{
                        Bitmap bmp = MediaStore.Images.Media.getBitmap( getContentResolver(), defaultUri);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byteArray = stream.toByteArray();
                        bmp.recycle();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    item.setAlbumArt(byteArray);
                }

                dbh.insertSong(item);
            }
        }

    }

    // get the meta data for a passed song
    private MediaItem getMetaData(Uri uri){
        MediaItem item = null;
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(this, uri);

        try {
            byte[] art = metaRetriever.getEmbeddedPicture();

            String songName =metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String artist = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            item = new MediaItem(songName + ", "+ artist,uri,art,Double.parseDouble(duration));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }
    // when the activity is started bind the service and set isActive to true
    @Override
    protected void onStart() {
        super.onStart();
        // bind the service via intent
        Intent serviceIntent  = new Intent(this, AudioPlayBackService.class);
        bindService(serviceIntent,this, BIND_AUTO_CREATE);
        isActive = true;
    }

    // when the activity is stopped unbind the service, set the audioservice to null, and set isActive to false
    @Override
    protected void onStop() {
        super.onStop();
        unbindService(this);
        mAudioService = null;
        isActive = false;
    }

    ////// Fragment Interface Methods ///////

    // when the previous button is pressed from the fragment this callback method is invoked
    @Override
    public void previous(MediaItem song) {
        if(mAudioService != null){
            mAudioService.previous(song);
        }

    }

    // when the play button is pressed from the fragment this callback method is invoked
    @Override
    public void play(MediaItem song) {
        if(mAudioService != null){

            mAudioService.play(song);

            Intent intent = new Intent(this,AudioPlayBackService.class);
            startService(intent);


        }
    }

    // when the next button is pressed from the fragment this callback method is invoked
    @Override
    public void next(MediaItem song) {
        if(mAudioService != null){
            mAudioService.next(song);
        }
    }

    // when the repeat switch is pressed from the fragment this callback method is invoked
    @Override
    public void repeat(boolean isChecked) {
        if(mAudioService != null){
            mAudioService.repeat(isChecked);
        }
    }

    // when the pause button is pressed from the fragment this callback method is invoked
    @Override
    public void pause() {
        if(mAudioService != null){
            mAudioService.pause();
        }
    }

    // when the stop button is pressed from the fragment this callback method is invoked
    @Override
    public void stop() {
        if(mAudioService != null){
            mAudioService.stop();

            Intent intent = new Intent(this, AudioPlayBackService.class);
            stopService(intent);
        }

    }

    // when the user interacts with the seekbar in the fragment this callback method is invoked
    @Override
    public void seekBarChanged(int progress) {
        if(mAudioService != null){
            mAudioService.seekBarChange(progress);
        }
    }
    ////// Fragment Interface Methods ///////


    // when the service is bound to this activity this method is called
    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        AudioPlayBackService.AudioServiceBinder audioBinder = (AudioPlayBackService.AudioServiceBinder) binder;
        if(audioBinder != null){
            mAudioService = audioBinder.getService();
        }
    }

    // when the service is unbound from this activity this method is called
    @Override
    public void onServiceDisconnected(ComponentName name) {
        mAudioService = null;
    }



}
