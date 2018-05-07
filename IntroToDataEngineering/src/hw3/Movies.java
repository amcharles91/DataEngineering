package hw3;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlAccessorType(XmlAccessType.FIELD)
public class Movies {

	private int total;
	
	
	public int getTotal() {
		return total;
	}
	
	public void setTotal(int total) {
		this.total = total;
	}
	
	@XmlElementWrapper(name = "MovieArray")
	private ArrayList<Movieobj> movies;
	
	public ArrayList<Movieobj> getMovies() {
		return movies;
	}

	public void setMovies(ArrayList<Movieobj> movies) {
		this.movies = movies;
	}

}
