package com.movie.worth.controller;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.movie.worth.dao.SimilarityCalc;
import com.movie.worth.util.AdjustedSimilarity;
import com.movie.worth.util.Rating;

@Controller
public class Test {
	@Autowired
	private SimilarityCalc scdao;
	@Autowired
	private AdjustedSimilarity as;

	
	@RequestMapping(value = { "/test/sim1" }, method = RequestMethod.GET)
	public ModelAndView sim1(){
		ModelAndView modelAndView = new ModelAndView();
		HashSet<Rating> rs = scdao.getRatingsOfOneUser(1);
		for(Rating rating : rs){
			//System.out.println("rating on item " + rating.getItemId() + " is "+rating.getRating());
		}
		return modelAndView;
	}
	
	@RequestMapping(value = { "/test/sim2" }, method = RequestMethod.GET)
	public ModelAndView sim2(){
		ModelAndView modelAndView = new ModelAndView();
		scdao.getRatingsOfOneMovie(2);
		return modelAndView;
	}
	
	@RequestMapping(value = { "/test/simall" }, method = RequestMethod.GET)
	public ModelAndView simall(){
		ModelAndView modelAndView = new ModelAndView();
		HashMap<Integer, Double> simUsers = as.newUser(3);
		Set<Integer> keySet = simUsers.keySet();
		for(Integer key : keySet){
			System.out.println("uid = " + key + " and similarity is = " + simUsers.get(key));
		}
		return modelAndView;
	}
}
