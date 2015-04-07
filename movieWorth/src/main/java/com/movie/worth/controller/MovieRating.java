package com.movie.worth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MovieRating {
	
	@RequestMapping(value = { "/movie" }, method = RequestMethod.GET)
	public ModelAndView movieDetial(){
		ModelAndView model  = new ModelAndView();
		model.setViewName("movie");
		return model;
	}
	
}
