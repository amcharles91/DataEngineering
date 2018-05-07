package hw3;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlAccessorType(XmlAccessType.FIELD)
 class Abridged {
	//Fields for AbridgedCast
	private String 
	name,
	id;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}
	
	//Put Characters they play into an array list
	@XmlElementWrapper(name = "Roles")
	@XmlElement(name = "role")
	private ArrayList<String> characters = new ArrayList<String>();

	public ArrayList<String> getCharacters() {
		return characters;
	}

	public void setCharacters(ArrayList<String> characters) {
		this.characters = characters;
	}
		
	
}
