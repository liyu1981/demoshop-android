package com.facebook.se.apac.example.liyuhk.didiads;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;

public class CartActivity extends AppCompatActivity {

    private DemoshopSession ds;
    private AppEventsLogger fblogger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ds = DemoshopSession.ExtractFromIntent(getIntent());

        setContentView(R.layout.activity_cart);

        final String cartContentIDsJSONStr = ds.calculateCartContentIDsJSONStr();
        final float cartTotalPrice = ds.calculateTotalPrice();

        ListView listView = (ListView)findViewById(R.id.cart_list);
        LazyAdapter listAdapter = new LazyAdapter(this, ds.cart, MainActivity.FILE_CACHE_DIR);
        listView.setAdapter(listAdapter);

        Button payButton = (Button)findViewById(R.id.pay);
        payButton.setText(String.format("Pay %.2f USD Now!", cartTotalPrice));
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CartActivity.this, MainActivity.class);
                CartActivity.this.ds.payAll();
                CartActivity.this.ds.saveToIntent(i);

                Bundle fbparams = new Bundle();
                fbparams.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID,
                        cartContentIDsJSONStr);
                fbparams.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "USD");
                fbparams.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "product");
                CartActivity.this.fblogger.logEvent(AppEventsConstants.EVENT_NAME_PURCHASED,
                        cartTotalPrice,
                        fbparams);

                startActivity(i);
            }
        });

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        fblogger = AppEventsLogger.newLogger(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_buy_more) {
            Intent i = new Intent(this, MainActivity.class);
            this.ds.saveToIntent(i);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
