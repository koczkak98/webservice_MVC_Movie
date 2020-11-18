package com.moviedb.webservice_MVC_Movie.user.model;

import java.util.ArrayList;

public class MovieInfo {

	private Integer userID;	
	private ArrayList<Movie> myMovies;
	
	
	public MovieInfo(Integer userID) {
		super();
		this.userID = userID;
		this.myMovies = new ArrayList<Movie>();
	}
	

	public Integer getUserID() {
		return userID;
	}

	public void setUserID(Integer userID) {
		this.userID = userID;
	}

	public ArrayList<Movie> getMyMovies() {
		return myMovies;
	}

	public void setMyMovies(ArrayList<Movie> myMovies) {
		this.myMovies = myMovies;
	}
	
	public void addMovie(Movie movie) {
		this.myMovies.add(movie);
	}
	
}
