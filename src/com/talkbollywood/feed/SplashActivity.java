package com.talkbollywood.feed;

import com.facebook.android.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;


public class SplashActivity extends Activity
{
    private final int SPLASH_DISPLAY_TIME = 5000;
    
    private final int ANIMATION_TIME = 3000;
    
    private SoundPool soundPool;
    private int soundID;
    
    private final int INTERNET_ALERT_DIALOG_ID = 0;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);               
        setContentView(R.layout.splash);        
        
        // Start loading data behind the scenes..
        InstanceManager.NewsListFragmentAdapter();
        InstanceManager.VideosListFragmentAdapter();        
    }
    
    @SuppressWarnings("deprecation")
    @Override
    protected void onStart()
    {
        super.onStart();         
        
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(ANIMATION_TIME);
        
        //View appIcon = findViewById(R.id.big_app_icon);
        View descfirst = findViewById(R.id.descfirst);
        View descsecond = findViewById(R.id.descsecond);
        
        //appIcon.setAnimation(animation);
        descfirst.setAnimation(animation);
        descsecond.setAnimation(animation);
        
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        
        soundID = soundPool.load(this, R.raw.splash_sound, 1);
        
        soundPool.setOnLoadCompleteListener(new SoundLoadListener());
        
        if(!isInternetConnected())
        {
            showDialog(INTERNET_ALERT_DIALOG_ID);
            return;
        }
        
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                //Finish the splash activity so it can't be returned to.
                SplashActivity.this.finish();
                // Create an Intent that will start the main activity.
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(mainIntent);
            }
        }, SPLASH_DISPLAY_TIME);           
        
    }
    
    protected Dialog onCreateDialog(int id) {
        AlertDialog dialog = null;
        switch(id) {
        case INTERNET_ALERT_DIALOG_ID:
            // do the work to define the pause Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getText(R.string.no_internet_warning))
            .setCancelable(false)
            .setPositiveButton(getText(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                     SplashActivity.this.finish();
                }
            });
            dialog = builder.create();
            break;
        default:
            dialog = null;
        }
        return dialog;
    }
    
    private class SoundLoadListener implements SoundPool.OnLoadCompleteListener
    {

        @Override
        public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            soundPool.play(soundID, actualVolume, actualVolume, 1, 0, 1f);            
        }
        
    }
    
    
    private boolean isInternetConnected() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}