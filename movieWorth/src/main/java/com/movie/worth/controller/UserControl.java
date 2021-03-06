package com.movie.worth.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.movie.worth.service.UserService;
import com.movie.worth.util.User;

@Controller
public class UserControl {
	
	@Autowired
	private UserService us;
	
	//method to show the login page
	@RequestMapping(value = { "/login" }, method = RequestMethod.GET)
	public ModelAndView loginPage(@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout){
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(!(auth instanceof AnonymousAuthenticationToken))
			modelAndView.addObject("goback", "window.history.back();");
		if(error != null)
			modelAndView.addObject("error", "Invalid username and password!");
		if(logout != null)
			modelAndView.addObject("msg", "You've been logged out successfully.");
		modelAndView.setViewName("login");
		return modelAndView;
	}
	
	//method to show the register page
	@RequestMapping(value = { "/reg" }, method = RequestMethod.GET)
	public ModelAndView regPage(){
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(!(auth instanceof AnonymousAuthenticationToken))
			modelAndView.setViewName("index");
		List<String> occups = us.getOccupations();
		modelAndView.addObject("occupations", occups);
		modelAndView.setViewName("reg");
		return modelAndView;
	}
	
	//method to register new user
	@RequestMapping(value = { "/reg" }, method = RequestMethod.POST)
	public @ResponseBody Hashtable<String, Boolean> doReg(@RequestBody User newComer){
		Hashtable<String, Boolean> rs = new Hashtable<String, Boolean>();
		if(us.register(newComer))
			rs.put("status", true);
		else
			rs.put("status", false);
		return rs;
	}
}
