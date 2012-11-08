package edu.berkeley.remoticon;

import java.util.ArrayList;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class TVGuideEntry implements Comparable<TVGuideEntry>, Parcelable{
	private Channel channel;
	private ArrayList<Show> shows;
	
	public TVGuideEntry(Channel channel, ArrayList<Show> shows) {
		this.channel = channel;
		this.shows = shows;
	
	}
	
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	public ArrayList<Show> getShows() {
		return shows;
	}
	
	public void setShows(ArrayList<Show> shows) {
		this.shows = shows;
	}

	public int compareTo(TVGuideEntry other) {
		
		return this.getChannel().compareTo(other.getChannel());
		
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(this.getChannel(), 0);
		dest.writeList(this.getShows());
	}
	
	public void readFromParcel(Parcel in) {
		this.setChannel((Channel)in.readParcelable(Channel.class.getClassLoader()));
		in.readList(shows, Show.class.getClassLoader());
		
	}
	
	public static final Parcelable.Creator<Show> CREATOR = new Parcelable.Creator<Show> () {
		public Show createFromParcel(Parcel in) {
			return new Show(in);
		}
		
		public Show[] newArray(int size) {
			return new Show[size];
		}
	};
	
	
	
}
