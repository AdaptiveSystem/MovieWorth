package com.movie.worth.controller;

import java.util.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.movie.worth.util.User;

@Controller
public class UserControl {
	
	//method to show the login page
	@RequestMapping(value = { "/login" }, method = RequestMethod.GET)
	public ModelAndView loginPage(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("login");
		return modelAndView;
	}
	
	//method to show the register page
	@RequestMapping(value = { "/reg" }, method = RequestMethod.GET)
	public ModelAndView regPage(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("reg");
		return modelAndView;
	}
	
	//method to register new user
	@RequestMapping(value = { "/reg" }, method = RequestMethod.POST)
	public @ResponseBody Hashtable<String, String> doReg(@RequestBody User newComer){
		Hashtable<String, String> rs = new Hashtable<String, String>();
		return rs;
	}
}
