package edu.berkeley.remoticon;

import java.util.ArrayList;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;



/**
 * @author mwho
 *
 */
public class FavoritesFragment extends Fragment {
	/** (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	private final String TAG = "FavoritesFragment";
	private MenuActivity parent;
	private final int numChannels = 12;
	ArrayList<Button> favButtons;

	
	private int[] favButtonIds = {
			R.id.favorites1, R.id.favorites2, R.id.favorites3,
			R.id.favorites4, R.id.favorites5, R.id.favorites6,
			R.id.favorites7, R.id.favorites8, R.id.favorites9,
			R.id.favorites10, R.id.favorites11, R.id.favorites12
	};
	
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
		return (LinearLayout)inflater.inflate(R.layout.favorites_layout, container, false);
	}
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		parent = (MenuActivity)getActivity();
		initButtons();
		setButtonTexts();
		setClickListeners();
	}
	
	private void initButtons()
	{
		favButtons = new ArrayList<Button>();
		for (int i = 0; i < numChannels; ++i)
		{
			Button currButton = (Button) getActivity().findViewById(favButtonIds[i]);
    		favButtons.add(currButton);
		}
	}
	
	public void setButtonTexts()
	{
		for (int i = 0; i < numChannels; ++i)
		{
			setButtonText(i);
		}
	}
	
	public void setButtonText(int i )
	{
		Button b = favButtons.get(i);
		if (parent.favChannels.get(i) != null && parent.favChannels.get(i) != -1)
		{	
			b.setBackgroundResource(R.drawable.button_background);
		    String styledText = "<big> <font color='#ffffff'>"
		            + parent.favChannels.get(i) + "</font> </big>" + "<br />"
		            + "<small>" + parent.favLabels.get(i) + "</small>";
		    b.setText(Html.fromHtml(styledText));
		}
		else
		{
			b.setText("[Tap to Add]");
		}
	}
	
	private void setClickListeners()
	{
		//if button filled, click = send signal, long click = edit/delete
		//if button unfilled, click = edit
		for (int i = 0; i < numChannels; ++i)
		{
			Button currButton = favButtons.get(i);
			currButton.setOnClickListener(new ShortTapListener(i));
			currButton.setOnLongClickListener(new LongTapListener(i));
		}
	}
	private class ShortTapListener implements OnClickListener
	{
		private int myInd = -1;
		public ShortTapListener(int i)
		{
			myInd = i;
		}

		@Override
		public void onClick(View v) {
			if (parent.favChannels.get(myInd) != -1)
			{
				parent.goToChannelInfo(parent.favChannels.get(myInd), parent.favLabels.get(myInd));
			}
			else
			{
				parent.promptEditFavorite(myInd);
			}
		}
	};
	
	private class LongTapListener implements OnLongClickListener
	{
		private int myInd = -1;
		public LongTapListener(int i)
		{
			myInd = i;
		}

		@Override
		public boolean onLongClick(View v) {
			parent.promptEditFavorite(myInd);
			return true;
		}
	};
}
