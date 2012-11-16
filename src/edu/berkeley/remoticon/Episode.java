package edu.berkeley.remoticon;

public class Episode {
	private int id;
	private int showID;
	private String title;
	private String description;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getShowID() {
		return showID;
	}
	public void setShowID(int showID) {
		this.showID = showID;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Episode(int id, int showID, String title, String description) {
		super();
		this.id = id;
		this.showID = showID;
		this.title = title;
		this.description = description;
	}
	
	
	
}
