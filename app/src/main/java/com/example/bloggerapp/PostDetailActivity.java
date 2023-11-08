package com.example.bloggerapp;

import static javax.xml.transform.OutputKeys.ENCODING;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PostDetailActivity extends AppCompatActivity {

    private TextView titleTv, pulishInfoTv;
    private WebView webView;
    private RecyclerView labelsRv, commentsRv;

    private String postId;
    private static final String TAG = "POST_DETAILS_TAG";
    private static final String TAG_COMMENT = "POST_COMMENTS_TAG";
    private ArrayList<ModelLabel> labelArrayList;
    private AdapterLabel adapterLabel;

    private ArrayList<ModelComment> commentArrayList;
    private AdapterComment adapterComment;

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Post Details");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        titleTv = findViewById(R.id.titleTv);
        pulishInfoTv = findViewById(R.id.publishInfoTv);
        webView = findViewById(R.id.webView);
        labelsRv = findViewById(R.id.labelsRv);
        commentsRv = findViewById(R.id.commentsRv);

        postId = getIntent().getStringExtra("postId");
        Log.d(TAG, "onCreate: " + postId);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        loadPostDetails();
    }

    private void loadPostDetails() {
        String url = "https://www.googleapis.com/blogger/v3/blogs/" + Constants.BLOG_ID +
                "/posts/" + postId +
                "?key=" + Constants.API_KEY;
        Log.d(TAG, "loadPostDetails: URL"+url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: "+ response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String title = jsonObject.getString("title");
                    String published = jsonObject.getString("published");
                    String content = jsonObject.getString("content");
                    String url = jsonObject.getString("url");
                    String displayName = jsonObject.getJSONObject("author").getString("displayName");

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
                    actionBar.setSubtitle(title);
                    titleTv.setText(title);
                    pulishInfoTv.setText("By"+displayName + " " + formattedDate);
                    webView.loadDataWithBaseURL(null, content, "text/html", ENCODING, null);

                    try{
                        labelArrayList = new ArrayList<>();
                        labelArrayList.clear();

                        JSONArray jsonArray = jsonObject.getJSONArray("labels");
                        for (int i = 0; i<jsonArray.length(); i++){
                            String label = jsonArray.getString(i);
                            ModelLabel modelLabel = new ModelLabel(label);
                            labelArrayList.add(modelLabel);
                        }
                        adapterLabel = new AdapterLabel(PostDetailActivity.this, labelArrayList);
                        labelsRv.setAdapter(adapterLabel);
                    }catch (Exception e){
                        Log.d(TAG, "onResponse: " + e.getMessage());
                    }
                    loadComments();
                }catch (Exception e){
                    Log.d(TAG, "onResponse: "+ e.getMessage());
                    Toast.makeText(PostDetailActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PostDetailActivity.this,""+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void loadComments(){
        String url = "https://www.googleapis.com/blogger/v3/blogs/"
                +Constants.BLOG_ID+"/posts/"
                +postId+"/comments?key="+Constants.API_KEY;
        Log.d(TAG_COMMENT, "LoadCommnents: "+url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG_COMMENT, "onResponse: " + response);

                commentArrayList = new ArrayList<>();
                commentArrayList.clear();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArrayItems =jsonObject.getJSONArray("items");
                    for(int i = 0; i < jsonArrayItems.length(); i++){
                        JSONObject jsonObjectComment = jsonArrayItems.getJSONObject(i);
                        String id = jsonObjectComment.getString("id");
                        String published = jsonObject.getString("published");
                        String content = jsonObject.getString("content");
                        String displayName = jsonObject.getJSONObject("author").getString("displayName");
                        String profileImage = "http:" + jsonObjectComment.getJSONObject("author").getJSONObject("image").getString("url");
                        Log.d("TAG_IMAGE_URL", "onResponse: " + profileImage);

                        ModelComment modelComment = new ModelComment(
                                "" + id,
                                "" + displayName,
                                "" + profileImage,
                                "" + published,
                                "" + content
                        );
                        commentArrayList.add(modelComment);
                    }
                    adapterComment = new AdapterComment(PostDetailActivity.this, commentArrayList);
                    commentsRv.setAdapter(adapterComment);
                } catch (Exception e) {
                    Log.d(TAG_COMMENT, "onResponse: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG_COMMENT, "onErrorResponse: "+ error.getMessage());
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