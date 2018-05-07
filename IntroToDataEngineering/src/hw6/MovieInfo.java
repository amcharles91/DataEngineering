package hw6;

import java.util.ArrayList;

import org.bson.Document;

public class MovieInfo {

	//Parameters for The Movie Information
	String id, mpaa_rating, title;
	int audience_score, critics_score, year;


	//constructor that applies all the values
	MovieInfo(String id, String mpaa_rating, int audience_score, int critics_score, String title, int year){
		this.id = id;
		this.mpaa_rating = mpaa_rating;
		this.title = title;
		this.audience_score = audience_score;
		this.critics_score = critics_score;
		this.year = year;
	}
	
	
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMpaa_rating() {
		return mpaa_rating;
	}

	public void setMpaa_rating(String mpaa_rating) {
		this.mpaa_rating = mpaa_rating;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getAudience_score() {
		return audience_score;
	}

	public void setAudience_score(int audience_score) {
		this.audience_score = audience_score;
	}

	public int getCritics_score() {
		return critics_score;
	}

	public void setCritics_score(int critics_score) {
		this.critics_score = critics_score;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	
	
}
