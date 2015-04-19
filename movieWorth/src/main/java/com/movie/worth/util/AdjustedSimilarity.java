package com.movie.worth.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.movie.worth.dao.Ratings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 *
 * @author lanzhang_mini
 * 
 */
@Service
public class AdjustedSimilarity {
	
	@Autowired
	private Ratings scCalcDAO;
	
	private static final double SIM_THRESHOLD = 0.2;
	private static final int SIM_NO = 10;
    private int tempAvgRating=0;
    private int tempRating=0;
    private HashMap<Integer, Integer> CurrUser = null;
    private int[] SimUserId=null;
    
    public int[] getSimUserId(int userid) {
    	
    	this.clear();
    	
        ArrayList<Integer> tempID = new ArrayList<Integer>();
        for (Entry<Integer, Double> mappingList1 : this.getSimilarityOfOneUser(userid)) {
        	//System.out.println("uid = " + mappingList.get(k).getKey() + " and similarity is = " + mappingList.get(k).getValue());
        	if (!mappingList1.getKey().equals(userid)) {
        		if (mappingList1.getValue() >= SIM_THRESHOLD) {
        			tempID.add(mappingList1.getKey());
                    //System.out.println("uid = " + mappingList1.getKey() + " and similarity is = " + mappingList1.getValue());
                }
            }
        }
        
        if(tempID.size()>0){
            SimUserId=new int[tempID.size()];
            for(int q=0;q<tempID.size();q++){
                SimUserId[q]=tempID.get(q);
            }
        }
        
        if(SimUserId.length > SIM_NO){
            return Arrays.copyOfRange(SimUserId, 0, SIM_NO);
        }else{
        	return SimUserId;
        }
    }
    
    private List<Map.Entry<Integer, Double>> getSimilarityOfOneUser(int userid) {
    	//HashMap<Integer, Integer> CurrUser = null;
        CurrUser = getCurrUser(userid);

        HashMap<Integer, Map<Integer, Integer>> RelateUser = new HashMap<Integer, Map<Integer,Integer>>();
        HashMap<Integer, Integer> MovieAvg = new HashMap<Integer, Integer>();

        Iterator<?> iter_curruser = CurrUser.entrySet().iterator();
        while (iter_curruser.hasNext()) {
            @SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) iter_curruser.next();
            int movieid = (Integer) entry.getKey();
            RelateUser = getRelateUser(RelateUser, movieid);
            int avg = tempAvgRating;
            MovieAvg.put(movieid, avg);
        }

        //the result of this method
        HashMap<Integer, Double> UserSimilarity = new HashMap<Integer, Double>();

        Iterator<?> iter_relateuser = RelateUser.entrySet().iterator();
        while (iter_relateuser.hasNext()) {
			Map.Entry entry = (Map.Entry) iter_relateuser.next();
            int userid_relate = (Integer) entry.getKey();
			Map<Integer, Integer> User_movie = (Map<Integer, Integer>) entry.getValue();
            double similarity = CalSimilarity(User_movie, MovieAvg);
            UserSimilarity.put(userid_relate, similarity);
        }

        List<Map.Entry<Integer, Double>> mappingList = new ArrayList<Entry<Integer, Double>>(UserSimilarity.entrySet());
        //Using comparator to get the sorted keyset.
        Collections.sort(mappingList, new Comparator<Map.Entry<Integer, Double>>() {
            public int compare(Map.Entry<Integer, Double> mapping1, Map.Entry<Integer, Double> mapping2) {
                return mapping2.getValue().compareTo(mapping1.getValue());
            }
        });
        
        return mappingList;
    }

    //get the target user's profile
    private HashMap<Integer, Integer> getCurrUser(int userid) {
        HashMap<Integer, Integer> UserRating = new HashMap<Integer, Integer>();
        HashSet<Rating> ratings = scCalcDAO.getRatingsOfOneUser(userid);
        for(Rating rating : ratings){
        	UserRating.put(rating.getItemId(), rating.getRating());
        }
        return UserRating;
    }

    private HashMap<Integer, Map<Integer, Integer>> getRelateUser(HashMap<Integer,
    		Map<Integer, Integer>> relateUser, Integer Movieid) {
    	//HashMap<Integer, Map<Integer, Integer>> relateUser = new HashMap<>();
        tempRating = 0;
        tempAvgRating = 0;
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
        tempAvgRating = tempRating / ratings.size();
        return relateUser;
    }

    public double CalSimilarity(Map<Integer, Integer> relateuser, HashMap<Integer, Integer> Average) {

        double sim = 0;
        double up = 0;
        double down1 = 0;
        double down2 = 0;
        int noHasSameMidOfRatings = 0;

        Iterator<Entry<Integer, Integer>> iter_relateuser = relateuser.entrySet().iterator();
        while (iter_relateuser.hasNext()) {
			Map.Entry entry = (Map.Entry) iter_relateuser.next();
            int movie = (Integer) entry.getKey();
            if (CurrUser.containsKey(movie)) {
                int avg = Average.get(movie);
                int ri = CurrUser.get(movie);
                int rj = (Integer) entry.getValue();
                up = (ri - avg) * (rj - avg) + up;
                down1 = (ri - avg) * (ri - avg) + down1;
                down2 = (rj - avg) * (rj - avg) + down2;
                noHasSameMidOfRatings++;
            }
        }
        
        if (down1 != 0 && down2 != 0) {
            double down = Math.sqrt(down1*down2);
            sim = up / down;
        } else {
            sim = 0;
        }
        
        //assign a weight for the similarity value
        return sim * noHasSameMidOfRatings / CurrUser.size();
    }
    
    private void clear(){
    	this.tempAvgRating=0;
        this.tempRating=0;
        this.CurrUser = null;
        this.SimUserId=null;
    }

}
