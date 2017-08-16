package com.parse.starter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Feed extends AppCompatActivity {

    ListView feedListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        setTitle("Feed");
        feedListView = (ListView) findViewById(R.id.feedListView);

        //Each Map Will Contain A Tweet as the Item and The userName as The subItem
        final List<Map<String,String>> tweetUserList = new ArrayList<Map<String,String>>();



        //get Tweets From Parse
        ParseQuery<ParseObject> getTweets = ParseQuery.getQuery("Tweets");


        getTweets.whereContainedIn("username",ParseUser.getCurrentUser().getList("following"));

        getTweets.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    if(objects.size() > 0){

                        for(ParseObject user : objects){

                            //Create The Map That Will Contain Tweet and UserName

                            Map<String,String> tweetAndUserName = new HashMap<String, String>();

                            tweetAndUserName.put("Tweet",user.getString("tweet"));

                            tweetAndUserName.put("username",user.getString("username"));

                            //Add it to the List That Contains all the Maps

                            tweetUserList.add(tweetAndUserName);
                        }


                        //put these here beacause the excution continues even if the findincallback dident finish excution which may result in a null listView
                        SimpleAdapter simpleAdapter = new SimpleAdapter(Feed.this,tweetUserList,android.R.layout.simple_list_item_2,new String[]{"Tweet","username"},new int[]{android.R.id.text1,android.R.id.text2});

                        feedListView.setAdapter(simpleAdapter);

                    }
                }
            }
        });



    }
}
