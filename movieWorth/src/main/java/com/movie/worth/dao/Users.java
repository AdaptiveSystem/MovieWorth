package com.movie.worth.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.movie.worth.util.User;

public class Users extends JdbcDaoSupport implements UserDAO {

	public boolean newUser(User user) {
		String sql = "INSERT INTO `movielens`.`users` (`age`,`gender`,`occupation`,`zipcode`,`username`,`password`)"
				+ "VALUES(?,?,?,?,?,?)";
		int no = getJdbcTemplate().update(sql, new Object[]{
				user.getAge(),
				user.getGender(),
				user.getOccupation(),
				user.getZipcode(),
				user.getUsername(),
				user.getPassword()
			});
		if(no > 0)
			return true;
		return false;
	}

	public User getUser(int uid) {
		//sql sentence for the count operation
		String sql = "SELECT * FROM `movielens`.`users` WHERE uid = ?";
		//get the row
		User target = (User) getJdbcTemplate()
						.queryForObject(sql, new Object[]{uid}, new BeanPropertyRowMapper<User>(User.class));
		return target;
	}
	
	public User getUserByUsername(String username) {
		//sql sentence for the count operation
		String sql = "SELECT * FROM `movielens`.`users` WHERE username = ?";
		//get the row
		User target = (User) getJdbcTemplate()
						.queryForObject(sql, new Object[]{username}, new BeanPropertyRowMapper<User>(User.class));
		return target;
	}

	public List<String> getOccupationList(){
		String sql = "SELECT `occupation`.`name` FROM `movielens`.`occupation`;";
		List<String> rs = getJdbcTemplate().query(
				sql,
				new RowMapper<String>(){
				    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				        return rs.getString("name");
				    }
				});
		return rs;
	}
}
