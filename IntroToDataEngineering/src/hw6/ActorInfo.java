package hw6;

import java.util.ArrayList;
import java.util.HashMap;


public class ActorInfo {

	//Fields for Actor Id and Actor Name
	private String id, name;
	//HashMap to organize
	HashMap<String, ArrayList<String>> Movies;
	//An ArrayList to store all the Characters they've played
	private ArrayList<String> CharacterRoles;
	//Doc Field use for appending

	
	
	//Constructor, Assigns id and name and initializes characterRoles and doc
	public ActorInfo(String id, String name){
		this.id = id;
		this.name = name;
		CharacterRoles = new ArrayList<String>();
		Movies = new HashMap<String, ArrayList<String>>();

	}
	
	public HashMap<String, ArrayList<String>> getMovies() {
		return Movies;
	}

	public void setMovies(HashMap<String, ArrayList<String>> movies) {
		Movies = movies;
	}

	//Method for adding a character they played to the list
	public void addCharacter(String Character){
		//Add characters to the array list
		CharacterRoles.add(Character);
		
	}
	
	public void addMovie(String movieTitle){
		//After every movie we add the list of character
		//For that movie into a hash map
		Movies.put(movieTitle, CharacterRoles);
		//refresh the list for the next movie
		CharacterRoles = new ArrayList<String>();
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

	public ArrayList<String> getCharacterRoles() {
		return CharacterRoles;
	}

	public void setCharacterRoles(ArrayList<String> actors) {
		CharacterRoles = actors;
	}
	

	
	
	
}
