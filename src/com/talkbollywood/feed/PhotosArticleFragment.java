
package com.talkbollywood.feed;

import com.facebook.android.R;
import java.util.ArrayList;
import java.util.List;

import com.talkbollywood.feed.AsyncImageLoader.ImageRequestListener;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Fragment that shows a single photos article.
 */
public class PhotosArticleFragment extends Fragment implements ImageRequestListener{
    
    private View mContentView;
    private Handler mHandler;
    private String imageSrc = "";
    private String articleUrl = "";    
    
    ScrollView rootScrollView = null;    
    
    private List<ImageView> asyncImages = new ArrayList<ImageView>();
    private List<TextView> textBlocks = new ArrayList<TextView>();
    private List<ProgressBar> progBars = new ArrayList<ProgressBar>();
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    
    /** This is where we perform setup for the fragment that's either
     * not related to the fragment's layout or must be done after the layout is drawn.
     * Notice that this fragment does not implement onCreateView(), because it extends
     * ListFragment, which includes a ListView as the root view by default, so there's
     * no need to set up the layout.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
    }
    
    public View onCreateView (LayoutInflater inflater, android.view.ViewGroup container, Bundle savedInstanceState)
    {
        mContentView = inflater.inflate(R.layout.photos_article, null);
        
        rootScrollView = (ScrollView) mContentView.findViewById(R.id.scroll_root);
        
        ImageButton shareButton = (ImageButton) mContentView.findViewById(R.id.fbshare);        
        shareButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity parent = (MainActivity)PhotosArticleFragment.this.getActivity();
                parent.fbShareArticle(articleUrl);
            }
        });
        
        return mContentView;
    }   
    
    public void setArticleContent(String content, String title, String url)
    {           
        rootScrollView.scrollTo(0, 0);
        
        TextView tvTitle = (TextView) mContentView.findViewById(R.id.title);
        tvTitle.setText(title+"\r\n");
        this.articleUrl = url;
        
        // Clean up previous asyncImage views.
        LinearLayout rootView = (LinearLayout) mContentView.findViewById(R.id.root_article);
        for(int i=0; i<asyncImages.size(); i++)
        {            
            rootView.removeView(asyncImages.get(i));
        }
        asyncImages.clear();

        // Clean up previous text views.        
        for(int i=0; i<textBlocks.size(); i++)
        {            
            rootView.removeView(textBlocks.get(i));
        }
        textBlocks.clear();
        
        // Clean up previous progress bars.        
        for(int i=0; i<progBars.size(); i++)
        {            
            rootView.removeView(progBars.get(i));
        }
        progBars.clear();
                
        processContent(content);                       
    }   
    
    // Strip the src tags out of the content string
    private void processContent(String rawContent)
    {
        String [] array = rawContent.split("<fb:like", 2);
        String preProcessed = array[0];
        //String processed = preProcessed;
        
        int imageIndexStart = preProcessed.indexOf("href=");
        int imageIndexEnd = -1;                
        
        while(imageIndexStart >= 0)
        {           
            imageIndexEnd = preProcessed.indexOf("\"", imageIndexStart+6);
            String srcStringRaw = preProcessed.substring(imageIndexStart, imageIndexEnd+1);
            String[] tempSrc = srcStringRaw.split("\"");
            imageSrc = tempSrc[1];
            if(isImageLink(imageSrc))
            {                
                insertTextView("\r\n");
                int imageListIndex = insertInvisibleImageView();
                int progBarIndex = insertProgressBar();
                
                AsyncImageLoader imgLoader = new AsyncImageLoader();
                imgLoader.startAsync(this, imageSrc, imageListIndex);
            
                imageIndexStart = preProcessed.indexOf("href=", imageIndexEnd);                
            }
            else
            {
                imageIndexStart = preProcessed.indexOf("href=", imageIndexStart+1);
            }
        }                                
        
        return;
        
    }
    
    private void insertTextView(String text) {
        // TODO Auto-generated method stub
        TextView textBlock = new TextView(this.getActivity());
        textBlock.setTextSize(15);
        textBlock.setText(Html.fromHtml(text, new ImageGetter(), null));
        
        LinearLayout rootLayout = (LinearLayout) mContentView.findViewById(R.id.root_article);
        
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1);        
        
        rootLayout.addView(textBlock, lp);
        textBlocks.add(textBlock);
        
        return ;  
    }

    private boolean isImageLink(String src)
    {
        return (src.contains(".jpg"));        
    }
    
    @SuppressWarnings("deprecation")
    private int insertProgressBar()
    {
        ProgressBar bar = new ProgressBar(this.getActivity());        
        LinearLayout rootLayout = (LinearLayout) mContentView.findViewById(R.id.root_article);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1);        
        rootLayout.addView(bar, lp);
        progBars.add(bar);                       
        return progBars.size()-1;
    }
    
    @SuppressWarnings("deprecation")
    private int insertInvisibleImageView()
    {
        ImageView asyncImage = new ImageView(this.getActivity());
        LinearLayout rootLayout = (LinearLayout) mContentView.findViewById(R.id.root_article);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1);      
        asyncImage.setVisibility(View.INVISIBLE);
        rootLayout.addView(asyncImage, lp);
        asyncImages.add(asyncImage);                       
        return asyncImages.size()-1;
    }
    
    @Override
    public void onDestroyView() {
      super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onImageLoaded(final Bitmap image, final int context) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {                   
                asyncImages.get(context).setImageBitmap(image);
                asyncImages.get(context).setVisibility(View.VISIBLE);
                progBars.get(context).setVisibility(View.GONE);
            }
        });  
        
    }
    
    private class ImageGetter implements Html.ImageGetter {

        @SuppressWarnings("deprecation")
        public Drawable getDrawable(String source) {
            return new BitmapDrawable();
        }
    };

}


