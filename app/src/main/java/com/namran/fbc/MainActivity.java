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

                TwitterSession session = result.data;

                info.setText(
                        "User ID: "
                                + session.getUserName()
                                + "\n" +
                                "Auth Token: "
                                + session.getAuthToken()
                );

                // Hide login button
                twloginButton.setText("Logout Twitter");
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
                    handled = true;
                }
                return handled;
            }
        });

        postButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        if ( checkBox.isChecked() ) {
                        sendMessage(editText.getText().toString());
                        }
                        if ( checkBoxtw.isChecked() ) {
                            tweetMessage(editText.getText().toString());
                        }
                    }
                });

        clearButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        editText.setText("");
                        info.setText("");
                    }
                });

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("http://developers.facebook.com"))
                .build();




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

    }

    private void tweetMessage(String v) {

        TweetComposer.Builder builder = new TweetComposer.Builder(this)
                      .text("#mySME "+ v.toString());
        builder.show();


    }


    private void logoutTwitter() {

        CookieManager.getInstance().setCookie(".twitter.com", "auth_token=''");
    }

    private void sendMessage(String v) {

        ShareDialog shareDialog;
        shareDialog = new ShareDialog(this);
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Hello World ! Bismillah ")
                    .setContentDescription(v.toString())
                    .setContentUrl(Uri.parse("http://www.namran.com"))
                    .build();

            shareDialog.show(linkContent);
        }

    }

    @Override
    protected void onActivityResult(final int requestCode,final int resultCode,final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        twloginButton.onActivityResult(requestCode, resultCode, data);
    }


}
