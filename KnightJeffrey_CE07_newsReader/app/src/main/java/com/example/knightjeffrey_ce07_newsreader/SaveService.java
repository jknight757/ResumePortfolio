//Jeffrey Knight
// Java 2 1911
//CE07
package com.example.knightjeffrey_ce07_newsreader;

import android.app.IntentService;
import android.content.Intent;
import androidx.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SaveService extends IntentService {

    public static final String ACTION_SAVE = "com.example.knightjeffrey_ce07_newsreader.action.SAVE";
    public static final String ACTION_SEND_MSG = "com.example.knightjeffrey_ce07_newsreader.action.MSG";
    public static final String EXTRA_ARTICLE = "com.example.knightjeffrey_ce07_newsreader.extra.ARTICLE";
    private static File folderPath;
    public static final String FOLDER = "article";

    public SaveService() {
        super("SaveService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent != null){
            String action = intent.getAction();

            if(action!= null && action.equals(ACTION_SAVE)){
                if(intent.hasExtra(EXTRA_ARTICLE)){

                    Article article = (Article) intent.getSerializableExtra(EXTRA_ARTICLE);
                    folderPath = getExternalFilesDir(FOLDER);
                    File newFile = createFile();

                    try{
                        FileOutputStream fos = new FileOutputStream(newFile);
                        ObjectOutputStream oos = new ObjectOutputStream(fos);
                        oos.writeObject(article);
                        oos.close();
                        fos.close();
                        Intent broadCastIntent= new Intent(ACTION_SEND_MSG);
                        sendBroadcast(broadCastIntent);

                    }catch (IOException e){
                        e.printStackTrace();
                    }


                }

            }
        }

    }
    private static File generateFile(){
        String fileName = "article_";
        fileName += ""+ System.currentTimeMillis();
        return  new File(folderPath, fileName);
    }

    // create a file path for the new image to be saved to by calling getFileReference()
    private static File createFile(){
        File newFile = generateFile();
        try{
             newFile.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }
        return newFile;
    }
}
