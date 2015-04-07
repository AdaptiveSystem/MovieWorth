package com.movie.worth.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import com.movie.worth.util.Rating;
import com.movie.worth.util.User;

@Component
public class SimilarityCalc extends JdbcDaoSupport implements UserDAO, RatingDAO{

	public boolean newUser(User user) {
		// TODO Auto-generated method stub
		return false;
	}

	public User getUser(int uid) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean newRating(Rating rating) {
		// TODO Auto-generated method stub
		return false;
	}

	public HashSet<Rating> getRatingsOfOneUser(int uid) {
		String sql = "SELECT `ratings`.`itemid`, `ratings`.`rating` FROM `movielens`.`ratings` WHERE uid = ?;";
		List<Rating> rs = getJdbcTemplate().query(
				sql,
				new Object[]{uid},
				new RowMapper<Rating>(){
				    public Rating mapRow(ResultSet rs, int rowNum) throws SQLException {
				        Rating rating = new Rating();
				        rating.setItemId(rs.getInt("itemid"));
				        rating.setRating(rs.getInt("rating"));
						return rating;
				    }
				});
		Set<Rating> set = new HashSet<Rating>(rs);
		return (HashSet<Rating>) set;
	}
	
	public HashSet<Rating> getRatingsOfOneMovie(int itemid) {
		String sql = "SELECT `ratings`.`uid`, `ratings`.`itemid`, `ratings`.`rating` FROM `movielens`.`ratings` WHERE itemid = ?";
		List<Rating> rs = getJdbcTemplate().query(
				sql,
				new Object[]{itemid},
				new RowMapper<Rating>(){
				    public Rating mapRow(ResultSet rs, int rowNum) throws SQLException {
				        Rating rating = new Rating();
				        rating.setuId(rs.getInt("uid"));
				        rating.setItemId(rs.getInt("itemid"));
				        rating.setRating(rs.getInt("rating"));
						return rating;
				    }
				});
		Set<Rating> set = new HashSet<Rating>(rs);
		return (HashSet<Rating>) set;
	}
	
}
