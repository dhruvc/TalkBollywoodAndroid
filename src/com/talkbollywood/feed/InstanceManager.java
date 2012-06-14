/*
This is manager of singleton classes
*/

package com.talkbollywood.feed;

import android.content.Context;

public class InstanceManager
{
    private static NewsListFragmentAdapter newsListAdapter = null;
    private static VideosListFragmentAdapter videosListAdapter = null;
    private static PhotosListFragmentAdapter photosListAdapter;
 
    public static NewsListFragmentAdapter NewsListFragmentAdapter()
    {
        if(newsListAdapter == null)
        {
            newsListAdapter = new  NewsListFragmentAdapter(null);  
        }
        
        return newsListAdapter;
    }
    
    public static NewsListFragmentAdapter NewsListFragmentAdapter(Context context)
    {
        if(newsListAdapter == null)
        {
            newsListAdapter = new  NewsListFragmentAdapter(context);  
        }
        else
        {
            newsListAdapter.setContext(context);
        }
        
        return newsListAdapter;
    }
    
    
    public static VideosListFragmentAdapter VideosListFragmentAdapter()
    {
        if(videosListAdapter == null)
        {
            videosListAdapter = new  VideosListFragmentAdapter(null);  
        }
        
        return videosListAdapter;
    }
    
    public static VideosListFragmentAdapter VideosListFragmentAdapter(Context context)
    {
        if(videosListAdapter == null)
        {
            videosListAdapter = new  VideosListFragmentAdapter(context);  
        }
        else
        {
            videosListAdapter.setContext(context);
        }
        
        return videosListAdapter;
    }
    
    public static PhotosListFragmentAdapter PhotosListFragmentAdapter()
    {
        if(photosListAdapter == null)
        {
            photosListAdapter = new  PhotosListFragmentAdapter(null);  
        }
        
        return photosListAdapter;
    }
    
    public static PhotosListFragmentAdapter PhotosListFragmentAdapter(Context context)
    {
        if(photosListAdapter == null)
        {
            photosListAdapter = new  PhotosListFragmentAdapter(context);  
        }
        else
        {
            photosListAdapter.setContext(context);
        }
        
        return photosListAdapter;
    }
}