package com.codepath.apps.restclienttemplate;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.LinearLayout;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    private RecyclerView rvTweets;
    private TweetAdapter adapter;
    private List<Tweet> tweets;
    private SwipeRefreshLayout swipeContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client= TwitterApp.getRestClient(this);

        swipeContainer=findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //find recyclerview
        rvTweets=findViewById(R.id.rvTweets);
        //initialize list of tweeets and adapter from the date source
        tweets=new ArrayList<>();
        adapter= new TweetAdapter(this,tweets);
        //Recyler view setup, layout manager and setting the adapter
         rvTweets.setLayoutManager( new LinearLayoutManager(this));
         rvTweets.setAdapter(adapter);
        pouplateHomeTimeline();

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("TwitterClient","content is being refreshed");
                pouplateHomeTimeline();

            }
        });
    }

    private void pouplateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<Tweet> tweetstoAdd=new ArrayList<>();
               // Log.d("TwitterClient", response.toString())   ;
                for(int i=0;i<response.length();i++){
                    try {
                        JSONObject jsonTweetObjects= response.getJSONObject(i);
                        Tweet tweet= Tweet.fromJson(jsonTweetObjects);
                        tweetstoAdd.add(tweet);
                       // tweets.add(tweet);
                       // adapter.notifyItemChanged(tweets.size()-1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.clear();
                adapter.addTweets(tweetstoAdd);
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("TwitterClient", responseString)   ;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString())   ;

            }
        });
    }
}
