//Jeffrey Knight
// Java 2 1911
//CE07
package com.example.knightjeffrey_ce07_newsreader;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;


class DownloadService extends Worker {

    private static final String CHANNEL_ID_MAIN = "MAIN_ID";
    private static final int STANDARD_NOTIFICATION = 0x10000000;
    private ArrayList<Article> articles;
    private final Context mContext = getApplicationContext();

    public DownloadService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        articles = new ArrayList<>();

        String webAdrress ="https://www.reddit.com/r/xbox/hot.json";

        String result = "Result Error";
        HttpURLConnection connection = null;
        InputStream is = null;
        URL url;

        // open connection to specified URL
        try{
            url = new URL(webAdrress);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

        }catch(Exception e){
            e.printStackTrace();
        }
        // get the input stream from the opened connection
        try{
            is = Objects.requireNonNull(connection).getInputStream();
            result = IOUtils.toString(is, "UTF-8");

        }catch (Exception e){
            e.printStackTrace();
        }

        // close connection
        finally {
            if(connection != null){
                if(is!= null){
                    try{
                        is.close();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }

        // parse retrieved JSON String
        try{
            String title;
            String imgUrl;
            String body;
            String webUrl;

            JSONObject obj = new JSONObject(result);
            JSONObject nestedObj = obj.getJSONObject("data");
            JSONArray jArray = nestedObj.getJSONArray("children");

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject secondNestedObj = jArray.getJSONObject(i);
                JSONObject thirdNestedObj = secondNestedObj.getJSONObject("data");
                title = thirdNestedObj.getString("title");
                imgUrl = thirdNestedObj.getString("thumbnail");
                body = thirdNestedObj.getString("selftext");
                webUrl = thirdNestedObj.getString("url");
                articles.add(new Article(title,imgUrl, webUrl));
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        sendNotification();


        return Result.success();
    }

    private void sendNotification(){

        // check build version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //create a channel for a notification
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_MAIN, "Main Channel", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel Description");

            NotificationManager mgr = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            if(mgr != null){
                mgr.createNotificationChannel(channel);
            }
        }

        //article variables
        Article myArticle = articles.get(0);
        String title = myArticle.getTitle();
        String webUrl = myArticle.getWebUrl();
        String imgUrl = myArticle.getImgUrl();
        byte[] byteImage;
        Bitmap image = null;
        try{
            URL url = new URL(imgUrl);
            byteImage = IOUtils.toByteArray(url);
            myArticle.setImage(byteImage);
            image = BitmapFactory.decodeByteArray(byteImage,0,byteImage.length);
        }catch (Exception e){
            e.printStackTrace();
        }

        // Create notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext,CHANNEL_ID_MAIN);
        builder.setContentTitle(title);
        builder.setSmallIcon(R.drawable.ic_library_books_white_24dp);
        builder.setContentText(webUrl);

        if(image != null){
            builder.setLargeIcon(image);
        }
        builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image));

        // Create pending intent to attach to notification
        Intent webIntent = new Intent(Intent.ACTION_VIEW);
        webIntent.setData(Uri.parse(webUrl));
        PendingIntent webPI = PendingIntent.getActivity(mContext,0,webIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(webPI);

        Intent saveIntent = new Intent(mContext,SaveService.class);
        saveIntent.setAction(SaveService.ACTION_SAVE);
        saveIntent.putExtra(SaveService.EXTRA_ARTICLE,myArticle);
        PendingIntent savePI =  PendingIntent.getService(mContext,1,saveIntent,PendingIntent.FLAG_UPDATE_CURRENT);



        NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.ic_save_white_24dp, "Save",savePI);
        builder.addAction(action);
        NotificationManager mgr = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if(mgr != null){
            mgr.notify(STANDARD_NOTIFICATION ,builder.build());
        }



    }
}
