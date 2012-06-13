
package com.talkbollywood.feed;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownServiceException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class AsyncRSSWrapper
{ 
    
    private final String VIDEOS_FEED_URL = "http://www.talkbollywood.com/category/videos/feed/";
    private final String NEWS_FEED_URL = "http://www.talkbollywood.com/feed/";
    
    public AsyncRSSWrapper()
    {
        
    }
    
    public void startAsync(final RssRequestListener listener, final boolean videoOnly)
    {
        new Thread(){
            @Override public void run() {
                try {                                               
                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    SAXParser saxParser = factory.newSAXParser();
                    XMLReader xmlReader = saxParser.getXMLReader();
                    RSSHandler rssHandler = new RSSHandler(videoOnly);
                    xmlReader.setContentHandler(rssHandler);                    
                    
                    
                    {             
                        URL rssUrl = null;
                        if(videoOnly)
                        {                            
                            rssUrl = new URL(VIDEOS_FEED_URL);
                        }
                        else
                        {
                            rssUrl = new URL(NEWS_FEED_URL);
                        }
                        HttpURLConnection connection = (HttpURLConnection)rssUrl.openConnection();
                        
                        InputStream ipS = connection.getInputStream();
                        
                        InputSource inputSource = new InputSource(ipS);
                        
                        xmlReader.parse(inputSource);
                    }
        
                    listener.onParsedElements(rssHandler.getParsedElements());                    
                } 
                catch (UnknownServiceException e){
                    
                }        
                catch (IOException e) {
                    
                } 
                catch (SAXException e) {
                    
                } 
                catch (ParserConfigurationException e) {
                    
                }                    
            }
        }.start();
    }
    
    public static interface RssRequestListener {
        public void onParsedElements(List<ParsedElement> values);        
    }
}