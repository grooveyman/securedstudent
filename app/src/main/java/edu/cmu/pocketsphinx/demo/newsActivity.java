package edu.cmu.pocketsphinx.demo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class newsActivity extends AppCompatActivity {

    private WebView view;
    private String url = "https://www.myjoyonline.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        getSupportActionBar().setTitle("News");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        view =  findViewById(R.id.webview);

        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setLoadWithOverviewMode(true);
        view.getSettings().setUseWideViewPort(true);
        view.getSettings().setBuiltInZoomControls(true);
        view.getSettings().setPluginState(WebSettings.PluginState.ON);
        view.setWebViewClient(new WebViewClient());

        view.loadUrl(url);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Check if the key event was the back button and if there's history
        if((keyCode == KeyEvent.KEYCODE_BACK) && view.canGoBack()){
            view.goBack();
            return true;
        }

        //If it wasn't the back key or there's no web page history
        //system behaviour (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == item.getItemId()){
            //end Activity
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
