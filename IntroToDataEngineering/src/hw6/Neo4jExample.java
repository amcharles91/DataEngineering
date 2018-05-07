package hw6;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.io.fs.FileUtils;


public class Neo4jExample {
    private static final String DB_PATH = "target/neo4j-example-db";
    private GraphDatabaseService graphDb;

    enum Labels implements Label{
		FACULTY, ADMINISTRATOR, CLASS, ADVISOR, STUDENT
    	
    }
    
    enum RelationshipTypes implements RelationshipType {
		TAKING, ADVISES, TEACHES, SUPERVISES
    	
    }

    public static void main( final String[] args )
    {
    	Neo4jExample example = new Neo4jExample();
    	example.createDb();
    	
    	example.classFindsStudents_Java("C++");
    	example.classFindsStudents_Cql("C++");
    	
    	example.adminFindsClass_Java();
    	example.adminFindsClass_Cql();
    	
    	example.shutDown();
    }

    /**
     * Given a class, find students that are taking it.
     * 
     * This method uses CQL.
     * 
     * @param className
     */
	void classFindsStudents_Cql(String className) {
		System.out.println("\n----- classFindsStudents_CQL -----");
		try(Transaction tx = graphDb.beginTx()){
			Result result = graphDb.execute(
					String.format("match (c:CLASS)<-[:TAKING]-(s:STUDENT) where c.title='%s' return s.name;",className));
			
			System.out.println("The following students are taking "+className+".");
			while(result.hasNext()){
				Map<String, Object> row = result.next();
				String title = (String) row.get("s.name");
				System.out.println("\t"+title);
			}
			
			tx.success();
		}

	}

	/**
	 * Given the administrator in the graph, find all the classes taught by
	 * faculty supervised by him. 
	 * 
	 * This method uses NO CQL.
	 */
	void classFindsStudents_Java(String className) {
		System.out.println("\n----- classFindsStudents_Java -----");
		try(Transaction tx = graphDb.beginTx()){
			Node classNode = graphDb.findNodes(
					Labels.CLASS, "title", className).next();
			Traverser traverser = graphDb.traversalDescription()
				.breadthFirst()
				.evaluator(Evaluators.toDepth(1))
				.relationships( RelationshipTypes.TAKING, Direction.INCOMING )
				.traverse(classNode);
			
			System.out.println("The following students are taking "+className+".");
			for(Path path:traverser){
				if(path.length()>0)
					System.out.println("\t"+path.endNode().getProperty("name"));
			}
			tx.success();
		}		
	}

    /**
     * Given a class, find students that are taking it.
     * 
     * This method uses CQL.
     * 
     * @param className
     */
	void adminFindsClass_Cql() {
		System.out.println("\n----- adminFindsClass_Cql -----");
		try(Transaction tx = graphDb.beginTx()){
			Result result = graphDb.execute(
					"match (a:ADMINISTRATOR)-[*1..2]->(c:CLASS) where a.name='Jim' return c.title;");
			
			System.out.println("Courses administered by Jim");
			while(result.hasNext()){
				Map<String, Object> row = result.next();
				String title = (String) row.get("c.title");
				System.out.println("\t"+title);
			}
			
			tx.success();
		}
	}


	/**
	 * Given the administrator in the graph, find all the classes taught by
	 * faculty supervised by him. 
	 * 
	 * This method uses NO CQL.
	 */
	void adminFindsClass_Java() {
		System.out.println("\n----- adminFindsClass_Java -----");
		try(Transaction tx = graphDb.beginTx()){
			Node admin = graphDb.findNodes(
					Labels.ADMINISTRATOR, "name", "Jim").next();
			Traverser traverser = graphDb.traversalDescription()
				.breadthFirst()
				.evaluator(Evaluators.toDepth(2))
				.relationships( RelationshipTypes.TEACHES, Direction.OUTGOING )
				.relationships( RelationshipTypes.SUPERVISES, Direction.OUTGOING )
				.traverse(admin);
			
			System.out.println("Courses administered by Jim");
			for(Path path:traverser){
				if(path.length()>0 && path.endNode().hasLabel(Labels.CLASS)){
					System.out.println("\t"+path.endNode().getProperty("title"));
				}
			}
			tx.success();
		}				
	}

	/**
	 * Creates a graph database with some artificial data
	 */
	void createDb()
    {
        clearDb();
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( new File(DB_PATH) );
        registerShutdownHook( graphDb );

        // starting a transaction
        try ( Transaction tx = graphDb.beginTx() )
        {
        	Node dahaiMcHands = graphDb.createNode(Labels.FACULTY);
        	dahaiMcHands.setProperty("name", "Dahai");
        	dahaiMcHands.setProperty("rank", "associate");

        	Node anna = graphDb.createNode(Labels.FACULTY);
        	anna.setProperty("name", "anna");
        	anna.setProperty("rank", "assistant");
        	
        	Node jim = graphDb.createNode(Labels.ADMINISTRATOR);
        	jim.setProperty("name", "Jim");
        	jim.setProperty("dept", "software");

        	Node java = graphDb.createNode(Labels.CLASS);
        	java.setProperty("title", "Java");
        	java.setProperty("classroom", "402");

        	Node cpp = graphDb.createNode(Labels.CLASS);
        	cpp.setProperty("title", "C++");
        	cpp.setProperty("classroom", "202");

        	Node jane = graphDb.createNode(Labels.ADVISOR);
        	jane.setProperty("name", "Jane");
        	jane.setProperty("rank", "senior");

        	Node joe = graphDb.createNode(Labels.STUDENT);
        	joe.setProperty("name", "Joe");
        	joe.setProperty("year", "junior");

        	Node jerry = graphDb.createNode(Labels.STUDENT);
        	jerry.setProperty("name", "Jerry");
        	jerry.setProperty("year", "senior");

            Relationship relationship = jerry.createRelationshipTo( cpp, RelationshipTypes.TAKING );
            relationship.setProperty( "grade", "A" );
            
            relationship = joe.createRelationshipTo( cpp, RelationshipTypes.TAKING );
            relationship.setProperty( "grade", "B" );
            
            relationship = joe.createRelationshipTo( java, RelationshipTypes.TAKING );
            relationship.setProperty( "grade", "C" );

            relationship = jane.createRelationshipTo( jerry, RelationshipTypes.ADVISES );
            relationship.setProperty( "appt_date", "monday" );

            relationship = jane.createRelationshipTo( joe, RelationshipTypes.ADVISES );
            relationship.setProperty( "appt_date", "tuesday" );
            
            relationship = dahaiMcHands.createRelationshipTo( java, RelationshipTypes.TEACHES );
            relationship.setProperty( "textbook", "Complete Java" );
            
            relationship = anna.createRelationshipTo( cpp, RelationshipTypes.TEACHES );
            relationship.setProperty( "textbook", "Complete C++" );
                        
            relationship = jim.createRelationshipTo( dahaiMcHands, RelationshipTypes.SUPERVISES );
            relationship = jim.createRelationshipTo( anna, RelationshipTypes.SUPERVISES );
            
            tx.success();
        }

    }

    private void clearDb()
    {
        try
        {
            FileUtils.deleteRecursively( new File( DB_PATH ) );
        } catch ( IOException e ){
            throw new RuntimeException( e );
        }
    }

    /**
     * Shuts down the database.
     */
    void shutDown()
    {
        System.out.println();
        System.out.println( "Shutting down database ..." );
        graphDb.shutdown();
    }

	/**
	 * Shuts down the databse when the JVM is exiting
	 * @param graphDb
	 */
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
