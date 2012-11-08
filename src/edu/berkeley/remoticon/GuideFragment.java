package edu.berkeley.remoticon;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class GuideFragment extends Fragment {

	static final String[] numbers = new String[] { "A", "B", "C", "D", "E",
			"F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
			"S", "T", "U", "V", "W", "X", "Y", "Z" };

	private RoviApiHandler apiHandler;
	//GridView guide;
	//TableLayout guide;
	ListView guide;
	Activity activity;
	ArrayList<TVGuideEntry> listings;
	SimpleDateFormat roundedHalfHourFormat;
	GuideItemAdapter guideAdapter;
	Date startTime;
	LayoutInflater mInflater;
	Calendar calendar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			// We have different layouts, and in one of them this
			// fragment's containing frame doesn't exist. The fragment
			// may still be created from its saved state, but there is
			// no reason to try to create its view hierarchy because it
			// won't be displayed. Note this is not needed -- we could
			// just run the code below, where we would create and return
			// the view hierarchy; it would just never be used.
			return null;
		}
		return (LinearLayout) inflater.inflate(R.layout.guide_layout,
				container, false);
	}

	public void onSaveInstanceState(Bundle outState) {
		Log.e("foo", "am i being called?");
		outState.putParcelableArrayList("listings", listings);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			System.out.println(savedInstanceState
					.getParcelableArrayList("listings"));
		}
		apiHandler = new RoviApiHandler();
		activity = getActivity();
		mInflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		roundedHalfHourFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:00'Z'");
		calendar = Calendar.getInstance();
		if (activity != null) {
			guide = (ListView) activity.findViewById(R.id.guideView);
			
			
		}
		
		// check if data has already been populated before making API call
		if (listings == null) {
			ListingFetcher task = new ListingFetcher();

			Date now = new Date();
			calendar = Calendar.getInstance();
			calendar.setTime(now);
			calendar.add(Calendar.MINUTE, -(calendar.get(Calendar.MINUTE) % 30));
			startTime = calendar.getTime();
			task.execute(new String[] { "76550", roundedHalfHourFormat.format(startTime) });
		} else {
			fillGuideTable();
		}
		
		Button prevButton = (Button) activity.findViewById(R.id.prevButton);
		prevButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ListingFetcher task = new ListingFetcher();
				calendar.setTime(startTime);
				calendar.add(Calendar.HOUR, -1);
				task.execute(new String[] { "76550", roundedHalfHourFormat.format(calendar.getTime()) });
			}
		});
		Button nextButton = (Button) activity.findViewById(R.id.nextButton);
		nextButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ListingFetcher task = new ListingFetcher();
				calendar.setTime(startTime);
				calendar.add(Calendar.HOUR, 1);
				task.execute(new String[] { "76550", roundedHalfHourFormat.format(calendar.getTime()) });
			}
		});
		
	}

	private class ListingFetcher extends
			AsyncTask<String, Void, ArrayList<TVGuideEntry>> {

		@Override
		protected ArrayList<TVGuideEntry> doInBackground(String... queryInfo) {
			SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss'Z'");
			try {
				startTime = sdf.parse(queryInfo[1]);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return apiHandler.getListings(queryInfo[0], queryInfo[1]);
		}

		protected void onPostExecute(ArrayList<TVGuideEntry> result) {
			listings = result;
			
			fillGuideTable();
		}
	}
	
/*	private void fillGuideTable() {
		TableRow row;
		for(int i = 0; i < listings.size(); i++) {
			row = new TableRow(activity);
			View v = mInflater.inflate(R.layout.guide_channel_item, null);
			TVGuideEntry e = listings.get(i);
			TextView channelName = (TextView) v
					.findViewById(R.id.channelAbbrev);
			TextView channelNumber = (TextView) v
					.findViewById(R.id.channelNum);
			if (channelName != null) {
				channelName.setText(e.getChannel().getAbbr());
			}
			if (channelNumber != null) {
				if (e.getChannel().getNumber() != 0) {
					channelNumber.setText(Integer.toString(e.getChannel()
							.getNumber()));
				}
			}
			row.addView(v);
			
			ArrayList<Show> shows = e.getShows();
			v = mInflater.inflate(R.layout.guide_show_item, null);
			TextView show1Label = (TextView) v
					.findViewById(R.id.show1_label);
			TextView show2Label = (TextView) v
					.findViewById(R.id.show2_label);
			show1Label.setText(shows.get(0).getName());
			show2Label.setText(shows.get(0).getName());
			row.addView(v);
			guide.addView(row, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		}
	}
*/
	private void fillGuideTable() {
		Calendar calendar;
		TextView time1 = (TextView)activity.findViewById(R.id.time1);
		TextView time2 = (TextView)activity.findViewById(R.id.time2);
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		time1.setText(timeFormat.format(startTime));
		calendar = Calendar.getInstance();
		calendar.setTime(startTime);
		calendar.add(Calendar.MINUTE, 30);
		time2.setText(timeFormat.format(calendar.getTime()));
		
		guideAdapter = new GuideItemAdapter(activity, 0, listings, startTime);
		guide.setAdapter(guideAdapter);
	}
	
	private class GuideItemAdapter extends ArrayAdapter<TVGuideEntry> {
		private ArrayList<TVGuideEntry> items;
		private LayoutInflater mInflater;
		private Date startTime;
		Context context;
		
		private static final int TYPE_CHANNEL = 0;
		private static final int TYPE_SHOW = 1;

		public GuideItemAdapter(Context context, int textViewResourceId,
				ArrayList<TVGuideEntry> items, Date startTime) {
			super(context, textViewResourceId, items);
			mInflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.items = items;
			this.startTime = startTime;
			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ListingRowHolder holder;
			if (convertView == null) {
				holder = new ListingRowHolder();
				convertView = mInflater.inflate(R.layout.guide_item, null);
				holder.channelAbbrev = (TextView) convertView
						.findViewById(R.id.channelAbbrev);
				holder.channelNum = (TextView) convertView
						.findViewById(R.id.channelNum);
				holder.showLabels.add((TextView) convertView
						.findViewById(R.id.show1_label));
				holder.showLabels.add((TextView) convertView
						.findViewById(R.id.show2_label));
				convertView.setTag(holder);
			} else {
				holder = (ListingRowHolder)convertView.getTag();
			}
			
			
		
				TVGuideEntry e = items.get(position);
				
				holder.channelAbbrev.setText(e.getChannel().getAbbr());
				
					if (e.getChannel().getNumber() != 0) {
				holder.channelNum.setText(Integer.toString(e.getChannel()
								.getNumber()));
					}
				ArrayList<Show> shows = e.getShows();
				
				holder.showLabels.get(0).setText(shows.get(0).getName());
				if(shows.size() == 1) {
					holder.showLabels.get(1).setText(shows.get(0).getName());
				} else {
					holder.showLabels.get(1).setText(shows.get(1).getName());
				}
				
			return convertView;
		}
		
		
	}
			
	private class ListingRowHolder {
		public TextView channelAbbrev;
		public TextView channelNum;
		public ArrayList<TextView> showLabels;
		
		public ListingRowHolder() {
			super();
			showLabels = new ArrayList<TextView>();
		}
	}
	
	/*
	private class GuideItemAdapter extends ArrayAdapter<TVGuideEntry> {
		private ArrayList<TVGuideEntry> items;
		private LayoutInflater mInflater;
		private Date startTime;
		private int numColumns;
		Context context;
		
		private static final int TYPE_CHANNEL = 0;
		private static final int TYPE_SHOW = 1;

		public GuideItemAdapter(Context context, int textViewResourceId,
				ArrayList<TVGuideEntry> items, Date startTime, int numColumns) {
			super(context, textViewResourceId, items);
			mInflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.items = items;
			this.numColumns = numColumns;
			this.startTime = startTime;
			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int columnNum = position % numColumns;
			int type = getItemViewType(position);
			if (convertView == null) {
				switch(type) {
				case TYPE_CHANNEL:
					convertView = mInflater.inflate(R.layout.guide_channel_item, null);
					break;
				case TYPE_SHOW:
					convertView = mInflater.inflate(R.layout.guide_show_item, null);
					break;
				}
			}
			TVGuideEntry e = items.get(position / numColumns);
			switch(type){
			case TYPE_CHANNEL:
				TextView channelName = (TextView) convertView
						.findViewById(R.id.channelAbbrev);
				TextView channelNumber = (TextView) convertView
						.findViewById(R.id.channelNum);
				if (channelName != null) {
					channelName.setText(e.getChannel().getAbbr());
				}
				if (channelNumber != null) {
					if (e.getChannel().getNumber() != 0) {
						channelNumber.setText(Integer.toString(e.getChannel()
								.getNumber()));
					}
				}
				break;
			case TYPE_SHOW:
				TextView showLabel = (TextView) convertView
						.findViewById(R.id.show_label);
				ArrayList<Show> shows = e.getShows();
				if (shows != null) {

					if (columnNum > shows.size()) {
						if (shows.get(shows.size() - 1).getName() != null) {
							showLabel.setText(shows.get(shows.size() - 1)
									.getName());
						}

					} else {
						if (shows.get(columnNum - 1).getName() != null) {
							showLabel.setText(shows.get(columnNum - 1)
									.getName());

						}
					}
				}
				break;
			}

			return convertView;
		}
		
		@Override
		public int getItemViewType(int position) {
			if(position % numColumns == 0) {
				return TYPE_CHANNEL;
			} else {
				return TYPE_SHOW;
			}
		}
		
		public int getViewTypeCount() {
			return 2;
		}
		
		public int getCount(){
			return items.size();
		}
	}
	*/
	
	

}
