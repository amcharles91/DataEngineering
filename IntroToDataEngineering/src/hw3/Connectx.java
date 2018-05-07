package hw3;

import java.sql.*;
import java.util.Formatter;

import javax.sql.*;

public class Connectx {
	private Connection conn;
	private Statement stat;
	private ResultSet rs;
	
	public Connectx() throws ClassNotFoundException, SQLException{
		Class.forName("org.h2.Driver");
		conn = DriverManager.getConnection(
				"jdbc:h2:" + //protocol
				System.getProperty("user.dir")+"/movies",//db file path
				"sa",//user name
				"");//Password 
		stat = conn.createStatement();
		
		
	}
	
	public void create() throws SQLException{
		
				String DropActor = "drop table if exists actor;";
				String DropMovie = "drop table if exists movie;";
				String DropCharacter = "drop table if exists character;";
				
				String actor =  "create table actor("
				+ "id varchar(100) not null unique,"
				+ "Name varchar(100),"
				+ "constraint pk_id primary key(id));";
				
				String movie = "create table movie("
				+ "id varchar(100) not null primary key,"
				+ "title varchar(100),"
				+ "year smallint unsigned,"
				+ "mpaa_rating varchar(10),"
				+ "audience_score smallint unsigned,"
				+ "critics_score smallint unsigned);";
				
				String Character =  "create table Character("
				+ "actor_id varchar(100),"
				+ "movie_id varchar(100),"
				+ "character varchar(100),"
				+ "constraint fk_actor_id foreign key (actor_id) references actor(id),"
				+ "constraint fk_movie_id foreign key (movie_id) references movie(id));"
				;
				//Add all server commands to a batch and then execute
				stat.addBatch(DropActor);
				stat.addBatch(DropMovie);
				stat.addBatch(DropCharacter);
				stat.addBatch(actor);
				stat.addBatch(movie);
				stat.addBatch(Character);
				stat.executeBatch();
	}
	
	
	public void insActor(String id, String name) throws SQLException{
		if(name.contains("'")){//If there's a ' in the string replace with 2 '
			name = name.replace("'", "''");
			stat.execute("insert into actor(id,name) values('"+ id +"','"+ name + "');");
			
		}else{
			
			stat.execute("insert into actor(id,name) values('"+ id +"','"+ name + "');");
		}
	}
	
	public void insMovie(String id, String title, int year, String mpaa_rating,
			int audience_score, int critics_score) throws SQLException{
		if(title.contains("'")){
			title = title.replace("'", "''");
			//System.out.println(title);
			
			stat.execute("insert into "
					+ "movie(id,title, year, mpaa_rating, audience_score, critics_score) "
					+ "values('"+ id +"','"+ title +"','"+ year + "',"
							+ "'"+ mpaa_rating + "','"+ audience_score + "','"+ critics_score + "');");
			//System.out.println("True at title " + title);
		}else{
			stat.execute("insert into"
					+ " movie(id, title, year, mpaa_rating, audience_score, critics_score) "
					+ "values('"+ id +"','"+ title +"','"+ year + "',"
							+ "'"+ mpaa_rating + "','"+ audience_score + "','"+ critics_score + "');");
		}
	}
	
	public void insCharacter(String actor_id, String movie_id, String character) throws SQLException {
		if(character.contains("'") ){
			character = character.replace("'", "''");
			stat.execute("insert into Character(actor_id, movie_id, character) "
					+ "values('"+ actor_id +"','"+ movie_id + "','"+ character + "');");
			
		}else{
			
			stat.execute("insert into Character(actor_id, movie_id, character) "
					+ "values('"+ actor_id +"','"+ movie_id + "','"+ character + "');");
		}
		
	}
	
	public void queryActor() throws SQLException{
		int Total = 0; //total amount of entries
		Formatter fmt = new Formatter(); 
		
		rs = stat.executeQuery("Select * FROM actor Order by name;");

		System.out.println("\n----------------------");
		System.out.format("%30s","Actors Tables");
		System.out.println("\n----------------------");
		System.out.println(fmt.format("%-10s %-15s"
				+ "", "id", "Name"));
		
		while(rs.next()){//while result set still has a row
			String id =	rs.getString("id");//get actor id
			String name = rs.getString("name");//get actor name
			fmt = new Formatter();//have to reinitialize to clear previous format
			System.out.println(fmt.format("%-10s %-150s",""+ id, name));
			Total++;
	  }
		fmt.close();//close formatter
		System.out.println("Total Entries in table are " + Total);
	}
	
	public void queryCharacter() throws SQLException{
		int Total = 0; //total number of entries
		Formatter fmt = new Formatter(); 
		
		rs = stat.executeQuery("Select * FROM character Order by movie_id;");

		System.out.println("\n----------------------");
		System.out.format("%30s","Character Tables");
		System.out.println("\n----------------------");
		System.out.println(fmt.format("%-10s %-15s %-10s"
				+ "", "Actor_ID", "Movie_ID", "Character"));
		
		while(rs.next()){
			String id =	rs.getString("actor_id");//get actor_id
			String name = rs.getString("movie_id");//get movie id
			String Character = rs.getString("Character");//get character name
			fmt = new Formatter();
			System.out.println(fmt.format("%-10s %-15s %-10s",""+ id, name, Character));
			Total++;
			
	  }
		fmt.close();//close formatter
		System.out.println("Total Entries in table are " + Total);
	}
	
	public void queryMovies() throws SQLException{
		int Total = 0; //Keeps track the total number of entries
		Formatter fmt = new Formatter(); 
		//Statement to query Movie table
		rs = stat.executeQuery("Select * FROM movie Order by title;");

		System.out.println("\n----------------------");
		System.out.format("%30s","Movie Tables");
		System.out.println("\n----------------------");
		System.out.println(fmt.format("%-10s %-70s %-10s %-10s %-15s %-15s"
				+ "", "id", "Title", "year", "mp_rating", "audience_score","critics_score"));
		
		//While there's still a row
		while(rs.next()){
			String id =	rs.getString("id");//get id
			String name = rs.getString("title");//get title
			int year = rs.getInt("year");//get year
			String mpaa_rating = rs.getString("mpaa_rating"); //rating
			int audience_score = rs.getInt("audience_score");//get audience score
			int critics_score = rs.getInt("critics_score");//get critics score
			fmt = new Formatter();//have to create a new instant of formatter for next line input
			System.out.println(fmt.format("%-10s %-70s %-10s %-10s %-15s %-15s",""
			+ id, name, year, mpaa_rating, audience_score, critics_score));
			Total++;	
	  }
		fmt.close();
		System.out.println("Total Entries in table are " + Total);
	}
	//Closes the server Connection
	public void closeConn() throws SQLException{
		conn.close();
	}



}
