package com.movie.worth.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.movie.worth.dao.Movies;
import com.movie.worth.dao.Ratings;
import com.movie.worth.dao.Users;
import com.movie.worth.util.AdjustedSimilarity;
import com.movie.worth.util.Movie;
import com.movie.worth.util.MovieUserRel;
import com.movie.worth.util.Rating;
import com.movie.worth.util.SlopeOne;

@Service
public class MovieService {
	
	@Autowired
	private Movies movies;
	@Autowired
	private Ratings ratings;
	@Autowired
	private Users user;
	
	@Autowired
	private AdjustedSimilarity as;
	@Autowired
	private SlopeOne so;
	
	//get all information of one movie by its id
	public Movie getMovieById(int mid){
		int totalRatingOfCurr = 0;
		Movie curr = movies.getMovie(mid);
		HashSet<Rating> allRatingsOfCurr = ratings.getRatingsOfOneMovie(mid);
		for(Rating r : allRatingsOfCurr){
			totalRatingOfCurr += r.getRating();
		}
		//System.out.println(totalRatingOfCurr + " " + allRatingsOfCurr.size());
		Float rating = (float)totalRatingOfCurr / (float)allRatingsOfCurr.size();
		curr.setRating(rating);
		return curr;
	}
	
	//get a movie's information with a certain user
	public MovieUserRel getMovieWithUser(int mid, String username){
		MovieUserRel rs = new MovieUserRel();
		rs.setMovie(getMovieById(mid));
		int uid = user.getUserByUsername(username).getUid();
		if(ratings.checkIfThisUserRatedThisMovie(uid, mid)){
			rs.setMyRating(ratings.getOneParticularRating(uid, mid).getRating());
		}else{
			rs.setMyRating(0);
		}
		return rs;
	}
	
	//get 5 movies to get user start
	public ArrayList<Movie> get5Movie(int times){
		ArrayList<Movie> rs = new ArrayList<Movie>();
		List<Integer> mids = movies.get5Movies(times);
		Iterator<?> it = mids.iterator();
		while(it.hasNext()){
			rs.add(getMovieById((Integer) it.next()));
		}
		return rs;
	}
	
	//get slope result for a user
	public ArrayList<Movie> getSlope(String username){
		int uid = user.getUserByUsername(username).getUid();
		int[] related = as.getSimUserId(uid);
		/*
		for(int j = 0; j < related.length; j++){
			System.out.println(related[j]);
		}
		*/
		int[] movieList = so.startSlopeOne(uid, related);
		ArrayList<Movie> rs = new ArrayList<Movie>();
		for(int i = 0; i < movieList.length; i++){
			rs.add(getMovieById(movieList[i]));
		}
		return rs;
	}
	
	//get search result for a user
	public HashSet<Movie> getSearchMovie(String keyword){
		
		HashSet<Movie> rs= movies.getsearchMovies(keyword);
		/*
		for(int j = 0; j < related.length; j++){
			System.out.println(related[j]);
		}
		*/
		
//		ArrayList<Movie> rs = new ArrayList<Movie>();
//		for(int i = 0; i < searched.size(); i++){
//			rs.add(getMovieById(movieList[i]));
//		}
		return rs;
	}
}
