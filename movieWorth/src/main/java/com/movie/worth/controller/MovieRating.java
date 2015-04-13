package com.movie.worth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MovieRating {
	
	@RequestMapping(value={"/"}, method=RequestMethod.GET)
	public ModelAndView coverPage(){
		ModelAndView cover = new ModelAndView();
		cover.setViewName("cover");
		return cover;
	}
	
	@RequestMapping(value = { "/index" }, method = RequestMethod.GET)
	public ModelAndView indexPage(){
		ModelAndView index = new ModelAndView();
		index.setViewName("index");
		return index;
	}
	
	@RequestMapping(value = { "/movie" }, method = RequestMethod.GET)
	public ModelAndView movieDetail(){
		ModelAndView mDetail = new ModelAndView();
		mDetail.setViewName("mdetail");
		return mDetail;
	}
	
}
