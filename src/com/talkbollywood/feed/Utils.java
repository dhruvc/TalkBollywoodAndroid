
package com.talkbollywood.feed;

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