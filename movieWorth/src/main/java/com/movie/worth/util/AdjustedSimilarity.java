/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.movie.worth.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.movie.worth.dao.SimilarityCalc;

/*
 *
 * @author lanzhang_mini
 * 
 */
@Repository
public class AdjustedSimilarity {
	
	@Autowired
	private SimilarityCalc scCalcDAO;
	
    private int tempRating;
    private static HashMap<Integer, Integer> CurrUser = null;

    public void newUser(int userid) {
    	//HashMap<Integer, Integer> CurrUser = null;
        CurrUser = getCurrUser(userid);

        HashMap<Integer, Map<Integer, Integer>> RelateUser = new HashMap<Integer, Map<Integer,Integer>>();
        HashMap<Integer, Integer> MovieAvg = new HashMap<Integer, Integer>();

        Iterator<?> iter_curruser = CurrUser.entrySet().iterator();
        int relateuser_size = CurrUser.size();
        while (iter_curruser.hasNext()) {
            @SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) iter_curruser.next();
            int movieid = (Integer) entry.getKey();
            RelateUser = getRelateUser(RelateUser, movieid);
            int avg = tempRating / relateuser_size;
            MovieAvg.put(movieid, avg);
        }

        //the result of this method
        HashMap<Integer, Double> UserSimilarity = new HashMap<Integer, Double>();

        Iterator<?> iter_relateuser = RelateUser.entrySet().iterator();
        while (iter_relateuser.hasNext()) {
            @SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) iter_relateuser.next();
            int userid_relate = (Integer) entry.getKey();
            @SuppressWarnings("unchecked")
			Map<Integer, Integer> User_movie = (Map<Integer, Integer>) entry.getValue();
            double similarity = CalSimilarity(User_movie, MovieAvg);
            UserSimilarity.put(userid_relate, similarity);
        }

    }

    //get the target user's profile
    public HashMap<Integer, Integer> getCurrUser(int userid) {
        HashMap<Integer, Integer> UserRating = new HashMap<Integer, Integer>();
        HashSet<Rating> ratings = scCalcDAO.getRatingsOfOneUser(userid);
        for(Rating rating : ratings){
        	UserRating.put(rating.getItemId(), rating.getRating());
        }
        return UserRating;
    }

    public HashMap<Integer, Map<Integer, Integer>> getRelateUser(HashMap<Integer,
    		Map<Integer, Integer>> relateUser, Integer Movieid) {
    	//HashMap<Integer, Map<Integer, Integer>> relateUser = new HashMap<>();
        tempRating = 0;
        HashSet<Rating> ratings = scCalcDAO.getRatingsOfOneMovie(Movieid);
        for(Rating rating : ratings){
        	Map<Integer, Integer> hashrating = null;
            if(relateUser.containsKey(rating.getuId())){
            	hashrating = relateUser.get(rating.getuId());
            	hashrating.put(rating.getItemId(), rating.getRating());
            }else{
            	hashrating = new HashMap<Integer, Integer>();
            	hashrating.put(rating.getItemId(), rating.getRating());
            }
            tempRating = tempRating + rating.getRating();
            relateUser.put(rating.getuId(), hashrating);
        }
        return relateUser;
    }

    public static double CalSimilarity(Map<Integer, Integer> relateuser, HashMap<Integer, Integer> Average) {

        double sim = 0;
        double up = 0;
        double down1 = 0;
        double down2 = 0;

        Iterator<Entry<Integer, Integer>> iter_relateuser = relateuser.entrySet().iterator();
        while (iter_relateuser.hasNext()) {
            @SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) iter_relateuser.next();
            int movie = (Integer) entry.getKey();
            if (CurrUser.containsKey(movie)) {
                int avg = Average.get(movie);
                int ri = CurrUser.get(movie);
                int rj = (Integer) entry.getValue();
                up = (ri - avg) * (rj - avg) + up;
                down1 = (ri - avg) * (ri - avg) + down1;
                down2 = (rj - avg) * (rj - avg) + down2;

            }
        }
        if (down1 != 0 && down2 != 0) {
            double down = Math.sqrt(down1*down2);
            sim = up / down;
        } else {
            sim = 0;
        }
        return sim;
    }

}
