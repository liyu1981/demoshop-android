package com.facebook.se.apac.example.liyuhk.didiads;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.applinks.AppLinkData;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

class XMLParser {
    String getXmlFromUrl(String url) {
        String xml = null;

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            xml = EntityUtils.toString(httpEntity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return xml;
    }

    Document getDomElement(String xml) {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = db.parse(is);
        } catch (ParserConfigurationException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (SAXException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        }
        return doc;
    }

    private String getElementValue(Node elem) {
        Node child;
        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (child = elem.getFirstChild(); child != null; child = child.getNextSibling()) {
                    if (child.getNodeType() == Node.TEXT_NODE) {
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }

    String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return this.getElementValue(n.item(0));
    }
}

class RetrieveFeedTask extends AsyncTask<String, Void, ArrayList> {

    ArrayList<HashMap<String, String>> productList = new ArrayList<>();
    LazyAdapter adapter;
    ListView view;

    void setListViewAndAdapter(ListView v, LazyAdapter a) {
        view = v;
        adapter = a;
    }

    protected ArrayList doInBackground(String... urls) {
        ArrayList<HashMap<String, String>> productList = new ArrayList<>();
        XMLParser parser = new XMLParser();
        String xml = parser.getXmlFromUrl(urls[0]);
        Document doc = parser.getDomElement(xml);

        NodeList products = doc.getElementsByTagName(MainActivity.FEED_TAG_ENTRY);
        for (int i=0; i<products.getLength(); i++) {
            HashMap<String, String> map = new HashMap<>();
            Element e = (Element)products.item(i);
            map.put("_id", String.valueOf(i));
            map.put(MainActivity.FEED_TAG_ENTRY_ID,
                    parser.getValue(e, MainActivity.FEED_TAG_ENTRY_ID));
            map.put(MainActivity.FEED_TAG_ENTRY_TITLE,
                    parser.getValue(e, MainActivity.FEED_TAG_ENTRY_TITLE));
            map.put(MainActivity.FEED_TAG_ENTRY_IMAGE_LINK,
                    parser.getValue(e, MainActivity.FEED_TAG_ENTRY_IMAGE_LINK));
            map.put(MainActivity.FEED_TAG_ENTRY_DESCRIPTION,
                    parser.getValue(e, MainActivity.FEED_TAG_ENTRY_DESCRIPTION));
            map.put(MainActivity.FEED_TAG_ENTRY_PRICE,
                    parser.getValue(e, MainActivity.FEED_TAG_ENTRY_PRICE));
            map.put(MainActivity.FEED_TAG_ENTRY_AVALIABILITY,
                    parser.getValue(e, MainActivity.FEED_TAG_ENTRY_AVALIABILITY));
            productList.add(map);
        }

        return productList;
    }

    protected void onPostExecute(ArrayList pl) {
        this.productList.clear();
        this.productList.addAll(pl);
        this.adapter.notifyDataSetChanged();
        this.view.invalidateViews();
        this.view.refreshDrawableState();
    }
}

class LazyAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private ImageLoader imageLoader;
    private ArrayList<HashMap<String, String>> data;

    LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d, String fileCacheDirName) {
        data = d;
        inflater = (LayoutInflater)(a.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        imageLoader = new ImageLoader(a.getApplicationContext(),
                R.drawable.placeholder_pushee, fileCacheDirName);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        HashMap<String, String> p = data.get(position);
        return Long.parseLong(p.get("_id"));
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.list_item, null);
        }

        TextView title = (TextView) vi.findViewById(R.id.title);
        TextView description = (TextView) vi.findViewById(R.id.description);
        ImageView thumb = (ImageView) vi.findViewById(R.id.list_image);

        HashMap<String, String> product = data.get(position);

        String price = product.get(MainActivity.FEED_TAG_ENTRY_PRICE);
        String avaliability = product.get(MainActivity.FEED_TAG_ENTRY_AVALIABILITY);
        String d = String.format("Price: %s, Avaliability: %s", price, avaliability);

        title.setText(product.get(MainActivity.FEED_TAG_ENTRY_TITLE));
        description.setText(d);
        imageLoader.DisplayImage(product.get(MainActivity.FEED_TAG_ENTRY_IMAGE_LINK), thumb);

        vi.setBackgroundColor(Color.WHITE);

        return vi;
    }

    public void installNewData(ArrayList<HashMap<String, String>> d) {
        data = d;
        this.notifyDataSetChanged();
    }
}

public class MainActivity extends AppCompatActivity {

