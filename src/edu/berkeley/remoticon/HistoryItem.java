package edu.berkeley.remoticon;

import java.sql.Timestamp;

public class HistoryItem {
	private long id;
	private int channel;
	private String name;
	private long time;

	  public long getId() {
	    return id;
	  }

	  public void setId(long id) {
	    this.id = id;
	  }

	  public int getChannel() {
	    return channel;
	  }

	  public void setChannel(int c) {
	    this.channel = c;
	  }
	  
	  public String getName() {
		  return name;
	  }
	  
	  public void setName(String n)
	  {
		  this.name = n;
	  }
	  
	  public long getTime()
	  {
		  return this.time;
	  }
	  
	  public void setTime(long t)
	  {
		  this.time = t;
	  }

	  // Will be used by the ArrayAdapter in the ListView
	  @Override
	  public String toString() {
	    return ""+time + "," + name +"," + time;
	  }
	} 