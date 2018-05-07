package mongoDB;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.plaf.synth.SynthSplitPaneUI;

import org.bson.Document;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;


public class MongoDataBaseMain {
		
	//creates an object of Movies
	private static Movies objArray;
	private static MongoClient mongoClient;//MongoClient Field
	private static MongoDatabase db;//database holder field
	private static MongoCollection<Document> actors;//actor documents
	private static MongoCollection<Document> movies;//movies documents
	
	//Creates an ArrayList of Actor Information and MovieInformation
	private static HashMap<String, ActorInfo> ActorInfo  = new HashMap<String, ActorInfo>();
	private static HashMap<String, MovieInfo> MovieInfo  = new HashMap<String, MovieInfo>();

	//Creates a document to store movies and one for actors
	private static List<Document> mdoc = new ArrayList<Document>();
	private static List<Document> adoc = new ArrayList<Document>();

	//Creates a List to put all the movies into
	private List<Movies> movi;
		
	public List<Movies> getMovi() {
		return movi;
	}

	public void setMovi(List<Movies> movi) {
		this.movi = movi;
	}
	
	private static void AddValues(){
		//Populates the Docuents with values from both HashMaps
		//This goes through Actor
	    for (Entry<String, ActorInfo> Akey : ActorInfo.entrySet()) {
	    	adoc.add(Akey.getValue().getDoc());
	    }
	    //This goes through Movies
	    for (Entry<String, MovieInfo> Mkey : MovieInfo.entrySet()) {
	    	mdoc.add(Mkey.getValue().getDoc());
	    }
	}

	//Method that populates all the actors and movies in Hashmaps then add them to the documents
	private static void populateCollections() {
		ActorInfo crew =null; //ActorInfo Object for inserting
		MovieInfo Squad =null;//MovieInfo Object for inserting
		int y,x, audience = 0, critics = 0, year = 0;// Y iterates Movie, x is iterating actor id
		String aID, movieID = null, c, mpaa_rating = null, movieTitle = null, Aname; 
		int MovieLength = objArray.getMovies().size(); //size of movie array
		
		//For every Movie
		for(y =0; y < MovieLength; y++ ){
		    movieID = objArray.getMovies().get(y).getId();//Gets Movie ID
		    movieTitle = objArray.getMovies().get(y).getTitle();//Gets Movie title
		    audience = objArray.getMovies().get(y).getRatings().getAudience_score();//Movie audience score
		    critics = objArray.getMovies().get(y).getRatings().getCritics_score();//Movie critics score
		    year= objArray.getMovies().get(y).getYear();//Movie release year
		    mpaa_rating = objArray.getMovies().get(y).getMpaa_rating();//Gets mpaa_rating
		    
		    
			int cast = objArray.getMovies().get(y).getAbridged_cast().size(); //AbridgeCast size
			//Checks to see if MovieID Exists
			if(MovieInfo.containsKey(movieID)){
				//Do Nothing
			}else{
				//Create a Movie object 
				Squad = new MovieInfo(movieID, mpaa_rating, audience, critics, movieTitle, year);
				MovieInfo.put(movieID, Squad);//puts the new object into HashMap with movie id as key
			}
			
			//If there is a cast 
			for(x =0; x < cast; x++){
				
				if(objArray.getMovies().get(y).getAbridged_cast().isEmpty()){ //Checks if case is empty
					break;
					//it's empty
				}else{
					
					
						aID = objArray.getMovies().get(y).getAbridged_cast().get(x).getId();//Gets Actor Id
						Aname = objArray.getMovies().get(y).getAbridged_cast().get(x).getName();//Gets Actor Name

					    //If it made it this far character roles are not empty

					    	//Checks to see if ActorInfo Already has ActordID as a key
					    	if(ActorInfo.containsKey(aID)){
					    		//System.out.println("true");
					    	}else{
					    		//Make a new ActorInfo Object and Stores Actor Id, Actor Name
					    		crew = new ActorInfo(aID,Aname);
					    		//Puts object into hashmap
					    		ActorInfo.put(aID, crew);
					    		//System.out.println("false");
					    	}
					    	
					    	//See's how many characters they play
					    	int CharacterSize = objArray.getMovies().get(y).getAbridged_cast().get(x).getCharacters().size();

					    	for(int z = 0; z<CharacterSize; z++){
					    		//Gets the Character String at index z
					    			c = objArray.getMovies().get(y).getAbridged_cast().get(x).getCharacters().get(z);
					    			//Adds a character under the actor
					    			ActorInfo.get(aID).addCharacter(movieID, c, movieTitle);
					    			//Adds a character to the Movies Character list
					    			MovieInfo.get(movieID).addCharacters(aID, c);
					    	}	    	
					    	//System.out.println("Ended character search");

				}
				
			}

			
		}
		
	}
	
	//Method to see if a collectionExists
	private static boolean collectionExists(MongoDatabase data, String col){
		//Checks to see if the collection Exists by using an arraylist
		//returns true or false
		return data.listCollectionNames().into(new ArrayList<String>()).contains(col);
		
	}

