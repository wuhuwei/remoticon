package edu.berkeley.remoticon;

import java.util.ArrayList;

public class Show {
	String description;
	ArrayList<Airing> airings;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public ArrayList<Airing> getAirings() {
		return airings;
	}
	public void setAirings(ArrayList<Airing> airings) {
		this.airings = airings;
	}
	public Show(String description, ArrayList<Airing> airings) {
		super();
		this.description = description;
		this.airings = airings;
	}
	
	
}
