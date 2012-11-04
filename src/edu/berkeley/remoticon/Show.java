package edu.berkeley.remoticon;

public class Show {
	private int id;
	private String name;
	private String episodeTitle;
	private String description;
	private String category;
	private String subcategory;
	private String rating;
	
	
	public Show(int id, String name, String episodeTitle, String description, String category, String subcategory, String rating) {
		this.id = id;
		this.name = name;
		this.episodeTitle = episodeTitle;
		this.description = description;
		this.category = category;
		this.subcategory = subcategory;
		this.rating = rating;
	}
	
	public Show() {
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	
	
}
