
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
    public AsyncRSSWrapper()
    {
        
    }
    
    public void startAsync(final RssRequestListener listener, final String feedUrl)
    {
        new Thread(){
            @Override public void run() {
                try {                                               
                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    SAXParser saxParser = factory.newSAXParser();
                    XMLReader xmlReader = saxParser.getXMLReader();
                    RSSHandler rssHandler = new RSSHandler();
                    xmlReader.setContentHandler(rssHandler);                    
                    
                    
                    {             
                        URL rssUrl = new URL(feedUrl);

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