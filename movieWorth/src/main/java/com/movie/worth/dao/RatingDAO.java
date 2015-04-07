package com.movie.worth.dao;

import java.util.HashSet;

import com.movie.worth.util.Rating;

public interface RatingDAO {
	
	public boolean newRating(Rating rating);
	
	public HashSet<Rating> getRatingsOfOneUser(int uid);
	
}
