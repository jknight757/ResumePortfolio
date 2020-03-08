// Jeffrey knight
// MDF3
// CE02 Media player
package com.example.knightjeffrey_ce02_mediaplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import java.io.IOException;
import java.util.Objects;

public class AudioPlayBackService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    // member variables
    private MediaPlayer player;
    private int playlistPosition = 0;
    private int firstBuild = 0;

    // state constants represent what stage the media player is in
    private static final int STATE_IDLE = 0;
    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_PREPARING = 2;
    private static final int STATE_PREPARED = 3;
    private static final int STATE_STARTED = 4;
    private static final int STATE_PAUSED = 5;
    private static final int STATE_STOPPED = 6;
    private static final int STATE_PLAYBACK_COMPLETED = 7;
    private static final int STATE_END = 8;

    private int state = STATE_IDLE;
    private Handler mHandler;

    // from noti is a boolean that represents whether this service has been started via notification
    private boolean fromNoti = false;
    private MediaItem currentSong;

    // notification extra variables
    private static final int NOTIFICATION_ID = 0x0011;
    private static final String CHANNEL_ID = "AUDIO_CHANNEL";
    private static final String CHANNEL_NAME = "Audio Channel";
    private static final String EXTRA_SONG_TITLE = "EXTRA_SONG_TITLE";
    private static final String EXTRA_SONG_PATH = "EXTRA_SONG_PATH";
    private static final String EXTRA_SONG_IMAGE = "EXTRA_SONG_IMAGE";
    private static final String EXTRA_SONG_DURATION = "EXTRA_SONG_DURATION";
    private static final String EXTRA_PLAYLIST_POSITION = "EXTRA_PLAYLIST_POSITION";

    // notification action variables
    private static final String ACTION_PLAY = "com.example.knightjeffrey_ce02_mediaplayer.action.PLAY";
    private static final String ACTION_PAUSE = "com.example.knightjeffrey_ce02_mediaplayer.action.PAUSE";
    private static final String ACTION_NEXT = "com.example.knightjeffrey_ce02_mediaplayer.action.NEXT";
    private static final String ACTION_PREVIOUS = "com.example.knightjeffrey_ce02_mediaplayer.action.PREVIOUS";
    private static final String ACTION_SEND_SEEKBAR = "com.example.knightjeffrey_ce02_mediaplayer.action.UPDATE_SEEK";
    private static final String ACTION_SONG_COMPLETE = "com.example.knightjeffrey_ce02_mediaplayer.action.NEXT_SONG";
    private static final String ACTION_UPDATE_UI = "com.example.knightjeffrey_ce02_mediaplayer.action.UPDATE_UI";




    public AudioPlayBackService() {
    }

    //when the service is created:
    //build the notification channel
    //start player and save state
    //set event listeners
    @Override
    public void onCreate() {
        super.onCreate();
        buildNotificationChannel();

        player = new MediaPlayer();
        state = STATE_IDLE;

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
    }

    // this method is called when the service is first created as well as any time the service is..
    // ..started and is already created.
    // if the service is created via intent with an action, it is being invoked by the notification.
    // ..action buttons
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            if(intent.getAction() != null) {
                MediaItem song;
                // started from notification so set to true
                fromNoti = true;
                switch (intent.getAction()) {
                    case ACTION_PLAY:
                         song = queryPlaylist(playlistPosition);
                         play(song);
                        break;
                    case ACTION_PAUSE:
                        pause();
                        break;
                    case ACTION_NEXT:
                        if(playlistPosition < 3) {
                            playlistPosition++;
                            song = queryPlaylist(playlistPosition);
                            next(song);
                        }
                        break;
                    case ACTION_PREVIOUS:
                        if(playlistPosition > 0) {
                            playlistPosition--;
                            song = queryPlaylist(playlistPosition);
                            previous(song);
                        }
                        break;
                }
            }
        }

        // if play is pressed update Notification to display pause button
        // if pause is pressed update Notification to display play button
        if(Objects.requireNonNull(intent).getAction() != null) {
            if (!intent.getAction().equals(ACTION_NEXT) && !intent.getAction().equals(ACTION_PREVIOUS)) {
                Notification musicNotification = buildNotification();
                startForeground(NOTIFICATION_ID, musicNotification);
            }
        }
        // since onstartCommand also is invoked when the service is first created(not from a notification),
        // we have to build the notification to accurately display information
        if(firstBuild == 0){
            buildOneTime();
            firstBuild++;
        }

        // if the application is open, update the UI from the notification change
        if(MainActivity.isActive && fromNoti){
            Intent broadcastIntent = new Intent(ACTION_UPDATE_UI);
            broadcastIntent.putExtra(EXTRA_PLAYLIST_POSITION, playlistPosition);
            sendBroadcast(broadcastIntent);
        }
        // the service has completed updates from notification, set to false
        fromNoti = false;
        return START_NOT_STICKY;
    }
    // builds the notification the one time
    private void buildOneTime(){
        Notification musicNotification = buildNotification();
        startForeground(NOTIFICATION_ID, musicNotification);
    }

    // when service is destroyed release the player and set player state
    @Override
    public void onDestroy() {
        super.onDestroy();

        if(player != null){
            player.release();
            player = null;
        }

        state = STATE_END;
    }

    // when the current media item has completed playing, check if the player is set to looping
    // if not looping than broadcast message to main fragment to query next song
    @Override
    public void onCompletion(MediaPlayer mp) {
        if(!mp.isLooping()){
            Intent broadcastIntent = new Intent(ACTION_SONG_COMPLETE);
            sendBroadcast(broadcastIntent);
            if(playlistPosition < 3){
                playlistPosition++;
            }
        }

    }

    // returns current service
    public class AudioServiceBinder extends Binder{
        public AudioPlayBackService getService(){
            return AudioPlayBackService.this;
        }
    }

    // returns service binder
    @Override
    public IBinder onBind(Intent intent) {
        return new AudioServiceBinder();
    }

    // when the player is prepared set the state start the player then update state again
    @Override
    public void onPrepared(MediaPlayer mp) {
        state = STATE_PREPARED;
        player.start();
        state = STATE_STARTED;

    }


    // this method takes a mediaItem checks the players state and plays song
    public void play(MediaItem song){
        if(state == STATE_PAUSED){
            player.start();
            state = STATE_STARTED;

        }
        else if(state != STATE_STARTED && state != STATE_PREPARING ) {

            player.reset();
            state = STATE_IDLE;

            try {

                player.setDataSource(this, song.getMp3Asset());
                currentSong = song;

                state = STATE_INITIALIZED;

                // this runnable is used to provide constant updates of the position in the song
                mHandler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        int currentPosition = player.getCurrentPosition() / 1000;
                        Intent broadcastIntent = new Intent(ACTION_SEND_SEEKBAR);
                        broadcastIntent.putExtra(EXTRA_SONG_DURATION, currentPosition);
                        sendBroadcast(broadcastIntent);
                        mHandler.postDelayed(this, 1000);
                    }

                };
                runOnIUThread(runnable);



            } catch (IOException e) {
                e.printStackTrace();
            }

            if (state == STATE_INITIALIZED) {
                player.prepareAsync();
                state = STATE_PREPARING;
            }

            if(state == STATE_PREPARING || state == STATE_PAUSED){
                Notification musicNotification = buildNotification();
                startForeground(NOTIFICATION_ID,musicNotification);
            }
        }
    }

    //this method checks the player state and pauses player
    public void pause(){
        if(state == STATE_STARTED){
            player.pause();
            state = STATE_PAUSED;
            Notification musicNotification = buildNotification();
            startForeground(NOTIFICATION_ID,musicNotification);
        }

    }

    //this method checks the player state and stops player
    public void stop(){
        switch (state){
            case STATE_STARTED:
            case STATE_PAUSED:
            case STATE_PLAYBACK_COMPLETED:
            case STATE_PREPARED:{
                player.stop();
                state = STATE_STOPPED;
                stopForeground(true);
            }

        }
    }
    // this method accepts a indexed mediaItem and plays it
    public void previous(MediaItem song){

        switch(state){
            case STATE_PREPARED:
            case STATE_PAUSED:
            case STATE_STARTED:
            case STATE_STOPPED:{
                player.reset();
                try {

                    player.setDataSource(this, song.getMp3Asset());
                    currentSong = song;
                    state = STATE_INITIALIZED;
                    if(!fromNoti){
                        playlistPosition--;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if (state == STATE_INITIALIZED) {
                player.prepareAsync();
                state = STATE_PREPARING;
            }

            if(state == STATE_PREPARING){
                Notification musicNotification = buildNotification();
                startForeground(NOTIFICATION_ID,musicNotification);
            }




        }
    }
    // this method accepts a indexed mediaItem and plays it
    public void next(MediaItem song){

        switch(state){
            case STATE_PREPARED:
            case STATE_PAUSED:
            case STATE_STARTED:
            case STATE_STOPPED:{
                player.reset();
                try {

                    player.setDataSource(this,song.getMp3Asset());
                    currentSong = song;
                    state = STATE_INITIALIZED;
                    if(!fromNoti){
                        playlistPosition++;
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if (state == STATE_INITIALIZED) {
                player.prepareAsync();
                state = STATE_PREPARING;
            }

            if(state == STATE_PREPARING){
                Notification musicNotification = buildNotification();
                startForeground(NOTIFICATION_ID,musicNotification);
            }


        }
    }
    // this method takes the state of the repeat switch and sets player looping to that state
    public void repeat(boolean isChecked){
        switch(state){
            case STATE_PREPARED:
            case STATE_PAUSED:
            case STATE_STARTED:
            case STATE_STOPPED:{
                player.setLooping(isChecked);

            }

        }

    }

    // when the user interacts with the seek bar this method is invoked to update the player
    public void seekBarChange(int position){
        switch(state){
            case STATE_PREPARED:
            case STATE_PAUSED:
            case STATE_STARTED:
            case STATE_STOPPED:{
                player.seekTo(position);

            }



        }
    }

    /// Notification methods ///

    private void buildNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager mgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

            if( mgr != null){
                mgr.createNotificationChannel(channel);
            }
        }
    }

    // returns built notification with necassary actions and content
    private Notification buildNotification(){


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setContentTitle(currentSong.getTitle());


        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
        style.setBigContentTitle(currentSong.getTitle());
        Bitmap bmp = BitmapFactory.decodeByteArray(currentSong.getAlbumArt(),0,currentSong.getAlbumArt().length);
        style.bigPicture(bmp);
        builder.setStyle(style);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_SONG_TITLE,currentSong.getTitle());
        intent.putExtra(EXTRA_SONG_PATH,currentSong.getMp3Asset().toString());
        intent.putExtra(EXTRA_SONG_IMAGE, currentSong.getAlbumArt());
        intent.putExtra(EXTRA_SONG_DURATION, currentSong.getDuration());
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        builder.addAction(android.R.drawable.ic_media_previous, "Previous",getPendingIntent(3));
        if(state == STATE_STARTED || state == STATE_PREPARING || state == STATE_PREPARED){
            builder.addAction(android.R.drawable.ic_media_pause, "Pause",getPendingIntent(2));
        }else{
            builder.addAction(android.R.drawable.ic_media_play, "Play",getPendingIntent(1));
        }
        builder.addAction(android.R.drawable.ic_media_next, "Next",getPendingIntent(4));

        return builder.build();
    }


    // returns a pending intent based on the passed id
    private PendingIntent getPendingIntent(int callBackId){
        Intent action;
        PendingIntent pendingIntent;
        final ComponentName serviceName = new ComponentName(this,AudioPlayBackService.class);

        switch (callBackId){
            case 1:
                action = new Intent(ACTION_PLAY);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this,1,action,0);
                return pendingIntent;
            case 2:
                action = new Intent(ACTION_PAUSE);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this,2,action,0);
                return pendingIntent;
            case 3:
                action = new Intent(ACTION_PREVIOUS);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this,3,action,0);
                return pendingIntent;
            case 4:
                action = new Intent(ACTION_NEXT);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this,4,action,0);
                return pendingIntent;
                default:
                    break;
        }
        return null;
    }

    /// Notification methods ///

    private void runOnIUThread(Runnable run){
        mHandler.post(run);
    }

    // returns mediaItem with the passed int
    private MediaItem queryPlaylist(int position){
        DatabaseHelper dbh = DatabaseHelper.getInstance(this);
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

}
