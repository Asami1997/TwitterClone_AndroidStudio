
package com.parse.starter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;


public class MainActivity extends AppCompatActivity {


  EditText usernameEditText;

  EditText passwordEditText;

  String userName;

  String password;


  //This function directs the user to the UsersList activity
  public void startUsersListIntent(){

    Intent userListIntent = new Intent(getApplicationContext(),UsersList.class);
    startActivity(userListIntent);
  }

  //this function attempts to login user or sign user up if they have no existing account
  public void signUpOrLogin(View view){

    //Logout current user if there is one to avoid errors

    if(ParseUser.getCurrentUser() != null){

      ParseUser.getCurrentUser().logOut();
    }


    userName = usernameEditText.getText().toString();

    password = passwordEditText.getText().toString();


    if(userName.length() > 0 && password.length() > 0){

      Log.i("here","yes");

      //check if user has an account

      ParseQuery<ParseUser> getUsers =  ParseUser.getQuery();

      getUsers.whereEqualTo("username",userName);

      getUsers.findInBackground(new FindCallback<ParseUser>() {
        @Override
        public void done(List<ParseUser> objects, ParseException e) {

          if(e == null){

            if(objects.size() > 0 ){

              // Login

              ParseUser.logInInBackground(userName, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {

                  if(e == null){

                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                    startUsersListIntent();
                  }

                }
              });


            }else {

              //SignUp
              ParseUser parseUser = new ParseUser();

              parseUser.setUsername(userName);

              parseUser.setPassword(password);

              parseUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {

                  if(e == null){

                    Toast.makeText(MainActivity.this, "SignUp SuccessFull", Toast.LENGTH_SHORT).show();

                    startUsersListIntent();
                  }else {

                    Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                  }
                }
              });


            }
          }
        }
      });




    }else {

      Toast.makeText(MainActivity.this, "Please check enter both username and password", Toast.LENGTH_SHORT).show();
    }




  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    //ParseUser.getCurrentUser().logOut();
    setTitle("Twitter Clone");

    usernameEditText = (EditText) findViewById(R.id.usernameEditText);

    passwordEditText = (EditText) findViewById(R.id.passwordEditText);


    //if user is logged in then direct immediately to UsersList Activity

     if(ParseUser.getCurrentUser() != null){

       startUsersListIntent();
     }

    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

}
