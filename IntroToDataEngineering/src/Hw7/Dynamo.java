package Hw7;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.RangeKeyCondition;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.ConditionalOperator;
import com.amazonaws.services.dynamodbv2.model.CreateGlobalSecondaryIndexAction;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;




public class Dynamo {
	//creates an object of Movies
	private static Movies objArray;
	private static AmazonDynamoDBClient ADB;
	private static DynamoDB dynamoDB;
	static Table table;
	private static String Movies = "Movies";

	
	//Loads all the movies into database
	private static void load() throws JsonParseException, JsonMappingException, IOException {
		//starts object mapper
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		//Gets the golder and save all the names to a list
		File moviesFolder = new File("movies");
		

		//for the 25 files in the folder
		for(File json: moviesFolder.listFiles()){
			//Reads the file
			objArray = (Movies) mapper.readValue(json, Movies.class);
			try{
				int y, audience = 0, critics = 0, year = 0;// Y iterates Movie, x is iterating actor id
				String movieID = null, c, mpaa_rating = null, movieTitle = null; 
				int MovieLength = objArray.getMovies().size(); //size of movie array
				
				//For every Movie
				for(y =0; y < MovieLength; y++ ){
				    movieID = objArray.getMovies().get(y).getId();//Gets Movie ID
				    movieTitle = objArray.getMovies().get(y).getTitle();//Gets Movie title
				    audience = objArray.getMovies().get(y).getRatings().getAudience_score();//Movie audience score
				    critics = objArray.getMovies().get(y).getRatings().getCritics_score();//Movie critics score
				    year= objArray.getMovies().get(y).getYear();//Movie release year
				    mpaa_rating = objArray.getMovies().get(y).getMpaa_rating();//Gets mpaa_rating
			
			        try {//slap into the table a new item which is the movie
			            table.putItem(new Item()
			                      .withPrimaryKey("year", year, "title", movieTitle)//Primary key year and title
			                      .with("mpaa_rating", mpaa_rating)//with some attribute mpaa_rating
			                      .with("audience", audience));//with audience attribute
			            
			           // System.out.println("PutItem succeeded: " + year + " " + movieTitle);

			        } catch (Exception e) {
			            System.err.println("Unable to add movie: " + year + " " + movieTitle);
			            System.err.println(e.getMessage());
			            break;
			        }

				
			}//for loop for the movie	
			}catch(Exception e){
				System.out.println("Error");
				System.out.println(e.getMessage());
			}

		}   //for loop of movies end
	
		System.out.println("Loading Succesful");
	}
	


	public static void main(final String args[]) throws JsonParseException, JsonMappingException, IOException {
		
		createDb(); //Creates the database and Table
		load(); // loading the movie data from the json files in folder "movies"
		
		//Gets some information about the created table
		TableDescription tableDescription = 
			dynamoDB.getTable(Movies).describe();

			System.out.printf("%s: %s \t ReadCapacityUnits: %d \t WriteCapacityUnits: %d",
			    tableDescription.getTableStatus(),
			    tableDescription.getTableName(),
			    tableDescription.getProvisionedThroughput().getReadCapacityUnits(),
			    tableDescription.getProvisionedThroughput().getWriteCapacityUnits()); 
		//Query 1 pass a year and what the movie title begins with	
		Query1(2005, "The P");
		//Query 2, pass a string of the Movie Rating you want to see the average score
		Query2("PG");
		

	}
	
	private static void Query1(int year, String search) {
		//Creates a substitution hashmap can't say year directly
		HashMap<String, String> nameMap = new HashMap<String, String>();
	    nameMap.put("#yr", "year");
	    //So we have to subsitute it in
	    HashMap<String, Object> valueMap = new HashMap<String, Object>();
	    valueMap.put(":yyyy", year);
	    valueMap.put(":startWith", search);
	    
	    //itemCollection for Query's
	    ItemCollection<QueryOutcome> items = null;
	    Iterator<Item> iterator = null;
	    Item item = null;
	    
	
	    
	    //Gettig the Query ready
	    QuerySpec querySpec = new QuerySpec()
	    .withProjectionExpression(
	        "#yr, title")//projected expressions to look for
	    .withKeyConditionExpression(
	        "#yr = :yyyy and begins_with(title, :startWith)")//The condition that must be satisfied
	    .withNameMap(nameMap).withValueMap(valueMap);//adds the hash maps with subsitutions

	    try {
	    	System.out.println("\n--------- Query1-----------");
	        System.out
	        .format("Movies from %d that starts with %s \n", year, search);
	        
	        items = table.query(querySpec); //Gets a collection of items that the query returns
	        iterator = items.iterator();//Get the iterator
	        
	        while (iterator.hasNext()) {//While there's still a item
	            item = iterator.next();//Get the item and print year/title
	            System.out.println(item.getNumber("year") + ": "
	                       + item.getString("title"));
	        }

	    } catch (Exception e) {
	        System.err.println("Unable to query movies from "+ year +":");
	        System.err.println(e.getMessage());
	    }
	}

