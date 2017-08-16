package com.parse.starter;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class UsersList extends AppCompatActivity {

    ListView usersListView;

    //This ArrayList will Store the userList from ParseServer
    ArrayList<String> userArrayList;

    //This Adapter is for the userArrayList

    ArrayAdapter<String> userListAdapter;


    //inflate the menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.main_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.tweet){

            final EditText tweetEditText = new EditText(UsersList.this);
            //display alertDialog

            AlertDialog tweetDialog = new AlertDialog.Builder(UsersList.this)
                    .setView(tweetEditText)
                    .setTitle("Send Tweet")
                    .setPositiveButton("send", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            //send tweet in edit text

                            String tweet = tweetEditText.getText().toString();

                            //save it to parse in new class called tweets

                            ParseObject parseObject = new ParseObject("Tweets");

                            //this will create a new column called username if dosent exists and save in it
                            parseObject.put("username",ParseUser.getCurrentUser().getUsername());

                            parseObject.put("tweet",tweet);


                            //save in parse

                            parseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                    if(e == null){

                                        Toast.makeText(UsersList.this, "Tweet Sent", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }
                    })

                    .setNegativeButton("cancel",null)
                    .show();



        }else  if(item.getItemId() == R.id.logout){

            //logout user
            ParseUser.getCurrentUser().logOut();

            //directUser back to signUpLogin Page
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);

            startActivity(intent);
        }else if(item.getItemId() == R.id.Feed){

            Intent feedIntent = new Intent(getApplicationContext(),Feed.class);

            startActivity(feedIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        setTitle("Users List");

        //create a an empty list to store users currentsUsers follow to add to it  later if currentuser dosent have list already in their column in parse

        if(ParseUser.getCurrentUser().getList("following") == null){

            //so put an list in  teh column

            List<String> emptyList = new ArrayList<>();

            ParseUser.getCurrentUser().put("following",emptyList);

        }

        usersListView = (ListView) findViewById(R.id.userListView);


        //This will allow users to choose from the list , as many rows as they like

        usersListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);


        userArrayList = new ArrayList<String>();


        userListAdapter =  new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_checked,userArrayList);

        usersListView.setAdapter(userListAdapter);

        //get all the users and store them in the UsersArrayList
        ParseQuery<ParseUser> getUsersQuery =  ParseUser.getQuery();

        //Get all users  except current user

        getUsersQuery.whereNotEqualTo("username",ParseUser.getCurrentUser().getUsername());


        getUsersQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {

                if(e == null){

                    if(objects.size()> 0){

                        //run through all the objects

                        for(ParseUser user : objects){

                            userArrayList.add(user.getUsername());

                            //check if currentUser is following this user by checking the array in the following column in parse

                            List<String> followedUsers = ParseUser.getCurrentUser().getList("following");


                            if(followedUsers.contains(user.getUsername())){

                                //Check the tick

                                usersListView.setItemChecked(userArrayList.indexOf(user.getUsername()),true);

                            }

                            //notify adapter with the changes

                            userListAdapter.notifyDataSetChanged();

                        }


                    }else{

                        userArrayList.clear();

                        userArrayList.add("No users");

                        userListAdapter.notifyDataSetChanged();

                    }
                }

            }
        });



        //To get the row that was taped on

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //To see if the view is checked we need a checkedTextView object to use the isChecked() function

                CheckedTextView checkedTick = (CheckedTextView) view;

                if(checkedTick.isChecked()){

                    //follow user

                    //add to following array in parse
                    ParseUser.getCurrentUser().getList("following").add(userArrayList.get(i));

                    //save changes
                    ParseUser.getCurrentUser().saveInBackground();
                }else{

                    //unFollow user

                    //remove from following array in parse
                    ParseUser.getCurrentUser().getList("following").remove(userArrayList.get(i));

                    //save changes
                    ParseUser.getCurrentUser().saveInBackground();

                }


            }
        });

    }
}
