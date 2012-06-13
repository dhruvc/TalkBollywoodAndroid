
package com.talkbollywood.feed;

import java.io.InputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class AsyncImageLoader
{ 
    
    public AsyncImageLoader()
    {
        
    }
    
    public void startAsync(final ImageRequestListener listener, final String imageUrl, final int context)
    {
        new Thread(){
            @Override public void run() {
                try {                        
                    listener.onImageLoaded(loadBitmap(imageUrl), context);                    
                } 
                catch (Exception e) {
                    
                }      
            }
        }.start();
    }
    
    private Bitmap loadBitmap(String url) {

        Bitmap bitmap = null;

        try {
            InputStream in = new java.net.URL(url).openStream();   
            bitmap = BitmapFactory.decodeStream(in);
        } 
        catch (Exception e) {

        } 

        return bitmap;
    }
    
    public static interface ImageRequestListener {
        public void onImageLoaded(Bitmap image, int context);        
    }
}