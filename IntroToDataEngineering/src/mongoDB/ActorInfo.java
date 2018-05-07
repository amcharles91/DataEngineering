package mongoDB;

import java.util.ArrayList;

import org.bson.Document;

public class ActorInfo {

	//Fields for Actor Id and Actor Name
	private String id, name;
	//An ArrayList to store all the Characters they've played
	private ArrayList<Document> CharacterRoles;
	//Doc Field use for appending
	private Document doc;
	
	
	//Constructor, Assigns id and name and initializes characterRoles and doc
	public ActorInfo(String id, String name){
		this.id = id;
		this.name = name;
		CharacterRoles = new ArrayList<Document>();
		doc = new Document();
	}
	
	//Method for adding a character they played to the list
	public void addCharacter(String movieid, String Character, String MovieTitle ){
		CharacterRoles.add(new Document("movieid", movieid).append("character", Character).append("movieTitle", MovieTitle));
	}
	
	//Getters and Setters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Document> getCharacterRoles() {
		return CharacterRoles;
	}

	public void setCharacterRoles(ArrayList<Document> actors) {
		CharacterRoles = actors;
	}
	
	//This getter Setter you want to return the document after appending
	public Document getDoc() {
		doc.append("actorid", id );
		doc.append("name", name);
		//If the CharacterRoles are Empty Ignore
		if(!CharacterRoles.isEmpty()){
			doc.append("characters", CharacterRoles);
		}
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}
	
	
	
}
