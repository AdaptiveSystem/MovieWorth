package com.movie.worth.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.movie.worth.dao.SimilarityCalc;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 *
 * @author lanzhang_mini
 * 
 */
@Component
public class AdjustedSimilarity {
	
	@Autowired//dao 处理所有与数据库有关内容
	private SimilarityCalc scCalcDAO;
	
    private int tempAvgRating;
    private int tempRating;
    private HashMap<Integer, Integer> CurrUser = null;
    private int[] SimUserId=null;

    public AdjustedSimilarity(int userid){
        HashMap<Integer, Double> UserSimilarity=newUser(userid);
    }

    public int[] getSimUserId() {
        return SimUserId;
    }

    public void setSimUserId(int[] SimUserId) {
        this.SimUserId = SimUserId;
    }
    
    
    
    public HashMap<Integer, Double> newUser(int userid) {
    	//HashMap<Integer, Integer> CurrUser = null;
        CurrUser = getCurrUser(userid);

        HashMap<Integer, Map<Integer, Integer>> RelateUser = new HashMap<Integer, Map<Integer,Integer>>();
        HashMap<Integer, Integer> MovieAvg = new HashMap<Integer, Integer>();

        Iterator<?> iter_curruser = CurrUser.entrySet().iterator();
//        int relateuser_size = CurrUser.size();
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
            @SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) iter_relateuser.next();
            int userid_relate = (Integer) entry.getKey();
            @SuppressWarnings("unchecked")
			Map<Integer, Integer> User_movie = (Map<Integer, Integer>) entry.getValue();
            double similarity = CalSimilarity(User_movie, MovieAvg);
            UserSimilarity.put(userid_relate, similarity);
        }

        List<Map.Entry<Integer, Double>> mappingList = null;
        //Using ArrayList to turn map.entrySet() to list. 
        mappingList = new ArrayList<>(UserSimilarity.entrySet());
        //Using comparator to get the sorted keyset.
        Collections.sort(mappingList, new Comparator<Map.Entry<Integer, Double>>() {
            public int compare(Map.Entry<Integer, Double> mapping1, Map.Entry<Integer, Double> mapping2) {
                return mapping2.getValue().compareTo(mapping1.getValue());
            }
        });

        ArrayList<Integer> tempID = null;
        tempID = new ArrayList<>();
            for (Entry<Integer, Double> mappingList1 : mappingList) {
//            System.out.println("uid = " + mappingList.get(k).getKey() + " and similarity is = " + mappingList.get(k).getValue());
                if (!mappingList1.getKey().equals(userid)) {
                    if (mappingList1.getValue() >= 0.5) {
                        tempID.add(mappingList1.getKey());
                        System.out.println("uid = " + mappingList1.getKey() + " and similarity is = " + mappingList1.getValue());
                    }
                }
            }
        
        if(tempID.size()>0){
            SimUserId=new int[tempID.size()];
            for(int q=0;q<tempID.size();q++){
                SimUserId[q]=tempID.get(q);
            }
        }
        
        return UserSimilarity;
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
