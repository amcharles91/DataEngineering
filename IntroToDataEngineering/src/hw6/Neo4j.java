package hw6;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.io.fs.FileUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;




public class Neo4j {
	//creates an object of Movies
	private static Movies objArray;
	private static final String DB_PATH = "target/movies-db";
	private static GraphDatabaseService graphDb;
	
	//Creates an ArrayList of Actor Information and MovieInformation
	private static HashMap<String, ActorInfo> ActorInfo  = new HashMap<String, ActorInfo>();
	private static HashMap<String, MovieInfo> MovieInfo  = new HashMap<String, MovieInfo>();
	
	private List<Movies> movi;
	
	public List<Movies> getMovi() {
		return movi;
	}

	public void setMovi(List<Movies> movi) {
		this.movi = movi;
	}

	//Enum for labels
	enum Labels implements Label{
		Movie, Actor
	}
	//Enum for relationShipTypes
	enum relationType implements RelationshipType{
		Staring
	}
	
	//Populates a Hash Map for actor and Movies
	private static void populateHashMaps() throws JsonParseException, JsonMappingException, IOException {
		//starts object mapper
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		

		for(int m=1; m<26; m++){
			//Reads the file
			objArray = (Movies) mapper.readValue(new File("movies/page"+m+".json"), Movies.class);
			
			//Creates an array of movies from the file
			List<Movies> movieObjects = Arrays.asList(objArray);
			Neo4j objs = new Neo4j();
			objs.setMovi(movieObjects);//Sets the movie into a list
			
			
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
						//If not empty
						
							aID = objArray.getMovies().get(y).getAbridged_cast().get(x).getId();//Gets Actor Id
							Aname = objArray.getMovies().get(y).getAbridged_cast().get(x).getName();//Gets Actor Name

						    	//Checks to see if ActorInfo Already has ActordID as a key
						    	if(ActorInfo.containsKey(aID)){
						    		//System.out.println("true");
						    	}else{
						    		//Make a new ActorInfo Object and Stores Actor Id, Actor Name
						    		crew = new ActorInfo(aID.toString(),Aname.toString());
						    		//Puts object into hashmap
						    		ActorInfo.put(aID, crew);
						    		//System.out.println("false");
						    	}
						    	
						    	//See's how many characters they play
						    	 ArrayList<String> CharacterSize = objArray.getMovies().get(y).getAbridged_cast().get(x).getCharacters();
						    	 
						    	 //If it's empty does nothing
						    	for(String role:CharacterSize){
						    		//Gets the Character String at index z
						    			
						    			//Adds a character under the actor
						    			ActorInfo.get(aID).addCharacter(role);
						    			//System.out.println("Is this working?");
						    	}	    	
						    	//System.out.println("Ended character search");
						    	//Add's the movie to the actor's hashMap and clears the list for next movie
						    	ActorInfo.get(aID).addMovie(movieTitle);
					}//end else if not empty
					
				}//End Cast loop

				
			}//for loop for the movie	

		}   //for loop of movies end
		AddValues();
	}
	
	private static void AddValues() {
		Node movieNode;
		Node actorNode;
		Relationship re;
		//Actually adds the data into the database
	    //This goes through Movies
	    for (Entry<String, MovieInfo> Mkey : MovieInfo.entrySet()) {
	    	
	    	
	    		//Creates node for the selected movie
	    		movieNode = graphDb.createNode(Labels.Movie);
	    		movieNode.setProperty("title", Mkey.getValue().title);
	    		movieNode.setProperty("id", Mkey.getValue().id);
	    		movieNode.setProperty("year", Mkey.getValue().year);
	    		movieNode.setProperty("mpaa_rating", Mkey.getValue().mpaa_rating);
	    		movieNode.setProperty("audience_score", Mkey.getValue().audience_score);
	    		movieNode.setProperty("critics_score", Mkey.getValue().critics_score);
	    		
	    	    //This goes through every Actor
	    	    for (Entry<String, ActorInfo> Akey : ActorInfo.entrySet()) {
	    	    	ActorInfo actie = Akey.getValue();

	    	    	//if an actor in the hashMap plays in this movie
	    	    	if(actie.Movies.containsKey(Mkey.getValue().getTitle())){
	    	    		
	    	    		//If the database has this id already get the node
	    	    		//Else create the node to add to the database
	    	    		if((graphDb.findNode(Labels.Actor, "id", Akey.getValue().getId()) != null)){
	    	    			//Finds the node in the data base
	    	    			actorNode = graphDb.findNode(Labels.Actor, "name", Akey.getValue().getName());
	    	    		}else{
	    	    			//creates the node in the database
		    	    		actorNode = graphDb.createNode(Labels.Actor);
		    	    		actorNode.setProperty("name", actie.getName());
		    	    		actorNode.setProperty("id", actie.getId());
	    	    		}
	    	    		
	    	    		//If the arrayList of characters is not empty
	    	    		//gets the ArrayList from the hashMap with the current Key
	    	    		if(!(actie.Movies.get(Mkey.getValue().title).isEmpty())){
	    	    			//Do this to add characters for the actor
	    	    			for(String role: actie.Movies.get(Mkey.getValue().title)){
	    	    				//create a relationship with the current movie and which character
	    	    				re = actorNode.createRelationshipTo(movieNode, relationType.Staring);
	    	    				re.setProperty("Character", role);
	    	    				
	    	    			}
	    	    			
	    	    		}else{
	    	    			//Do this to just make the relationship between the movie if no characters
	    	    			re = actorNode.createRelationshipTo(movieNode, relationType.Staring);
	    	    		}
	    	    	}
	    	    }

	    }

	}

	public static void main(final String args[]) {

		
		createDb(); // creating a graph database in DB_PATH
		load(); // loading the movie data from the json files in folder "movies"
		doQueries(); // do some queries
		shutDown(); // shuts down the database
	}

	private static void shutDown() {
		// TODO Auto-generated method stub
	       System.out.println();
	        System.out.println( "Shutting down database ..." );
	        graphDb.shutdown();
	}

	
	//Loading in such a roundabout way because kept running into an error
	//When I tried adding the values while doing the populatingHashMaps 
	//with setting name property.  Worked normal but when using find node
	//gave an error. Maybe had to do a toString method or cast it String
	private static  void load() {
		// TODO Auto-generated method stub
		try(Transaction tx = graphDb.beginTx()){
			
		
			try {
				populateHashMaps();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			tx.success();
		}

		System.out.println("Loading Succesful");
	}

	private static  void createDb() {
		
		clearDb();
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( new File(DB_PATH) );
		registerShutdownHook(graphDb);
		
		
	}
	
    private static void clearDb()
    {
        try
        {
            FileUtils.deleteRecursively( new File( DB_PATH ) );
        } catch ( IOException e ){
            throw new RuntimeException( e );
        }
    }


	private static  void doQueries() {
		try(Transaction tx = graphDb.beginTx()){
			
			System.out.println("--------- Movies in 1980 (Java)-----------");
			printMoviesIn_Java(1980);
			System.out.println("\n--------- Tautou's Co-Stars (Java)-----------");
			printCoStarts_Java("Audrey Tautou");
			System.out.println("\n--------- Movies in 1980 (CQL)-----------");
			printMoviesIn_Cql(1980);
			System.out.println("\n--------- Tautou's Co-Stars (CQL)-----------");
			printCoStarts_Cql("Audrey Tautou");
			
			tx.success();
		}		
	}

	private static  void printCoStarts_Cql(String string) {
		
			//CQL query commands, match the actor name with the string go out 1 depth name all incoming Staring labels coActors and return that
			Result rs = graphDb.execute( 
					String.format("MATCH (whoever:Actor {name:\"%s\"})-"
							+ "[:Staring]->(m)<-[:Staring]-(coActors) RETURN coActors.name", string));
			//goes through the reuslt list
			while(rs.hasNext()){ 
				Map<String, Object> nzt = rs.next();
				String title = (String) nzt.get("coActors.name");
				System.out.println("\t" + title);
			}
			

		
	}

	private static void printMoviesIn_Cql(int i) {
		
			//CQL Query commands, match the Label Movie where the property year is = to i
			Result rs = graphDb.execute( String.format("match (m:Movie) where m.year = %d return m.title;", i));
			//goes through the result list
			while(rs.hasNext()){
				Map<String, Object> nzt = rs.next();
				String title = (String) nzt.get("m.title");
				System.out.println("\t" + title);
			}
			

		
		
	}

	private static void printCoStarts_Java(String string) {
		
			//Finds the node with the actor name
			Node find = graphDb.findNodes(Labels.Actor, "name", string).next();
			
			//traverse to the movie then from there check all edges
			//with an incoming edge that has the relationship
			//Staring
			Traverser traverser = graphDb.traversalDescription()
					.breadthFirst()
					.evaluator(Evaluators.toDepth(2))
					.relationships(relationType.Staring, Direction.OUTGOING)
					.relationships(relationType.Staring, Direction.INCOMING)
					.traverse(find);
			
			//Go through the results to print the costars
			for(Path path:traverser){
				if(path.length()>0)
					try{
					System.out.println("\t"+path.endNode().getProperty("name"));
					}catch (Exception e){
						//They don't have a property in place which means get out
					}
			}
			

		
	}

	private static void printMoviesIn_Java(int i) {
		
			//uses Iterator to find all nodes that meet the requirements
			ResourceIterator<Node> find = graphDb.findNodes(Labels.Movie, "year", i);
			//while loop to go through the list
			while(find.hasNext()){
				Node nxt = find.next();
				System.out.println("\t" + nxt.getProperty("title"));
				
			}
			
		
		
		
	}
	
    private static void registerShutdownHook( final GraphDatabaseService graphDb )
    {
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                graphDb.shutdown();
            }
        } );
    }
}