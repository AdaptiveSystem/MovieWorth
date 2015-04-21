package com.movie.worth.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.movie.worth.util.Movie;

public class Movies extends JdbcDaoSupport implements MovieDAO {
	
	private static final String[] GENRES = {"unknown", "Action", "Adventure", "Animation", "Children_s",
			"Comedy", "Crime", "Documentary", "Drama", "Fantasy", "Film_Noir", "Horror", "Musical", 
			"Mystery", "Romance", "Sci_Fi", "Thriller", "War", "Western"};

	//get one movie by it's mid
	public Movie getMovie(int mid) {
		String sql = "SELECT * FROM `movielens`.`items` WHERE mid = ?";
		Movie rs = (Movie) getJdbcTemplate()
				.queryForObject(sql, new Object[]{mid}, new BeanPropertyRowMapper<Movie>(){
					public Movie mapRow(ResultSet rs, int rowNum) throws SQLException {
						Movie target = new Movie();
						target.setMid(rs.getInt("mid"));
						target.setTitle(rs.getString("mtitle"));
						target.setReleaseDate(rs.getString("release_date"));
						target.setImdbURL(rs.getString("IMDb_URL"));
						ArrayList<String> genres = new ArrayList<String>();
						for(int i = 0; i < GENRES.length; i++){
							if(rs.getInt(GENRES[i].toString()) == 1){
								genres.add(GENRES[i].toString());
							}
						}
						target.setGenre(genres);
						return target;
					}
				});
		return rs;
	}
	
	//get 5 movie ids from the database
	public List<Integer> get5Movies(int times){
		int limit = 10;
		int offset = times * limit;
		String sql = "SELECT itemid, COUNT(itemid) AS count FROM `movielens`.`ratings` GROUP BY itemid ORDER BY count DESC LIMIT ? OFFSET ?;";
		List<Integer> rs = getJdbcTemplate().query(
				sql,
				new Object[]{limit, offset},
				new RowMapper<Integer>(){
					public Integer mapRow(ResultSet rs, int rowNum) throws SQLException{
						return rs.getInt("itemid");
					}
				});
		return rs;
	}
	
	public HashSet<Movie> getsearchMovies(String likename) {
		String sql = "Select * from `movielens`.`items` Where mtitle like '%"+likename+"%' limit 10";
		List<Movie> rs = getJdbcTemplate().query(
				sql,
				new RowMapper<Movie>(){
					public Movie mapRow(ResultSet rs, int rowNum) throws SQLException{
				    	Movie target = new Movie();
				    	target.setMid(rs.getInt("mid"));
				    	target.setTitle(rs.getString("mtitle"));
						return target;
				    }
				});
		HashSet<Movie> set = new HashSet<Movie>(rs);
		return set;
	}
	
	public HashSet<Integer> getPopularMovies(int uid){
		String sql = "SELECT mid, mtitle from movielens.ratings,movielens.items where uid in " +
				"(SELECT uid from movielens.users where occupation = (SELECT occupation FROM movielens.users where uid = ?)) " +
				"AND rating > 3 And mid = itemid And itemid Not IN (SELECT distinct itemid FROM movielens.ratings where uid = ?) " +
				"Group by itemid Order by count(rating) desc limit 10 ";
		List<Integer> rs = getJdbcTemplate().query(
				sql,
				new Object[]{uid, uid},
				new RowMapper<Integer>(){
					public Integer mapRow(ResultSet rs, int rowNum) throws SQLException{
						return rs.getInt("mid");
					}
				});
		HashSet<Integer> set = new HashSet<Integer>(rs);
		return set;
	}
}
