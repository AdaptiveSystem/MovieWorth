package com.movie.worth.dao;

import com.movie.worth.util.User;

public interface UserDAO {

	public boolean newUser(User user);
	
	public User getUser(int uid);

}
