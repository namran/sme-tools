package com.namran.fbc;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.concurrency.AsyncTask;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookDialog;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;


import com.twitter.sdk.android.core.*;
import com.twitter.sdk.android.core.identity.*;


import io.fabric.sdk.android.Fabric;

import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;


public class MainActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    //private static final String TWITTER_KEY = "voDNEpA1Hd6e2M1v1Cu7qzHs8";
    //private static final String TWITTER_SECRET = "vIWbFV8TGdseY9hIiZY7y2iboarencRWKE00UNT7jqIowrSkB0";

    private static final String TWITTER_KEY = "SdOeeKTv1I7j73gjlRtEzWP7q";
    private static final String TWITTER_SECRET = "Vis0ivutpLYyuP44zFjxcSh3xB5g9tfcWoJmmKTvcM4Axt6FqA";




    private TextView info;
    private LoginButton loginButton;

    private CallbackManager callbackManager;

    private ShareButton shareButton;

    // twitter
    private TwitterLoginButton twloginButton;


    @Override




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // FB
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_main);
        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);

        Button postButton = (Button) findViewById(R.id.post_button);
        Button clearButton = (Button) findViewById(R.id.clear_button);
        // Twitter
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        Fabric.with(this, new TwitterCore(authConfig), new TweetComposer());




        twloginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);




        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkfb);
        if (checkBox.isChecked()) {
            checkBox.setChecked(true);
        }

        final CheckBox checkBoxtw = (CheckBox) findViewById(R.id.checktwitter);
        if (checkBoxtw.isChecked()) {
            checkBoxtw.setChecked(false);
        }


        //twloginButton.registerCallback(callbackManager,
        twloginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                TwitterSession session = result.data;
                // TODO: Remove toast and use the TwitterSession's userID
                // with your app's user model
                //String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                //TwitterSession session = Twitter.getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                //String token = authToken.token;
                //String secret = authToken.secret;
                info.setText(
                        "User ID: "
                                + session.getUserName()
                                + "\n" +
                                "Auth Token: "
                                + session.getAuthToken()
                );

                // Hide login button
                //twloginButton.setText("Logout Twitter");
                //twloginButton.setOnClickListener(twloginButton.logout());
            }

            @Override
            public void failure(TwitterException exception) {
                //Log.d("TwitterKit", "Login with Twitter failure", exception);
                info.setText("Login to Twitter attempt failed.");
            }
        });


        final EditText editText = (EditText) findViewById(R.id.text_to_post);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMessage(editText.getText().toString());
                    //shareButton = (ShareButton)findViewById(R.id.fb_share_button);
                    //shareButton.setShareContent();
                    handled = true;
                }
                return handled;
            }
        });

        postButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        //Log.v("EditText", EditText.getText().toString());
                        sendMessage(editText.getText().toString());
                        if ( checkBoxtw.isChecked() ) {
                            tweetMessage(editText.getText().toString());
                        };
                    }
                });

        clearButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        //Log.v("EditText", EditText.getText().toString());
                        editText.setText("");
                        info.setText("");
                    }
                });
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("http://developers.facebook.com"))
                .build();

        //shareButton.setShareContent(content);

        // this part is optional
       // shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {


        // });


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                info.setText(
                        "User ID: "
                                + loginResult.getAccessToken().getUserId()
                                + "\n" +
                                "Auth Token: "
                                + loginResult.getAccessToken().getToken()
                );
            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");
            }
        });


/*
        twloginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        twloginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
            }

        });

*/
        /*
        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        TwitterAuthToken authToken = session.getAuthToken();
        String token = authToken.token;
        String secret = authToken.secret;

*/
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.namran.fbc",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }

    private void tweetMessage(String v) {

        //AsyncTask.Status status = twitter.updateStatus(v.toString());
        //System.out.println("Successfully updated the status to [" + status.getText() + "].");


        //TwitterSession session = Twitter.getSessionManager().getActiveSession();
        //TwitterAuthToken authToken = session.getAuthToken();

        /*
        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        //TwitterAuthToken authToken = session.getAuthToken();

        //final TwitterSession session = TwitterCore.getInstance().getSessionManager()
        //        .getActiveSession();
        final Intent intent = new ComposerActivity.Builder(this)
            //ComposerActivity.Builder()
               // .text("#mySME " + v.toString());
                    .session(session)
                .createIntent();
        startActivity(intent);

        */

        TweetComposer.Builder builder = new TweetComposer.Builder(this)
                      .text("#mySME "+ v.toString());

        //.image();
        builder.show();





    }


    private void logoutTwitter() {

        CookieManager.getInstance().setCookie(".twitter.com", "auth_token=''");
    }

    private void sendMessage(String v) {
        // When Post Status Update button is clicked
        //public void postStatusUpdate(View v){
            // Pass null as parameter for setLink method to post status update
           // ShareDialog shareDialog = new shareDialog.ShareLinkContentBuilder(this)
                   // .setLink(null)
                   // .build();
          //  uiHelper.trackPendingDialogCall(shareDialog.present());
       // }
        //ShareButton shareButton = (ShareButton)findViewById(R.id.fb_share_button);
        ShareDialog shareDialog;
        shareDialog = new ShareDialog(this);
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Hello World ! Bismillah ")
                    //.setContentDescription(
                    //        "The 'Hello World' from simplified apps for integration.")
                    .setContentDescription(v.toString())
                    .setContentUrl(Uri.parse("http://www.namran.com"))
                    .build();

            shareDialog.show(linkContent);
        }

/*
        TweetComposer.Builder builder = new TweetComposer.Builder(this)
                .text(v.toString());
        //.image();
        //builder.show();

        final TwitterSession session = TwitterCore.getInstance().getSessionManager()
                .getActiveSession();
        final Intent intent = new ComposerActivity.Builder(builder)
                .session(session)
                .createIntent();
        startActivity(intent);

*/
        // The factory instance is re-useable and thread safe.
       //Twitter twitter = TwitterFactory.getSingleton();
        //AsyncTask.Status status = twitter.updateStatus(v.toString());
        //System.out.println("Successfully updated the status to [" + status.getText() + "].");
        //info.setText(status.toString());
    }

    @Override
    protected void onActivityResult(final int requestCode,final int resultCode,final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        twloginButton.onActivityResult(requestCode, resultCode, data);
    }


}
