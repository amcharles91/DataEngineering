package hw3;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@XmlRootElement (name="Moviedata")
@XmlAccessorType(XmlAccessType.FIELD)
public class MovieMain {

	//Object for server
	static Connectx serv;
	//Start's 
	private static Movies objArray;

	@XmlElementWrapper(name = "MovieMain")
	@XmlElement(name="Movieobj")
	private List<Movies> movi;
		
	public List<Movies> getMovi() {
		return movi;
	}

	public void setMovi(List<Movies> movi) {
		this.movi = movi;
	}
	

	public static void populateActor() throws SQLException{
		int y,x; //y  is for the movies, x for the abridge cast array
		String i, a; //Parameters for inserting into database
		int MovieLength = objArray.getMovies().size(); //size of movie array
		
		//Double for Loop Iterate through the movies and for each movie get the cast info
		for(y =0; y < MovieLength; y++ ){
			int cast = objArray.getMovies().get(y).getAbridged_cast().size(); //AbridgeCast size
			for(x =0; x < cast; x++){
				if(objArray.getMovies().get(y).getAbridged_cast().isEmpty()){ //Checks if case is empty
					//it's empty
				}else{
					//Try catch block, try insert if fails means duplicate entry so leave alone
						try{
							i = objArray.getMovies().get(y).getAbridged_cast().get(x).getId();
					    	a = objArray.getMovies().get(y).getAbridged_cast().get(x).getName();
					    	//Try's the insert
					    	serv.insActor(i, a);
						}catch(Exception e){
							//Do nothing when duplicate actors are found
						}
				}
			}
		}
		
	    
	}
	
	public static void populateMovie() throws SQLException{
		int y; //For loop
		int MovieLength = objArray.getMovies().size(); //size of movie array
		//for loop to go through the movies
		for(y =0; y < MovieLength; y++ ){
				//
			    String id = objArray.getMovies().get(y).getId(); //Movie ID
			    String title = objArray.getMovies().get(y).getTitle();//Movie Title
			    int year= objArray.getMovies().get(y).getYear();//Movie release year
			    String mpprating = objArray.getMovies().get(y).getMpaa_rating();//Movie Mpaa Rating
			    int audience = objArray.getMovies().get(y).getRatings().getAudience_score();//Movie audience score
			    int critics = objArray.getMovies().get(y).getRatings().getCritics_score();//Movie critics score
			    //Try's the insert
			    try{
			    	serv.insMovie(id, title, year, mpprating, audience, critics);
			    }catch(SQLException e){
			    	//Catch any duplicate insertion errors
			    	System.out.println("Duplicate Movie");
			    }
		}
		
	    
	}

	private static void populateCharacter() throws SQLException {
		int y,x;// Y Movie, x is actor id
		String a, m, c; //Actor, Movie, Character
		int MovieLength = objArray.getMovies().size(); //size of movie array
		
		//For every Movie
		for(y =0; y < MovieLength; y++ ){
			int cast = objArray.getMovies().get(y).getAbridged_cast().size(); //AbridgeCast size
			//If there is a cast 
			for(x =0; x < cast; x++){
				
				if(objArray.getMovies().get(y).getAbridged_cast().isEmpty()){ //Checks if case is empty
					break;
					//it's empty
				}else{
						a = objArray.getMovies().get(y).getAbridged_cast().get(x).getId();//Gets Actor Id
					    m = objArray.getMovies().get(y).getId();//Gets Movie ID
					    //If it made it this far character roles are not empty
					    try{
					    	//See's how many characters they play
					    	int CharacterSize = objArray.getMovies().get(y).
					    			getAbridged_cast().get(x).getCharacters().size();
					    	//Then loops through each character they play and add to databse
					    	for(int z = 0; z<CharacterSize; z++){
					    		//Gets the Character String at index z
					    		c = objArray.getMovies().get(y).getAbridged_cast().get(x).getCharacters().get(z);
					    		//Inserts the values into the data base
					    		serv.insCharacter(a, m, c);
					    	}	    	
					    	//System.out.println("Ended character search");
						}catch(SQLException e){
							//Do nothing when duplicate actors are found
							//System.out.println("The problem names are at page movie " + y + " Abridged cast " + x);
						}
				    
				}
			}
		}
		
	}

	public static void main(String args[]) throws JAXBException,
		JsonParseException, JsonMappingException, IOException, ClassNotFoundException, SQLException{
		
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
	    //Connecct to server
	    serv = new Connectx();
	    //creates table
	    serv.create();
	    
	    //Loope which reads Each file and adds data to server
		for(int m=1; m<26; m++){
				//Reads the file
				objArray = (Movies) mapper.readValue(new File("movies/page"+m+".json"), Movies.class);
				
				//Creates an array of movies from the file
				List<Movies> movieObjects = Arrays.asList(objArray);
				MovieMain objs = new MovieMain();
				objs.setMovi(movieObjects);//Sets the movie into a list

				//First we Populate the actors into the table
			    populateActor();
			    //Then Populate the movie table
			    populateMovie();
			    //Now that actors and Movie tables are fill we can make the
			    //Character table 
			    populateCharacter();
			    
			 /* Option to uncomment if want to view xml file representation  */
			    
			    JAXBContext context = JAXBContext.newInstance(MovieMain.class);
			    Marshaller marshaller = context.createMarshaller();
			    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			    // Write to File for xml version 
			    marshaller.marshal(objs, new File("movies"+m+".xml"));
			 
		}
		
		/*Query commands to view All from Tables*/
		//Just uncomment below to view whichever table console
		//has a limit of shown characters however
		
		//total Character Size 5583
	   serv.queryCharacter();
	    //Total Actor size 3411
	//	serv.queryActor();
	    //TotalMovie size 1247
		//serv.queryMovies();
	    serv.closeConn();
	    

	    

	}

	
}
