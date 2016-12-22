package com.facebook.se.apac.example.liyuhk.didiadsa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    private DemoshopSession ds;
    private ArrayList<String> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ds = DemoshopSession.ExtractFromIntent(getIntent());
        categoryList = (ArrayList<String>)getIntent().getSerializableExtra("categoryList");

        setContentView(R.layout.activity_category);

        ListView listView = (ListView)findViewById(R.id.category_list);
        ArrayAdapter adapter =
                new ArrayAdapter<String>(this, R.layout.simple_list_item, categoryList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Intent i = new Intent(CategoryActivity.this, MainActivity.class);
                ds.selectedCategory = categoryList.get(position);
                ds.saveToIntent(i);
                startActivity(i);
            }
        });

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

}
