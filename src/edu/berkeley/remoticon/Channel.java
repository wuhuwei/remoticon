package edu.berkeley.remoticon;

import android.os.Parcel;
import android.os.Parcelable;

public class Channel implements Comparable<Channel>, Parcelable {
	private int id;
	private String abbr;
	private String fullName;
	private int number;
	
	public Channel(String abbr, String fullName, int number) {
		this.abbr = abbr;
		this.fullName = fullName;
		this.number = number;
	}
	
	public Channel(Parcel in) {
		readFromParcel(in);
	}
	
	public Channel() {
		super();
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAbbr() {
		return abbr;
	}
	public void setAbbr(String abbr) {
		this.abbr = abbr;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	
	public int compareTo(Channel other) {
		return this.getNumber() - other.getNumber();
	}
	
	public boolean equals(Channel other) {
		if(other == null) {
			return false;
		}
		return this.getNumber() == other.getNumber();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.getId());
		dest.writeInt(this.getNumber());
		dest.writeString(this.getAbbr());
		dest.writeString(this.getFullName());
		
	}
	
	private void readFromParcel(Parcel in) {
		setId(in.readInt());
		setNumber(in.readInt());
		setAbbr(in.readString());
		setFullName(in.readString());
	}
	
	public static final Parcelable.Creator<Channel> CREATOR = new Parcelable.Creator<Channel> () {
		public Channel createFromParcel(Parcel in) {
			return new Channel(in);
		}
		
		public Channel[] newArray(int size) {
			return new Channel[size];
		}
	};
}
