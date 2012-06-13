
package com.talkbollywood.feed;

import android.app.ListFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Fragment that shows the list of videos
 */
public class VideosListFragment extends ListFragment {

    private VideosListFragmentAdapter adapter;
    
    /** This is where we perform setup for the fragment that's either
     * not related to the fragment's layout or must be done after the layout is drawn.
     * Notice that this fragment does not implement onCreateView(), because it extends
     * ListFragment, which includes a ListView as the root view by default, so there's
     * no need to set up the layout.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Must call in order to get callback to onCreateOptionsMenu()
        setHasOptionsMenu(true);
        
        ListView lv = getListView();
        lv.setCacheColorHint(Color.TRANSPARENT); // Improves scrolling performance
        
        adapter = InstanceManager.VideosListFragmentAdapter(this.getActivity());
        setListAdapter((ListAdapter) adapter);
    }

    @Override
    public void onDestroyView() {
      super.onDestroyView();
      // destroy adapter too.
    }
    
    
    public void onStart()
    {
        super.onStart();
        this.adapter.reloadVideosFeed();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        
        if(this.adapter.getItemIndex(position) < 0)
        {
            return;
        }
        
        String videoUrl = this.adapter.getItemVideoLink(position);        
        MainActivity parent = (MainActivity)this.getActivity();
        parent.videoArticleSelected(videoUrl);
    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}


