package com.talkbollywood.feed;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.R;
import com.facebook.android.Facebook.DialogListener;
import com.talkbollywood.feed.NewsListFragmentAdapter.NewsListListener;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;


public class MainActivity extends Activity implements ActionBar.TabListener, NewsListListener {

    private static int NEWS_TAB_POS = 0;
    private static int VIDEOS_TAB_POS = 1;
    private final String FB_APP_ID = "240398539388249";
    
    private ProgressDialog progressDialog;

    Facebook facebook = new Facebook(FB_APP_ID);
    
    private SharedPreferences mPrefs;
    
    public static AsyncFacebookRunner mAsyncRunner;
    public static String userUID = null;
    
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);

        ActionBar bar = getActionBar();
        //bar.setDisplayShowTitleEnabled(false);  
        //bar.setDisplayShowHomeEnabled(false);
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        bar.addTab(bar.newTab().setText(getText(R.string.tab_news)).setTabListener(this), NEWS_TAB_POS);

        bar.addTab(bar.newTab().setText(getText(R.string.tab_videos)).setTabListener(this), VIDEOS_TAB_POS);
        
        mAsyncRunner = new AsyncFacebookRunner(facebook);
        new Handler();
        
        /*
         * Get existing access_token if any
         */
        mPrefs = getPreferences(MODE_PRIVATE);
        String access_token = mPrefs.getString("access_token", null);
        long expires = mPrefs.getLong("access_expires", 0);
        if(access_token != null) {
            facebook.setAccessToken(access_token);
        }
        if(expires != 0) {
            facebook.setAccessExpires(expires);
        }        
    }    
    
    protected void onResume()
    {
        super.onResume();       
        ActionBar bar = getActionBar();
        if(bar.getSelectedNavigationIndex() == NEWS_TAB_POS)
        {
            this.updateVisibility(R.id.news_list_frag);   
        }
        else if(bar.getSelectedNavigationIndex() == VIDEOS_TAB_POS)
        {
            this.updateVisibility(R.id.videos_list_frag);
        }
    }

    protected void onPause()
    {
        super.onPause();
    }
    
    @SuppressWarnings("deprecation")
    protected void onStart ()
    {
        super.onStart();
        updateVisibility(R.id.news_list_frag);
        if(InstanceManager.NewsListFragmentAdapter().getCount() < 1)
        {
            progressDialog = ProgressDialog.show(this, "", getText(R.string.load_news_progress));
            InstanceManager.NewsListFragmentAdapter().setNewsListListener(this);
        }

    }    
    
    /* The following are callbacks implemented for the ActionBar.TabListener,
     * which this fragment implements to handle events when tabs are selected.
     */

    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        
        int selectedTabPos = tab.getPosition();
        
        if(selectedTabPos == NEWS_TAB_POS)
        {
            updateVisibility(R.id.news_list_frag);
        }
        else if(selectedTabPos == VIDEOS_TAB_POS)
        {
            updateVisibility(R.id.videos_list_frag);
        }
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //don't reload the current page when the orientation is changed        
        super.onConfigurationChanged(newConfig);
    }
    
    private void updateVisibility(int shownFragmentId)
    {
        Fragment newsFrag = getFragmentManager().findFragmentById(R.id.news_list_frag);     

        Fragment videosFrag = getFragmentManager().findFragmentById(R.id.videos_list_frag);
        
        Fragment articleFrag = getFragmentManager().findFragmentById(R.id.news_article_frag);

        Fragment shownFrag = getFragmentManager().findFragmentById(shownFragmentId);
        
        FragmentTransaction fTrans = getFragmentManager().beginTransaction();        
        fTrans.hide(newsFrag);
        fTrans.hide(videosFrag);
        fTrans.hide(articleFrag);        
        fTrans.show(shownFrag);        
        fTrans.commit();        
    }
    
    /* These must be implemented, but we don't use them */
    
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    public void onBackPressed()
    {
        // If news article fragment is visible
        // take back to news list
        Fragment articleFrag = getFragmentManager().findFragmentById(R.id.news_article_frag);
        if(articleFrag.isVisible())
        {
            this.updateVisibility(R.id.news_list_frag);
        }
        else
        {
            super.onBackPressed();
        }        
    }    
    
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        onTabSelected(tab, ft);
    }
    
    public void newArticleSelected(String content, String title, String url)
    {
        NewsArticleFragment articleFrag = (NewsArticleFragment)getFragmentManager().findFragmentById(R.id.news_article_frag);
        articleFrag.setArticleContent(content, title, url);
        this.updateVisibility(R.id.news_article_frag);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.authorizeCallback(requestCode, resultCode, data);
    }  
    
    public void fbShareArticle(final String articleUrl)
    {
        if(!facebook.isSessionValid()) {
            facebook.authorize(this, new String[] {"publish_actions" }, new DialogListener() {
                @Override
                public void onComplete(Bundle values) {
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString("access_token", facebook.getAccessToken());
                    editor.putLong("access_expires", facebook.getAccessExpires());
                    editor.commit();  
                    MainActivity.this.postOnFacebook(articleUrl);
                }

                @Override
                public void onFacebookError(FacebookError error) {
                    
                }

                @Override
                public void onError(DialogError e) {
                    
                }

                @Override
                public void onCancel() {
                    
                }
            });
        }   
        else
        {
            this.postOnFacebook(articleUrl);
        }
    }
    
    private void postOnFacebook(String url)
    {
        Bundle params = new Bundle();
        params.putString("link", url);
        params.putString("description", (String) getText(R.string.fb_post_desc));
        this.facebook.dialog(this, "feed", params, new FacebookRequestListener());
    }
    
    public void videoArticleSelected(String url)
    {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www." + url)));
    }
    
    private class FacebookRequestListener implements DialogListener{
        
        // called if there is an error
        public void onFacebookError(FacebookError error){
            Log.e("Facebook", error.getMessage());
        }

        @Override
        public void onComplete(Bundle values) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onError(DialogError e) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onCancel() {
            // TODO Auto-generated method stub
            
        }
    }

    @Override
    public void onFirstItemsReady() {
        progressDialog.dismiss();
        
    }
}