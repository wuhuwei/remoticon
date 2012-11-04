package edu.berkeley.remoticon;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class GuideFragment extends Fragment {
	
	static final String[] numbers = new String[] { 
		"A", "B", "C", "D", "E",
		"F", "G", "H", "I", "J",
		"K", "L", "M", "N", "O",
		"P", "Q", "R", "S", "T",
		"U", "V", "W", "X", "Y", "Z"};
	
	private RoviApiHandler apiHandler;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist.  The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed.  Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.
            return null;
        }
		return (LinearLayout)inflater.inflate(R.layout.guide_layout, container, false);
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        apiHandler = new RoviApiHandler();
 
        Activity activity = getActivity();
        
        if (activity != null) {
            ListView guide = (ListView) activity.findViewById(R.id.guideView);
            ArrayList<TVGuideEntry> items = apiHandler.getListings("76550");
            Log.e("hai", items.toString());
            GuideItemAdapter guideAdapter = new GuideItemAdapter(activity.getBaseContext(), R.id.show_label, items);
            guide.setAdapter(guideAdapter);
        }
    }
	
	private class GuideItemAdapter extends ArrayAdapter<TVGuideEntry> {
		private ArrayList<TVGuideEntry> items;
		Context context;
		
		public GuideItemAdapter(Context context, int textViewResourceId, ArrayList<TVGuideEntry> items) {
			super(context, textViewResourceId, items);
			this.items = items;
			this.context = context;
		}
		
		@Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	            View v = convertView;
	            if (v == null) {
	                LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	                v = vi.inflate(R.layout.guide_channel_item, null);
	            }
	            TVGuideEntry e = items.get(position);
	            if (e != null) {
	                    TextView channelName = (TextView) v.findViewById(R.id.channelAbbrev);
	                    TextView channelNumber = (TextView) v.findViewById(R.id.channelNum);
	                    if (channelName != null) {
	                    	channelName.setText(e.getChannel().getAbbr());                            }
	                    if(channelNumber != null){
	                    	if(e.getChannel().getNumber() != 0) {
	                    		channelNumber.setText(Integer.toString(e.getChannel().getNumber()));
	                    	}
	                        
	                    }
	            }
	            return v;
	    }
	}
	
	
	
	
	
}

