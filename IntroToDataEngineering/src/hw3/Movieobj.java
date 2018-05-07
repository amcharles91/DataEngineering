package hw3;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD) 
class Movieobj {
	//Fields for the movie object
	private String 
	id,
	title,
	mpaa_rating;
	
	private int 
	year;
	
	private ratings ratings;
	
	private ArrayList<Abridged> abridged_cast;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMpaa_rating() {
		return mpaa_rating;
	}

	public void setMpaa_rating(String mpaa_rating) {
		this.mpaa_rating = mpaa_rating;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public ratings getRatings() {
		return ratings;
	}

	public void setRatings(ratings ratings) {
		this.ratings = ratings;
	}

	public ArrayList<Abridged> getAbridged_cast() {
		return abridged_cast;
	}

	public void setAbridged_cast(ArrayList<Abridged> abridged_cast) {
		this.abridged_cast = abridged_cast;
	}
	
	

}
