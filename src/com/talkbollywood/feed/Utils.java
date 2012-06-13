
package com.talkbollywood.feed;

import android.content.Intent;
import android.net.Uri;

public class Utils{
    
    public static String extractYoutubeLink(String rawContent)
    {
        int indexStart = rawContent.indexOf("youtube.com/");
        int indexEnd = -1;        
        String srcString = "";
        if(indexStart >= 0)
        {
            indexEnd = rawContent.indexOf("?", indexStart+6);
            
            srcString = rawContent.substring(indexStart, indexEnd);            
        }                
        
        return srcString;
        
    }    
}