    static final String FILE_CACHE_DIR = "demoshop";
    static final String FEED_URL =
            "http://104.236.187.180/magento/facebook_adstoolbox_product_feed.xml";
    static final String FEED_TAG_ENTRY = "entry";
    static final String FEED_TAG_ENTRY_ID = "g:id";
    static final String FEED_TAG_ENTRY_TITLE = "g:title";
    static final String FEED_TAG_ENTRY_DESCRIPTION = "g:description";
    static final String FEED_TAG_ENTRY_IMAGE_LINK = "g:image_link";
    static final String FEED_TAG_ENTRY_PRICE = "g:price";
    static final String FEED_TAG_ENTRY_AVALIABILITY = "g:availability";

    ListView listView;
    LazyAdapter listAdapter;
    RetrieveFeedTask retrieveFeedTask;

    private DemoshopSession ds = new DemoshopSession();

    ArrayList<String> calculateCategoryList() {
        HashSet<String> allCategories = new HashSet<>();
        for (HashMap<String, String> product: retrieveFeedTask.productList) {
            String title = product.get(MainActivity.FEED_TAG_ENTRY_TITLE);
            String[] parts = title.split("\\s+");
            allCategories.add(parts[parts.length - 1]);
        }
        ArrayList<String> a = new ArrayList<>(allCategories);
        Collections.sort(a);
        a.add(0, "ALL");
        return a;
    }

    ArrayList<HashMap<String, String>> calculateProductsInCategory() {
        if (ds.selectedCategory.equals("ALL")) {
            return retrieveFeedTask.productList;
        }
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        for (HashMap<String, String> product: retrieveFeedTask.productList) {
            String title = product.get(MainActivity.FEED_TAG_ENTRY_TITLE);
            if (title.trim().endsWith(ds.selectedCategory)) {
                list.add(product);
            }
        }
        return list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        retrieveFeedTask = new RetrieveFeedTask();
        listView = (ListView)findViewById(R.id.listView);
        listAdapter = new LazyAdapter(this, this.calculateProductsInCategory(), FILE_CACHE_DIR);
        listView.setAdapter(listAdapter);
        retrieveFeedTask.setListViewAndAdapter(listView, listAdapter);
        retrieveFeedTask.execute(FEED_URL);

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, DetailActivity.class);
                ds.selectedProduct = retrieveFeedTask.productList.get((int)id);
                ds.saveToIntent(i);
                startActivity(i);
            }
        });

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        AppLinkData.fetchDeferredAppLinkData(this, new AppLinkData.CompletionHandler() {
            @Override
            public void onDeferredAppLinkDataFetched(AppLinkData ald) {
                if (ald != null) {
                    final String deferreddeeplink = ald.toString();
                    final String advertiserid =
                            AppEventsLogger.getAnonymousAppDeviceGUID(getApplicationContext());
                    final String msg =
                            String.format("DEFERRED DEEPLINK: %s\n\nAdvertiser ID: %s",
                                    deferreddeeplink, advertiserid);
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Got Deferred Deeplink Request")
                            .setMessage(msg)
                            .setPositiveButton("Copy & Close",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                    ClipboardManager clipboard =
                                            (ClipboardManager) getSystemService(
                                                    Context.CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("didiads", msg);
                                    clipboard.setPrimaryClip(clip);
                                }
                            })
                            .show();
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent i) {
        if (i != null) {
            setIntent(i);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent i = getIntent();

        Uri uriquery = i.getData();
        if (uriquery != null) {
            final String deeplink = uriquery.toString();
            final String advertiserid =
                    AppEventsLogger.getAnonymousAppDeviceGUID(getApplicationContext());
            final String msg =
                    String.format("DEEPLINK: %s\n\nAdvertiser ID: %s", deeplink, advertiserid);
            new AlertDialog.Builder(this)
                    .setTitle("Got Deeplink Request")
                    .setMessage(msg)
                    .setPositiveButton("Copy & Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ClipboardManager clipboard =
                                    (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("didiads", msg);
                            clipboard.setPrimaryClip(clip);
                        }
                    })
                    .show();
        } else {
            ds = DemoshopSession.ExtractFromIntent(i);
            listAdapter.installNewData(this.calculateProductsInCategory());
            listView.invalidateViews();
            listView.refreshDrawableState();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_select_category) {
            Intent i = new Intent(this, CategoryActivity.class);
            this.ds.saveToIntent(i);
            i.putExtra("categoryList", this.calculateCategoryList());
            startActivity(i);
            return true;
        } else if (id == R.id.action_show_cart) {
            Intent i = new Intent(this, CartActivity.class);
            this.ds.saveToIntent(i);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
