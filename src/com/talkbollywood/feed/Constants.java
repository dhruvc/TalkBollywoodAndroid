package com.talkbollywood.feed;

public class Constants
{
    public static final String VIDEOS_FEED_URL = "http://www.talkbollywood.com/category/videos/feed/";
    public static final String NEWS_FEED_URL = "http://www.talkbollywood.com/feed/";
    public static final String PHOTOS_FEED_URL = "http://www.talkbollywood.com/category/photos/feed/";
    public static final int RELOAD_THRESHHOLD = 20*60*1000;
    public static final String FB_APP_ID = "240398539388249";
    
    public static final int SPLASH_DISPLAY_TIME = 5000;    
    public static final int SPLASH_ANIMATION_TIME = 3000;
    
    public enum Category
    {
        none,
        photos,
        videos,
    }
    
}