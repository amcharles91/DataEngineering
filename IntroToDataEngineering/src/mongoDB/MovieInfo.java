package mongoDB;

import java.util.ArrayList;

import org.bson.Document;

public class MovieInfo {

	//Parameters for The Movie Information
	String id, mpaa_rating, title;
	int audience_score, critics_score, year;
	ArrayList<Document> characters;
	Document doc;
	//constructor that applies all the values
	MovieInfo(String id, String mpaa_rating, int audience_score, int critics_score, String title, int year){
		characters = new ArrayList<Document>();
		this.id = id;
		this.mpaa_rating = mpaa_rating;
		this.title = title;
		this.audience_score = audience_score;
		this.critics_score = critics_score;
		this.year = year;
	}
	//Used for adding Characters to the document
	public void addCharacters(String actID, String cname){
		characters.add(new Document("actorid", actID).append("character", cname));
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
	/**
	//Used to return the appropriate Document Format for Mongo
	 **/
	public Document getDoc() {
		doc = new Document();
		doc.append("id", id);
		doc.append("mpaa_rating", mpaa_rating);
		doc.append("audience_score", audience_score);
		doc.append("critics_score", critics_score);
		doc.append("title", title);
		doc.append("year", year);
		//Passes character list to document if it's not empty
		if(!characters.isEmpty()){
			doc.append("characters", characters);
		}
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public ArrayList<Document> getCharacters() {
		
		return characters;
	}

	public void setCharacters(ArrayList<Document> characters) {
		this.characters = characters;
	}
	
	
	
}
