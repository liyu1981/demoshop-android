package com.facebook.se.apac.example.liyuhk.didiadsa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

public class CartActivity extends AppCompatActivity {

    private DemoshopSession ds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ds = DemoshopSession.ExtractFromIntent(getIntent());

        setContentView(R.layout.activity_cart);

        ListView listView = (ListView)findViewById(R.id.cart_list);
        LazyAdapter listAdapter = new LazyAdapter(this, ds.cart);
        listView.setAdapter(listAdapter);

        Button payButton = (Button)findViewById(R.id.pay);
        payButton.setText(String.format("Pay %.2f USD Now!", ds.calculateTotalPrice()));
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CartActivity.this, MainActivity.class);
                CartActivity.this.ds.payAll();
                CartActivity.this.ds.saveToIntent(i);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_buy_more) {
            Intent i = new Intent(this, MainActivity.class);
            this.ds.saveToIntent(i);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
