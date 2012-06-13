package com.talkbollywood.feed;

public class ParsedElement
{
    public String title;
    public String linkUrl;
    public String thumbUrl;  
    public String content;
    public boolean isVideoArticle;
    
    ParsedElement(String title, String link, String thumb, String content, boolean containsVideo)
    {
        this.title = title;
        this.linkUrl = link;
        this.thumbUrl = thumb;
        this.content = content;
        this.isVideoArticle = containsVideo;
    }
}