	private static void Query2(String rating) {
	    HashMap<String, Object> valueMap = new HashMap<String, Object>();
	    valueMap.put(":mpaa_rating", rating);
	    //substitution value for replace :mppaa_rating with the string passed
	    ItemCollection<QueryOutcome> items = null;
	    Iterator<Item> iterator = null;
	    Item item = null;
	    
	    
	    Index mpa = table.getIndex("rating");//Gets the index created for mpaa_rating
	    QuerySpec querySpec = new QuerySpec()
	    		.withKeyConditionExpression("mpaa_rating = :mpaa_rating")//Sure for that rating
			    .withValueMap(valueMap);

	    try {
	    	System.out.println("\n--------- Query2-----------");
	        items = mpa.query(querySpec);//get the item collection
	        int total = 0;//total of all the scores that were found
	        int count = 0;//total number of hits that meet the criteria
	        int average;//place holder for the average score
	        iterator = items.iterator();//gets iterator
	        while (iterator.hasNext()) {//while it has next
	            item = iterator.next();//get the item
	            total += item.getInt("audience");//add the score to the current number of total
	            count++;//for every item add 1
	        }
	        average = total/count;//Average Score is equals to the total number of all scores added up
	        //Divided by the number of movies that met the criteria
	        System.out.format("Average scores is %d for all %s movies", average, rating);

	    } catch (Exception e) {
	        System.err.println("Unable to query movies with "+ rating);
	        System.err.println(e.getMessage());
	    }
	    
	}

	private static  void createDb() throws JsonParseException, IOException {
		ADB = new AmazonDynamoDBClient(
				new ProfileCredentialsProvider());
		//sets end point and region
		ADB.setEndpoint("http://localhost:8000");
		ADB.setSignerRegionOverride("local");
		dynamoDB = new DynamoDB(ADB);
		//deletes table if it exists
		deleteDb();
		
		// Attribute definitions
		ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
		//Creates the attribute Int year and use as primary key
		attributeDefinitions.add(new AttributeDefinition()
		    .withAttributeName("year")
		    .withAttributeType("N"));
		//creates the attribute definition title which is sort key
		attributeDefinitions.add(new AttributeDefinition()
		    .withAttributeName("title")
		    .withAttributeType("S"));
		//Creates the attribute definition mpaa_rating whih we use as an index
		attributeDefinitions.add(new AttributeDefinition()
		    .withAttributeName("mpaa_rating")
		    .withAttributeType("S"));
		
		// Table key schema
		ArrayList<KeySchemaElement> tableKeySchema = new ArrayList<KeySchemaElement>();
		//Creates the primary partition key year
		tableKeySchema.add(new KeySchemaElement()
		    .withAttributeName("year")
		    .withKeyType(KeyType.HASH));  //Partition key
		//creates the sort key title
		tableKeySchema.add(new KeySchemaElement()
		    .withAttributeName("title")
		    .withKeyType(KeyType.RANGE));  //Sort key
		
		//Making the secondary global index
        GlobalSecondaryIndex ratingIndex = new GlobalSecondaryIndex()
        		//naming it rating
        	    .withIndexName("rating")//Name of the index being created
        	    .withProvisionedThroughput(new ProvisionedThroughput()
        	        .withReadCapacityUnits((long) 10)
        	        .withWriteCapacityUnits((long) 10))
        	        .withProjection(new Projection().withProjectionType(ProjectionType.ALL));

		//IndexKeySchema
		ArrayList<KeySchemaElement> indexKeySchema = new ArrayList<KeySchemaElement>();
		//setting it's schema elements
		//mpaa_rating as the partition key
		indexKeySchema.add(new KeySchemaElement()
		    .withAttributeName("mpaa_rating")
		    .withKeyType(KeyType.HASH));  //Partition key
		//title as the sort key
		indexKeySchema.add(new KeySchemaElement()
		    .withAttributeName("title")
		    .withKeyType(KeyType.RANGE));  //Sort key
		//sets it to the globalsecondary index
		ratingIndex.setKeySchema(indexKeySchema);
		//make a create table request with previous settings
		CreateTableRequest createTableRequest = new CreateTableRequest()
			    .withTableName(Movies)//Movie Name
			    .withProvisionedThroughput(new ProvisionedThroughput()
			        .withReadCapacityUnits((long) 10)//read capacity of 10 and same for writing
			        .withWriteCapacityUnits((long) 10))
			    .withAttributeDefinitions(attributeDefinitions)
			    .withKeySchema(tableKeySchema)
			    .withGlobalSecondaryIndexes(ratingIndex);

	    try {
	    	//try to create the table and wait for active status
	        System.out.println("Attempting to create table; please wait...");
	        table = dynamoDB.createTable(createTableRequest);

	        table.waitForActive();
	        System.out.println("Success.  Table status: " + table.getDescription().getTableStatus());
	        //sets the static variable table to the movies table for further use
	        table = dynamoDB.getTable(Movies);

	    } catch (Exception e) {
	    	//catches any errors with creating table
	        System.err.println("Unable to create table: ");
	        System.err.println(e.getMessage());
	    }
	    
		
	}
	
	private static void deleteDb() {

		try{
			//Try's to retrieve the table
			Table table = dynamoDB.getTable(Movies);
			table.delete();//if it exists no error and we proceed to delete
			table.waitForDelete();//Wait for delete
			System.out.println("TableDeleted");//confirms deletion
		}catch(Exception e){
			System.out.println("No Table Found");
		}
		
	}	










}