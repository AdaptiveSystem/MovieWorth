package com.movie.worth.util;

public class MovieUserRel {

	private Movie movie;
	private Boolean isMy;
	private int myRating;
	
	public Movie getMovie() {
		return movie;
	}
	public void setMovie(Movie movie) {
		this.movie = movie;
	}
	public Boolean getIsMy() {
		return isMy;
	}
	public void setIsMy(Boolean isMy) {
		this.isMy = isMy;
	}
	public int getMyRating() {
		return myRating;
	}
	public void setMyRating(int myRating) {
		this.myRating = myRating;
	}
	
}
