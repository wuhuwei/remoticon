package edu.berkeley.remoticon;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class Program implements Parcelable {
	private int id;
	private String name;
	private String episodeTitle;
	private String description;
	private String category;
	private String subcategory;
	private String rating;
	private int duration;
	private Date airingTime;
	
	public Program(int id, String name, String episodeTitle, String description, String category, String subcategory, String rating, int duration, Date airingTime) {
		this.id = id;
		this.name = name;
		this.episodeTitle = episodeTitle;
		this.category = category;
		this.subcategory = subcategory;
		this.rating = rating;
		this.duration = duration;
		this.airingTime = airingTime;
	}
	
	public Program(Parcel in) {
		readFromParcel(in);
	}
	
	public Program() {
		super();
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEpisodeTitle() {
		return episodeTitle;
	}

	public void setEpisodeTitle(String episodeTitle) {
		this.episodeTitle = episodeTitle;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSubcategory() {
		return subcategory;
	}

	public void setSubcategory(String subcategory) {
		this.subcategory = subcategory;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public Date getAiringTime() {
		return airingTime;
	}
	
	public void setAiringTime(Date airingTime) {
		this.airingTime = airingTime;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.getId());
		dest.writeString(this.getCategory());
		dest.writeString(this.getSubcategory());
		dest.writeString(this.getEpisodeTitle());
		dest.writeString(this.getName());
		dest.writeString(this.getRating());
		dest.writeInt(this.getDuration());
		dest.writeSerializable(this.getAiringTime());
		
	}
	
	private void readFromParcel(Parcel in) {
		setId(in.readInt());
		setCategory(in.readString());
		setSubcategory(in.readString());
		setEpisodeTitle(in.readString());
		setName(in.readString());
		setRating(in.readString());
		setDuration(in.readInt());
		setAiringTime((Date)in.readSerializable());
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
