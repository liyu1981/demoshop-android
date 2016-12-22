package com.facebook.se.apac.example.liyuhk.didiads;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;

public class DetailActivity extends AppCompatActivity {

    private DemoshopSession ds;
    private AppEventsLogger fblogger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ds = DemoshopSession.ExtractFromIntent(getIntent());

        setContentView(R.layout.activity_detail);

        final String productID = ds.selectedProduct.get(MainActivity.FEED_TAG_ENTRY_ID);
        final float productPrice = DemoshopSession.calculateProductPrice(ds.selectedProduct);

        ImageView imageView = (ImageView)findViewById(R.id.product_image);
        TextView titleView = (TextView)findViewById(R.id.product_title);
        TextView descriptionView = (TextView)findViewById(R.id.product_description);
        TextView priceView = (TextView)findViewById(R.id.product_price);
        TextView availiabilityView = (TextView)findViewById(R.id.product_avaliability);
        Button buyButton = (Button)findViewById(R.id.buy_buy_buy);

        ImageLoader imageLoader = new ImageLoader(getApplicationContext(),
                R.drawable.placeholder_pushee, MainActivity.FILE_CACHE_DIR);
        imageLoader.DisplayImage(
                ds.selectedProduct.get(MainActivity.FEED_TAG_ENTRY_IMAGE_LINK), imageView);

        titleView.setText(ds.selectedProduct.get(MainActivity.FEED_TAG_ENTRY_TITLE));
        descriptionView.setText(ds.selectedProduct.get(MainActivity.FEED_TAG_ENTRY_DESCRIPTION));
        priceView.setText(ds.selectedProduct.get(MainActivity.FEED_TAG_ENTRY_PRICE));
        availiabilityView.setText(ds.selectedProduct.get(MainActivity.FEED_TAG_ENTRY_AVALIABILITY));

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailActivity.this, CartActivity.class);
                DetailActivity.this.ds.addSelectedProductToCart();

                Bundle fbparams = new Bundle();
                fbparams.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, productID);
                fbparams.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "product");
                fbparams.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "USD");
                DetailActivity.this.fblogger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART,
                        productPrice,
                        fbparams);

                DetailActivity.this.ds.saveToIntent(i);
                startActivity(i);
            }
        });

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        fblogger = AppEventsLogger.newLogger(this);

        Bundle fbparams = new Bundle();
        fbparams.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, productID);
        fbparams.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "product");
        fbparams.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "USD");
        fblogger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT,
                productPrice,
                fbparams);
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
            this.ds.clearSelectedProduct();
            this.ds.saveToIntent(i);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
