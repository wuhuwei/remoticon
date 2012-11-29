package edu.berkeley.remoticon;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ExploreFragment extends Fragment {
	/** (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	ListView historyList;
	HistoryItemAdapter historyAdapter;
	MenuActivity activity;
	LayoutInflater mInflater;
	
	Button clearButton;

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
		return (LinearLayout)inflater.inflate(R.layout.explore_layout, container, false);
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		activity = (MenuActivity) getActivity();
		mInflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		historyAdapter = new HistoryItemAdapter(activity, activity.allHistoryItems);
		if (activity != null) {
			historyList = (ListView) activity.findViewById(R.id.history_listview);
			historyList.setAdapter(historyAdapter);
		}
		clearButton = (Button) activity.findViewById(R.id.history_clear);
		clearButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.clearHistory();
				historyAdapter.clear();
				historyAdapter.notifyDataSetChanged();
			}
		});
	}
	
	
	private class HistoryItemAdapter extends ArrayAdapter<HistoryItem> {
		private LayoutInflater mInflater;
		Context context;

		public HistoryItemAdapter(Context context, List<HistoryItem> items) {
			super(context, 0);
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.context = context;
			for (int i = items.size()-1; i >= 0; --i)
			{
				this.add(items.get(i));
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.history_item, null);
			}
			HistoryItem hi = this.getItem(position);
			
			TextView channel = (TextView) convertView.findViewById(R.id.history_item_channel);
			TextView info = (TextView) convertView.findViewById(R.id.history_item_info);
			TextView time = (TextView) convertView.findViewById(R.id.history_item_time);
			channel.setText(""+hi.getChannel());
			info.setText(hi.getName());
			
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm");
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(hi.getTime());
			time.setText(formatter.format(calendar.getTime()));
			return convertView;
		}

	}

}
