package com.talkbollywood.feed;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.talkbollywood.feed.AsyncRSSWrapper.RssRequestListener;
import com.talkbollywood.feed.NewsItem.NewsItemEventListener;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PhotosListFragmentAdapter extends BaseAdapter implements ListAdapter, RssRequestListener, NewsItemEventListener{        

    private Context context;
    private Handler mHandler;
    
    Date lastLoadDate = null;
    private List<NewsItem> items = new ArrayList<NewsItem>();
    
    public PhotosListFragmentAdapter(Context context) {
        this.context = context;
        mHandler = new Handler();
        this.reloadPhotosFeed();
    }
    
    public void setContext(Context context)
    {
        this.context = context;
    }
    
    public void reloadPhotosFeed()
    {       
        if(lastLoadDate == null)
        {
            lastLoadDate = new Date();
            AsyncRSSWrapper wrapper = new AsyncRSSWrapper();
            wrapper.startAsync(this, Constants.PHOTOS_FEED_URL);
            return;
        }
        
        Date currentDate = new Date();
        long msSinceLastLoad = currentDate.getTime() - lastLoadDate.getTime(); 
        if(msSinceLastLoad > Constants.RELOAD_THRESHHOLD)
        {
            lastLoadDate = currentDate;
            AsyncRSSWrapper wrapper = new AsyncRSSWrapper();
            wrapper.startAsync(this, Constants.PHOTOS_FEED_URL); 
            return;            
        }           
    }
    
    // Impl for RequestListener
    public void onParsedElements(final List<ParsedElement> values)
    {               
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                items.clear();                
                for(int i=0; i<values.size(); i++)
                {
                    items.add(new NewsItem(values.get(i), i, PhotosListFragmentAdapter.this));                    
                }
                notifyDataSetChanged();
            }
        });        
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.news_item, null);
        }
        NewsItem item = items.get(position);
        if (item != null) {
            TextView title = (TextView) v.findViewById(R.id.title);
            ImageView thumb = (ImageView) v.findViewById(R.id.thumb);
            ProgressBar bar = (ProgressBar)v.findViewById(R.id.progressBar1);
            
            if (item.getThumbnailBitmap() != null) {
                thumb.setImageBitmap(item.getThumbnailBitmap());
                
                thumb.setVisibility(View.VISIBLE);                
                bar.setVisibility(View.GONE);                
            }
            else
            {                
                thumb.setVisibility(View.GONE);   
                bar.setVisibility(View.VISIBLE);     
            }
            
            if (item.getTitle() != null) {
                title.setText(item.getTitle());
            }
        }
        
        return v;

    }
    
    public int getItemIndex(int position)
    {
        return this.items.get(position).getIndex();
    }    
    
    @Override
    public int getCount() {

        return items.size();
    }

    @Override
    public Object getItem(int position) {
        
        return items.get(position);
        
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    public String getItemContent(int index)
    {
        return this.items.get(index).getContent();
    }

    public String getItemTitle(int index)
    {
        return this.items.get(index).getTitle();
    }

    public String getItemUrl(int index)
    {
        return this.items.get(index).getWebLink();
    }
    
    @Override
    // Impl for NewsItemEventListener
    public void onThumbnailLoaded(int itemIndex) {
        // Maybe we can use the itemIndex for something useful
        // later.
        
        // Tell view/activity to fetch updated views
        PhotosListFragmentAdapter.this.notifyDataSetChanged();   
        
    }
}