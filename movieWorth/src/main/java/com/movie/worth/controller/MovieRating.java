package com.movie.worth.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.movie.worth.service.MovieService;
import com.movie.worth.service.RatingService;
import com.movie.worth.util.Movie;
import com.movie.worth.util.MovieUserRel;

@Controller
public class MovieRating {
	
	private static final int INIT_RATING_NO = 5;
	@Autowired
	private RatingService rs;
	@Autowired
	private MovieService ms;
	
	private String guideNewUser(String viewName){
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = userDetails.getUsername();
		int no = rs.getCountOfOneUser(username);
		if(no < INIT_RATING_NO){
			return "userguide";
		}else{
			return viewName;
		}
	}
	
	@RequestMapping(value={"/"}, method=RequestMethod.GET)
	public ModelAndView coverPage(){
		ModelAndView cover = new ModelAndView();
		cover.setViewName("cover");
		return cover;
	}
	
	@RequestMapping(value = { "/index**" }, method = RequestMethod.GET)
	public ModelAndView indexPage(){
		ModelAndView index = new ModelAndView();
		index.setViewName(guideNewUser("index"));
		return index;
	}
	
	@RequestMapping(value = { "/movie/{mid}" }, method = RequestMethod.GET)
	public ModelAndView movieDetail(@PathVariable("mid") int mid){
		ModelAndView mDetail = new ModelAndView();
		mDetail.setViewName(guideNewUser("mdetail"));
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = userDetails.getUsername();
		MovieUserRel target = ms.getMovieWithUser(mid, username);
		mDetail.addObject("mName", target.getMovie().getTitle());
		mDetail.addObject("mId", target.getMovie().getMid());
		mDetail.addObject("mDate", target.getMovie().getReleaseDate());
		mDetail.addObject("mURL", target.getMovie().getImdbURL());
		mDetail.addObject("mRating", target.getMovie().getRating());
		mDetail.addObject("mRatingInt", Math.rint(target.getMovie().getRating()));
		//System.out.println(target.getMovie().getRating());
		mDetail.addObject("mGenre", target.getMovie().getGenre());
		mDetail.addObject("myRating", target.getMyRating());
		return mDetail;
	}
	
	@RequestMapping(value = { "/rating/{mid}/{rating}" }, method = RequestMethod.GET)
	public void updateRating(@PathVariable("mid") int mid, @PathVariable("rating") int rating){
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = userDetails.getUsername();
		rs.updateRating(mid, rating, username);
	}
	
	@RequestMapping(value = { "/recommend/start" }, method = RequestMethod.POST)
	public @ResponseBody ArrayList<Movie> m5(@RequestBody int times){
		return ms.get5Movie(times);
	}
	
	@RequestMapping(value = { "/recommend/slope" }, method = RequestMethod.GET)
	public @ResponseBody ArrayList<Movie> slope(){
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = userDetails.getUsername();
		return ms.getSlope(username);
	}
}
