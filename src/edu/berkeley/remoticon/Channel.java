package edu.berkeley.remoticon;

public class Channel {
	private int id;
	private String abbr;
	private String fullName;
	private int number;
	
	public Channel(String abbr, String fullName, int number) {
		this.abbr = abbr;
		this.fullName = fullName;
		this.number = number;
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
	
	
}
