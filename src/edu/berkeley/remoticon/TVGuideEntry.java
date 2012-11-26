package edu.berkeley.remoticon;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class TVGuideEntry implements Comparable<TVGuideEntry>, Parcelable{
	private Channel channel;
	private ArrayList<Program> shows;
	
	public TVGuideEntry(Channel channel, ArrayList<Program> shows) {
		this.channel = channel;
		this.shows = shows;
	
	}
	
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	public ArrayList<Program> getShows() {
		return shows;
	}
	
	public void setShows(ArrayList<Program> shows) {
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
		in.readList(shows, Program.class.getClassLoader());
		
	}
	
	public static final Parcelable.Creator<Program> CREATOR = new Parcelable.Creator<Program> () {
		public Program createFromParcel(Parcel in) {
			return new Program(in);
		}
		
		public Program[] newArray(int size) {
			return new Program[size];
		}
	};
	
	
	
}
