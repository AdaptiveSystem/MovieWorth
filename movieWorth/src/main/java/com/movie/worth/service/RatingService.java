package com.movie.worth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.movie.worth.dao.Ratings;
import com.movie.worth.dao.Users;
import com.movie.worth.util.Rating;
import com.movie.worth.util.User;

@Service
public class RatingService {

	@Autowired
	private Ratings ratings;
	@Autowired
	private Users users;
	
	//a feature to return the count of a user's rating
	public int getCountOfOneUser(String username){
		User curr = users.getUserByUsername(username);
		return ratings.getRatingCountOfOneUser(curr.getUid());
	}
	
	//a feature to update a rating
	public boolean updateRating(int mid, int rating, String username){
		int uid = users.getUserByUsername(username).getUid();
		Rating curr = new Rating();
		curr.setItemId(mid);
		curr.setRating(rating);
		curr.setuId(uid);
		curr.setTimeStamp(System.currentTimeMillis()/1000L);
		//first check if this user rated this movie
		if(ratings.checkIfThisUserRatedThisMovie(uid, mid)){
			return ratings.updateRating(curr);
		}else{
			return ratings.newRating(curr);
		}
	}
}
