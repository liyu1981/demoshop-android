package com.facebook.se.apac.example.liyuhk.didiadsa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class DetailActivity extends AppCompatActivity {

    private DemoshopSession ds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ds = DemoshopSession.ExtractFromIntent(getIntent());

        setContentView(R.layout.activity_detail);

        ImageView imageView = (ImageView)findViewById(R.id.product_image);
        TextView titleView = (TextView)findViewById(R.id.product_title);
        TextView descriptionView = (TextView)findViewById(R.id.product_description);
        TextView priceView = (TextView)findViewById(R.id.product_price);
        TextView availiabilityView = (TextView)findViewById(R.id.product_avaliability);
        Button buyButton = (Button)findViewById(R.id.buy_buy_buy);

        ImageLoader imageLoader =
                new ImageLoader(getApplicationContext(), R.drawable.placeholder_pushee);
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
                DetailActivity.this.ds.saveToIntent(i);
                startActivity(i);
            }
        });
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
