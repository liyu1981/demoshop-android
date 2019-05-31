package com.facebook.se.apac.example.liyuhk.didiads;

import android.app.PendingIntent;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class FBEActivity extends AppCompatActivity {
    private PendingIntent pendingIntent;
    private AlarmManager manager;

    public void startAlarm(View view) {
        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        int interval = 1000;

        manager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                interval,
                pendingIntent
        );
    }

    public void cancelAlarm(View view) {
        if (manager != null) {
            manager.cancel(pendingIntent);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fbe);

        Button launchButton = (Button)findViewById(R.id.action_launch_fbe);
        final FBEActivity self = this;

        launchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(self, FBEWebViewActivity.class);
                startActivity(i);
            }
        });

        // Retrieve a PendingIntent that will perform a broadcast
        Intent alarmIntent = new Intent(this, FBEWebhookReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
    }
}
