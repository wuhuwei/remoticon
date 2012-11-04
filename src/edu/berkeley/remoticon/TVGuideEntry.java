package edu.berkeley.remoticon;

import java.util.Date;

public class TVGuideEntry {
	private Channel channel;
	private Show show;
	private Date airingTime;
	
	public TVGuideEntry(Channel channel, Show show, Date airingTime) {
		this.channel = channel;
		this.show = show;
		this.airingTime = airingTime;
	}
	
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	public Show getShow() {
		return show;
	}
	
	public void setShow(Show show) {
		this.show = show;
	}

	public Date getAiringTime() {
		return airingTime;
	}

	public void setAiringTime(Date airingTime) {
		this.airingTime = airingTime;
	}
	
}
