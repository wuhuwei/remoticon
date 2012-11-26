package edu.berkeley.remoticon;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShowFragment extends Fragment {
	Program show;
	Channel channel;
	Show info;

	MenuActivity activity;
	TextView showTitle;
	TextView networkTitle;
	TextView showDescription;
	ImageView showImage;
	ExpandableListView airingsView;
	
	ProgressDialog loader;
	
	
	public ShowFragment() {
		super();
	}
	
	public ShowFragment(Channel c, Program s) {
		super();
		channel = c;
		show = s;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		return (LinearLayout) inflater.inflate(R.layout.show_layout, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		activity = (MenuActivity) getActivity();
		airingsView = (ExpandableListView) activity.findViewById(R.id.airingsList);
		showTitle = (TextView) activity.findViewById(R.id.showTitle);
		showTitle.setText(show.getName() + " " + show.getId());

		showDescription = (TextView) activity
				.findViewById(R.id.showDescription);
		showDescription.setVisibility(View.GONE);
		
		networkTitle = (TextView) activity.findViewById(R.id.networkTitle);
		networkTitle.setText(channel.getFullName() + " (" + channel.getAbbr() + ")");
		
		// TODO: fetch show images?
		showImage = (ImageView) activity.findViewById(R.id.showImage);
		showImage.setVisibility(View.GONE);
		
		loader = new ProgressDialog(activity);

		ShowInfoFetcher task = new ShowInfoFetcher();
		task.execute(new String[] { "76550", Integer.toString(show.getId()) });
	}

	private class ShowInfoFetcher extends AsyncTask<String, Void, Show> {
		protected void onPreExecute() {
			loader.setMessage("Loading...");
			loader.show();
		}
		@Override
		protected Show doInBackground(String... queryInfo) {
			return activity.getApiHandler().getShow(queryInfo[0], queryInfo[1]);
		}

		protected void onPostExecute(Show result) {
			info = result;
			populateView();
		}
	}
	
	private void populateView() {
		if(info != null) {
			showDescription.setText(info.getDescription());
			showDescription.setVisibility(View.VISIBLE);
			
			airingsView.setAdapter(new NextAiringsListAdapter(activity, info.getAirings()));
		}
		loader.dismiss();
	}
	
	private class NextAiringsListAdapter extends BaseExpandableListAdapter {
		private LayoutInflater mInflater;
		private ArrayList<Airing> airings;
		
		public NextAiringsListAdapter(Context context, ArrayList<Airing> airings) {
			this.airings = airings;
			this.mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public Object getGroup(int groupPosition) {
			return airings.get(groupPosition);
		}
		
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return airings.get(groupPosition);
		}
		
		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}
		
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return airings.size();
		}
		
		@Override
		public int getChildrenCount(int groupPosition) {
			return 1;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			if(convertView == null) {
				convertView = mInflater.inflate(R.layout.airing_parent, parent, false);
				
			}
			TextView airingTime = (TextView) convertView.findViewById(R.id.showTime);
			airingTime.setText(airings.get(groupPosition).getAiringTime().toString());
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			if(convertView == null) {
				convertView = mInflater.inflate(R.layout.airing_details, parent, false);
				
			}
			TextView episodeTitle = (TextView) convertView.findViewById(R.id.title);
			TextView episodeDescription = (TextView) convertView.findViewById(R.id.description);
			episodeTitle.setText(airings.get(groupPosition).getEpisode().getTitle());
			episodeDescription.setText(airings.get(groupPosition).getEpisode().getDescription());
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}
		
	}

}
