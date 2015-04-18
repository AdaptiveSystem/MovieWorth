package com.movie.worth.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.movie.worth.dao.Users;
import com.movie.worth.util.User;

@Service
public class UserService {

	@Autowired
	private Users userManipulation;

	//a feature to create a new user
	public boolean register(User incoming){
		return userManipulation.newUser(incoming);
	}

	//a feature to return all the occupations
	public List<String> getOccupations(){
		return userManipulation.getOccupationList();
	}
	
}
