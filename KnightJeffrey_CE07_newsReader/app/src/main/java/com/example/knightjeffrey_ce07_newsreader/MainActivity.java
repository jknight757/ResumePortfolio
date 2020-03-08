//Jeffrey Knight
// Java 2 1911
//CE07
package com.example.knightjeffrey_ce07_newsreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private final MsgReceiver receiver = new MsgReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.list_container, ArticleListFragment.newInstance()).commit();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest request = new PeriodicWorkRequest
                .Builder(DownloadService.class, PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(this).enqueue(request);

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SaveService.ACTION_SEND_MSG);
        registerReceiver(receiver,filter);
    }

    class MsgReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.list_container,ArticleListFragment.newInstance()).commit();
        }
    }
}
