//jeffrey Knight
// java 2 1911
//CE03
package com.example.knightjeffrey_ce03_java2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.knightjeffrey_ce03_java2.fragments.ImageGridFragment;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements ImageGridFragment.ImageListener{

    private static final int REQUEST_PICTURE = 0x001;
    private static final String FOLDER_NAME = "images";
    public static Boolean sentFromAppB = false;
    private static final String AUTHORITY = "com.example.knightjeffrey_ce03_java2.fileprovider";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(getIntent() != null){
            String startignIntent = getIntent().getStringExtra("APPB");
            try{
                if (Objects.requireNonNull(startignIntent).equals("APPB")) {
                    Log.i("Here", "onCreate: ");
                    sentFromAppB = true;
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }

        }

        // Get files names that are stored in the folder
        //fileNames = new ArrayList<>();
        File folderPath = getExternalFilesDir(FOLDER_NAME);
        if(Objects.requireNonNull(Objects.requireNonNull(folderPath).list()).length != 0 ){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, ImageGridFragment.newInstance(folderPath.listFiles())).commit();
        }
        // attach fragment



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.camera_menu, menu);
        return true;
    }

    //
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.action_take_picture){

            File fileRef = createFile();

            Uri uri = FileProvider.getUriForFile(this,AUTHORITY,fileRef);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent,REQUEST_PICTURE);


        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        File folderPath = getExternalFilesDir(FOLDER_NAME);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, ImageGridFragment.newInstance(Objects.requireNonNull(folderPath).listFiles())).commit();


    }

    //
    private File getFileReference(){
        File folderPath = getExternalFilesDir(FOLDER_NAME);
        return new File(folderPath,generateFileName());
    }

    //gets the
    private String generateFileName(){
        String fileName = "image_taken_";
        fileName += ""+ System.currentTimeMillis();
        return fileName;
    }

    // create a file path for the new image to be saved to by calling getFileReference()
    private File createFile(){
        File newFile = getFileReference();
        try{
            boolean wasCreated = newFile.createNewFile();
            Log.i("IMAGE TAG", "Image file was freshly created " + wasCreated);
        }catch (IOException e){
            e.printStackTrace();
        }
        return newFile;
    }

    @Override
    public void sendImage(File file) {
        Log.i("Made it", "sendImage: ");
        Intent returnIntent = new Intent(this,MainActivity.class);
        Uri uri = FileProvider.getUriForFile(this,AUTHORITY,file);
        returnIntent.setData(uri);
        returnIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        setResult(RESULT_OK, returnIntent);
        finish();

    }

}
