package com.facebook.se.apac.example.liyuhk.didiads;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class FBEActivity extends AppCompatActivity {

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
    }
}
