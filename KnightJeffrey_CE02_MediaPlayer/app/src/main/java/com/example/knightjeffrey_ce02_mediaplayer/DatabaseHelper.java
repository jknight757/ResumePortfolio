// Jeffrey knight
// MDF3
// CE02 Media player
package com.example.knightjeffrey_ce02_mediaplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_FILE = "database.db";
    private static final int DATABASE_VERSION = 1;

    // contract keys, used to specify which data to pull or push
    private static final String TABLE_NAME = "articles";
    private static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_MP3_PATH = "description";
    public static final String COLUMN_ALBUM_ART = "album_art";
    public static final String COLUMN_SONG_DURATION = "duration";

    // using SQL syntax to create a string that will be passed to create a database
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME +
            " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TITLE + " TEXT, " +
            COLUMN_MP3_PATH + " TEXT, " +
            COLUMN_ALBUM_ART + " BLOB, "+
            COLUMN_SONG_DURATION + " DOUBLE " +
            ")";


    ////// Standard for all DataBase helper classes ////////
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //Singleton style instance
    private static DatabaseHelper mInstance = null;

    public static DatabaseHelper getInstance(Context context){

        if(mInstance == null){
            mInstance = new DatabaseHelper(context);
        }
        return mInstance;
    }

    private final SQLiteDatabase mDatabase;

    private DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_FILE, null, DATABASE_VERSION);

        mDatabase = getWritableDatabase();
    }
////// Standard for all DataBase helper classes ////////




    public void insertSong(MediaItem mediaItem){
        //Creates a dictionary using the keys we created and the values passed in the parameters
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, mediaItem.getTitle());
        cv.put(COLUMN_MP3_PATH, mediaItem.getMp3Asset().toString());
        cv.put(COLUMN_ALBUM_ART, mediaItem.getAlbumArt());
        cv.put(COLUMN_SONG_DURATION, mediaItem.getDuration());
        mDatabase.insert(TABLE_NAME, null, cv);
    }

    // gets all the data from the database
    public Cursor getAllSongs(){
        return mDatabase.query(TABLE_NAME,
                null,null,
                null,null,
                null,null);
    }


}
