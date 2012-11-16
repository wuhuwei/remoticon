package edu.berkeley.remoticon;

import java.util.Date;

public class Airing {
	private Episode episode;
	private Date airingTime;
	
	public Episode getEpisode() {
		return episode;
	}
	public void setEpisode(Episode episode) {
		this.episode = episode;
	}
	public Date getAiringTime() {
		return airingTime;
	}
	public void setAiringTime(Date airingTime) {
		this.airingTime = airingTime;
	}
	public Airing(Episode episode, Date airingTime) {
		super();
		this.episode = episode;
		this.airingTime = airingTime;
	}
	
}
