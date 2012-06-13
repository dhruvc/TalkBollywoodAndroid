package com.talkbollywood.feed;

import com.talkbollywood.feed.AsyncImageLoader.ImageRequestListener;

import android.graphics.Bitmap;
import android.os.Handler;

public class NewsItem implements ImageRequestListener {     
    
    private ParsedElement metaData;
    private int index;
    private NewsItemEventListener listener;
    private Handler mHandler;
    private Bitmap thumbImage = null;
    
    NewsItem(ParsedElement element, int index, NewsItemEventListener listener)
    {
        this.metaData = element;
        this.index = index;
        this.listener = listener;
        mHandler = new Handler();
        this.loadImage();
        
    }

    public int getIndex()
    {
        return this.index;
    }
    
    public String getTitle()
    {
        return this.metaData.title;
    }
    
    public String getContent()
    {
        return this.metaData.content;
    }
    
    public String getWebLink()
    {
        return this.metaData.linkUrl;
    }   

    public Bitmap getThumbnailBitmap()
    {
        return this.thumbImage;
    }
    
    private void loadImage()
    {
        if(this.metaData.thumbUrl.length() > 0)
        {
            AsyncImageLoader imgLoader = new AsyncImageLoader();
            imgLoader.startAsync(this, this.metaData.thumbUrl, 0);
        }
    }
    
    public static interface NewsItemEventListener
    {
        public void onThumbnailLoaded(int itemIndex);
    }

    @Override
    public void onImageLoaded(final Bitmap image, final int context) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                thumbImage = image;
                // Tell view/activity to fetch updated views
                NewsItem.this.listener.onThumbnailLoaded(index);                
            }
        });  
    }
}