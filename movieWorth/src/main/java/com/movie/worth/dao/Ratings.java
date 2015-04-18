package com.movie.worth.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.movie.worth.util.Rating;

public class Ratings extends JdbcDaoSupport implements RatingDAO{

	public boolean newRating(Rating rating) {
		String sql = "INSERT INTO `movielens`.`ratings` (`uid`, `itemid`, `rating`, `timestamp`) VALUES (?, ?, ?, ?)";
		int no = getJdbcTemplate().update(sql, new Object[]{
				rating.getuId(),
				rating.getItemId(),
				rating.getRating(),
				rating.getTimeStamp(),
				});
		if(no > 0)
			return true;
		return false;
	}

	public boolean updateRating(Rating rating) {
		String sql = "UPDATE `movielens`.`ratings` SET `rating` = ?, `timestamp` = ? WHERE uid = ? AND itemid = ?";
		int no = getJdbcTemplate().update(sql, new Object[]{
				rating.getRating(),
				rating.getTimeStamp(),
				rating.getuId(),
				rating.getItemId(),
				});
		if(no > 0)
			return true;
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
	
	public int getRatingCountOfOneUser(int uid){
		String sql = "SELECT COUNT(*) FROM `movielens`.`ratings` WHERE uid = ?";
		int no = getJdbcTemplate().queryForObject(sql, new Object[]{uid}, Integer.class);
		return no;
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
	
	public Boolean checkIfThisUserRatedThisMovie(int uid, int mid){
		String sql = "SELECT COUNT(*) FROM `movielens`.`ratings` WHERE uid = ? AND itemid = ?";
		int no = getJdbcTemplate().queryForObject(sql, new Object[]{uid, mid}, Integer.class);
		if(no > 0)
			return true;
		else
			return false;
	}
	
	public Rating getOneParticularRating(int uid, int mid){
		String sql = "SELECT `ratings`.`uid`, `ratings`.`itemid`, `ratings`.`rating` FROM `movielens`.`ratings` WHERE uid = ? AND itemid = ?";
		Rating rs = (Rating) getJdbcTemplate()
				.queryForObject(sql, new Object[]{uid, mid}, new BeanPropertyRowMapper<Rating>(Rating.class));
		return rs;
	}
}
