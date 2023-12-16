package com.example.bloggerapp;

import static javax.xml.transform.OutputKeys.ENCODING;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PageDetailsActivity extends AppCompatActivity {

    private TextView titleTv, publishInfoTv;
    private WebView webView;
    private String pageId;
    private ActionBar actionBar;
    private static final String TAG = "PageDetails_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_details);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Android Tutorials");
        actionBar.setTitle("Pages Details");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        titleTv = findViewById(R.id.titleTv);
        publishInfoTv = findViewById(R.id.publishInfoTv);
        webView = findViewById(R.id.webView);

        pageId = getIntent().getStringExtra("pageId");
        Log.d(TAG, "onCreate: PageId: " + pageId);

        loadPageDetails();
    }

    private void loadPageDetails(){
        String url = "https://www.googleapis.com/blogger/v3/blogs/"+Constants.BLOG_ID+"/pages/"+pageId+"?key="+Constants.API_KEY;
        Log.d(TAG,"loadPageDetails: URL: "+url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: " + response);

                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String title = jsonObject.getString("title");
                    String published = jsonObject.getString("published");
                    String content = jsonObject.getString("content");
                    String url = jsonObject.getString("url");
                    String id = jsonObject.getString("id");
                    String displayName = jsonObject.getJSONObject("author").getString("displayname");

                    String gmtDate = published;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy K:mm a");
                    String formattedDate = "";
                    try{
                        Date date = dateFormat.parse(gmtDate);
                        formattedDate = dateFormat2.format(date);
                    }catch (Exception e){
                        formattedDate = published;
                        e.printStackTrace();
                    }

                    titleTv.setText(title);
                    publishInfoTv.setText("By" + displayName + " "+ formattedDate);
                    webView.loadDataWithBaseURL(null, content,"text/html", ENCODING, null);


                }catch (Exception e){
                    Log.d(TAG, "onResponse: "+ e.getMessage());
                    Toast.makeText(PageDetailsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: "+ error.getMessage());
                Toast.makeText(PageDetailsActivity.this, "", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}