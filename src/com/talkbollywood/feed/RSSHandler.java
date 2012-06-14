package com.talkbollywood.feed;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
    
public class RSSHandler extends DefaultHandler {

    private boolean insideItem;
    private boolean insideTitle;
    private boolean insideCategory;
    private boolean insideLink;
    private boolean insideContent;
    private boolean insideEnclosure;    // thumbnail
    
    private String linkUrl = "";
    private String thumbUrl = "";
    private String contentData = "";
    
    private String ParsedTitle = "";   
    private boolean isCurrentItemInCategoryVideo = false;
    
    
    private List<ParsedElement> parsedItems = new ArrayList<ParsedElement>();
    
    public List<ParsedElement> getParsedElements()
    {
        return this.parsedItems;
    }
    
    public RSSHandler()
    {
        
    }
    
    public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
        if (localName.equals("item"))
        {
            insideItem = true;
        }            
        if (localName.equals("link") && insideItem == true)
        {
            insideLink = true;                
        }
        if (localName.equals("encoded") && insideItem == true)
        {
            insideContent = true;                
        }        
        if (localName.equals("enclosure") && insideItem == true)
        {
            insideEnclosure = true;        
            // Process each attribute
            for (int i=0; i<attrs.getLength(); i++) {
                // Get names and values for each attribute
                String name = attrs.getQName(i);
                String value = attrs.getValue(i);
                if(name.equalsIgnoreCase("url"))
                {
                    thumbUrl = value;
                }

            }
        }
        if (localName.equals("title") && insideItem == true)
        {
            insideTitle = true;                
        }
        if (localName.equals("category") && insideItem == true)
        {
            insideCategory = true;                
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        if (localName.equals("item"))
        {            
            insideItem = false;
            
            if(ParsedTitle.length() > 0)
            {
                this.parsedItems.add(new ParsedElement(ParsedTitle, linkUrl, thumbUrl, contentData, isCurrentItemInCategoryVideo));
            }
            ParsedTitle = "";
            linkUrl = "";
            thumbUrl = "";
            contentData = "";
            isCurrentItemInCategoryVideo = false;            
        }
        if (localName.equals("title"))
        {
            insideTitle = false;
        }
        if (localName.equals("category"))
        {
            insideCategory = false;
        }
        if (localName.equals("link"))
        {
            insideLink = false;
        }
        if (localName.equals("encoded"))
        {
            insideContent = false;
        }
        if (localName.equals("enclosure"))
        {
            insideEnclosure = false;
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        
        if(!insideItem)
        {
            return;
        }
        String cdata = new String(ch, start, length);
        String rssResult = (cdata.trim()).replaceAll("\\s+", " ");
        if (insideTitle)
        {                           
            ParsedTitle += rssResult;            
        }
        else if (insideCategory)
        {
            if(rssResult.contains("Videos"))
            {
                isCurrentItemInCategoryVideo = true;   
            }
        }
        else if (insideLink)
        {
            linkUrl += rssResult;
        }
        else if (insideContent)
        {
            contentData += rssResult;
        }
        else if (insideEnclosure)
        {
            thumbUrl += rssResult;
        }
    }
    
    /*
    public void endDocument()
    {
        
    }*/
    
    public String removeSpaces(String s) 
    {
        StringTokenizer st = new StringTokenizer(s," ",false);
        String t="";
        while (st.hasMoreElements()) t += st.nextElement();
        return t;
    }

}