	private static void VilQuery(){
		//For Query 2
		ArrayList<String> idHolder = new ArrayList<String>();
		System.out.println("Tom Hank's coStars");
		MongoCursor<Document> tomID = actors.find(Filters.eq("name","Tom Hanks")).iterator();
		while(tomID.hasNext()){
			Document FindActor = tomID.next();
			String placeHolderID = FindActor.getString("actorid");
			System.out.println(placeHolderID);
			MongoCursor<Document> CoStars = movies.find(Filters.eq("characters.actorid",placeHolderID)).iterator();
			while(CoStars.hasNext()){
				Document coActorId = CoStars.next();
			
				ArrayList<Document> AllDemStars = (ArrayList<Document>) coActorId.get("characters");
				for(Document idsearch: AllDemStars){
					String ThereId = idsearch.getString("actorid");
					
					if(!ThereId.equals(FindActor) && !idHolder.contains(ThereId)){									
						MongoCursor<Document> actornames = actors.find(Filters.eq("actorid", ThereId)).iterator();
						while(actornames.hasNext()){
							Document TeamList = actornames.next();
							idHolder.add(ThereId);
							System.out.println(TeamList.get("name") +" in " +coActorId.get("title") );
						}
					}
				}
			}
		}
	}
	//Method to Query for actor
	private static void QueryMoviesPlayed(String Actor, boolean costars) {
		String actid = null;//PlaceHolder for ActorID
		
		//Array List used saving movie titles
		ArrayList<String> Query= new ArrayList<String>();
		//Array List used for saving actor CoStars //used to SortList
		ArrayList<String> Query2= new ArrayList<String>();
		//Array for gaining all of the CoStars actorid's 
		ArrayList<String> Mo = new ArrayList<String>();
		//HashMap to store all the movie titles as key and a list of Actor Id's for that movie
		HashMap<String, ArrayList<String>> actorQueries = new HashMap<String, ArrayList<String>>();
		
		//creates a document of just the Actor Searched for
		MongoCursor<Document> dat = actors.find(Filters.eq("name", Actor)).iterator();
		while(dat.hasNext()){
			//While Loop that goes through the document list
			Document tomas = dat.next();
			//Assigns actid to the Actor were searching for
			actid = tomas.getString("actorid");
			//System.out.format("Is this %s id?  %s\n", Actor, actid);
			
			//Creates another list of that actors character list
			@SuppressWarnings("unchecked")
			List<Document> tomasRoles = (List<Document>) tomas.get("characters");
			
			//Checks to see if we are printing the CoStars or movie played in
			if(!costars){
				System.out.format("%s %s has played in \n", Actor, Actor);
			}
			//For loop that goes through the list of characters the actor played and what movie
			for (Document d: tomasRoles){
				if(!costars){
					System.out.format(" \"%s\"  in \"%s\"\n", d.get("character"),d.get("movieTitle"));
				}
				//Adds the movie title to the list
				if(!Query.contains(d.get("movieTitle"))){
					Query.add((String) d.get("movieTitle"));
				}
			}
		}
		//For Spacing
		System.out.println("");

		
		//If CoStars is True we find their CoStars
		if(costars){
			//For loop for going through the movie title list
			for(String dalist : Query){
				//We query that movie title
				MongoCursor<Document> playWith = movies.find(Filters.eq("title", dalist)).iterator();
				while(playWith.hasNext()){
					Document poc = playWith.next();
					//For every movie we access the characters
					@SuppressWarnings("unchecked")
					List<Document> movieeess = (List<Document>) poc.get("characters");
					
					//System.out.println("Movie titles are " + dalist);
					//Iterate through the characters
					for (Document d: movieeess){
						//System.out.println("   " + d.get("character") + " in " + d.get("actorid"));
						//If the actor id is the same as who were searching for
						if(d.getString("actorid").equals(actid)){
							//do nothing because we don't need it
						}else{
							//If it's different we want to add it
							if(!Mo.contains(d.getString("actorid"))){
								Mo.add((String)d.getString("actorid"));
							}	
						}
					}
					//Now we add the movie as key and list of characters
					//into the hash map
					actorQueries.put(dalist, Mo);
					BasicDBObject inQuery = new BasicDBObject();
					inQuery.put("actorid", new BasicDBObject("$in", actorQueries.get(dalist)));
					
					MongoCursor<Document> cursor = actors.find(inQuery).iterator();
					while(cursor.hasNext()){
						Document acties = cursor.next();
						System.out.format("   %-25s \t in \t %s \n" ,acties.get("name"), dalist);
					}
					
					//Create a new list for the next set of characters
					Mo = new ArrayList<String>();
				}
			}
			

		}
	}

	
	//Main
	public static void main(String args[]) throws JsonParseException, JsonMappingException, IOException {
		
		//starts object mapper
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		//Server Connection to Mongo
		mongoClient = new MongoClient("localhost", 27017);
		
		// creating/finding a database named "Movies"
		db = mongoClient.getDatabase("Movies");

		
		//First checks if databases exists and then drops/creates them
		if(collectionExists(db, "movies") || collectionExists(db, "actors")){
			//System.out.println("The Collection is here of course");
		    db.getCollection("movies").drop();
		    db.getCollection("actors").drop();
		}

		//Creates collections
		movies = db.getCollection("movies");
		actors = db.getCollection("actors");
	
		
	    //Loop which reads Each file and adds data to server
		for(int m=1; m<26; m++){
				//Reads the file
				objArray = (Movies) mapper.readValue(new File("movies/page"+m+".json"), Movies.class);
				
				//Creates an array of movies from the file
				List<Movies> movieObjects = Arrays.asList(objArray);
				MongoDataBaseMain objs = new MongoDataBaseMain();
				objs.setMovi(movieObjects);//Sets the movie into a list

				//Populates the databases Collections
			    populateCollections(); 
			   
		}     		
		
		//Method populates the docuents
		AddValues();
		//Adds the values to MonogoDatabase from the documents
		movies.insertMany(mdoc);
		actors.insertMany(adoc);	
		
		
		//Type which Actor you want to search for
		String ActorName = "Tom Hanks";
		//Query1
		//Finds all the movies this actor plays in 
		//must pass false
		//QueryMoviesPlayed(ActorName, false);
		//Query 2
		//Finds all the CoStars of this actor
		//must pass true
		QueryMoviesPlayed(ActorName, true);
		
		//closes the database
		mongoClient.close();

	}


}
