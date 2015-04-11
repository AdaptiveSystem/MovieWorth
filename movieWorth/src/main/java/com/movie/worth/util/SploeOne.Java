package com.movie.worth.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import java.util.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.movie.worth.dao.SimilarityCalc;

@Component
public class SlopeOne {
	
	@Autowired//dao 处理所有与数据库有关内容
	private SimilarityCalc scCalcDAO;

	private int maxItemsId = 0;
    private int maxItem = 0;
    private int targetUser = 12;
    private float mteste[][];
    private float mFreq1[][];
    private int mFreq[][];
    private float mDiff[][];
    private Map<Integer,Map<Integer,Float>> usersMatrix;
    private Map<Integer,Float> user = new HashMap<Integer, Float>();
    private HashMap<Integer,Float> predictions = new HashMap<Integer,Float>();
    
    private int userid[];

    public static void main(String args[]){

        SlopeOne so = new SlopeOne(userid[],targetUser);
    }

    public SlopeOne(int userid[], int targetUser){

        readInput();
        buildDiffMatrix();
        maxItem = maxItemsId;
        mDiff = new float[maxItem + 1][maxItem + 1];
        mFreq1 = new float[maxItem + 1][maxItem + 1];
        
        for(int i = 1; i <= maxItem; i++)
              for(int j = 1; j <= maxItem; j++){
                mDiff[i][j] = 0;
                mFreq1[i][j] = 0;
              }
        
        for(int i = 1; i <= maxItemsId; i++){
                for(int j = i; j <= maxItemsId; j++){
                    if(!Float.isNaN (mteste[i][j])){
                         mDiff[i][j] = mteste[i][j];
                         mFreq1[i][j] = mFreq[i][j];
                    }
                }
        }
        Predict();
        
    }

    /*
     * Function readInput()
     * Read the input and saves it in the usersMatrix
     * And also extract target user information 
     */
    
    public void readInput(int[] userid, int targetuser){
    	
    	 HashSet<Rating> ratings = scCalcDAO.getRatingsOfOneUser(targetuser);
         
         for(Rating rating : ratings){
         	         	
         	user.put(Integer.parseInt(rating.getItemId()),Float.parseFloat(rating.getRating()));
         	
         }
           
           for(int i=0;i<userid.length;i++){
           
                int user =  userid[i];

                usersMatrix.put(user, new HashMap<Integer,Float>());
                
                HashSet<Rating> ratings = scCalcDAO.getRatingsOfOneUser(user);
                
                for(Rating rating : ratings){
                	
                	maxItemsId = maxItemsId < rating.getItemId() ? rating.getItemId() : maxItemsId;
                	
                	usersMatrix.get(user).put(Integer.parseInt(rating.getItemId()), Float.parseFloat(rating.getRating()));
                }
                
            }
           
    }
    
    /*
     * Function buildDiffMatrix()
     * Calculates the DiffMatrix for all items
     *
     */
    
    public void buildDiffMatrix() {

        mteste = new float[maxItemsId+1][maxItemsId+1];
        mFreq = new float[maxItemsId+1][maxItemsId+1];

        for(int i = 1; i <= maxItemsId; i++)
            for(int j = 1; j <= maxItemsId; j++){
                mteste[i][j] = 0;
                mFreq[i][j] = 0;
            }

        /* Iterate through all users, and then, through all items do calculate the diffs */
        for(int cUser : usersMatrix.keySet()){
            for(int i: usersMatrix.get(cUser).keySet()){
                for(int j : usersMatrix.get(cUser).keySet() ){
                    mteste[i][j] = mteste[i][j]  + 
                                   ( usersMatrix.get(cUser).get(i).floatValue() - (usersMatrix.get(cUser).get(j).floatValue()));
                    mFreq[i][j] = mFreq[i][j] + 1;
                }
            }
        }

        /*  Calculate the averages (diff/freqs) */
        for(int i = 1; i<= maxItemsId; i++){
            for(int j = i; j <= maxItemsId; j++){
                if(mFreq[i][j] > 0){
                    mteste[i][j] = mteste[i][j] / mFreq[i][j];
                }
            }
        }
    }
    
    class FloatEntryComparator implements Comparator<Map.Entry> {

        public int compare(Map.Entry e1, Map.Entry e2) {
            return ((Float) e2.getValue()).intValue() - ((Float) e1.getValue()).intValue();
        }
    }
    
    public void Predict(){
       
        float totalFreq[] =  new float [maxItem+1];

        /* Start prediction */
        for (int j=1; j <= maxItem; j++) {
            predictions.put(j,0.0f);
            totalFreq[j] = 0;
        }
        
        for (int j : user.keySet()) {
            for (int k = 1; k <= maxItem; k++) {
                if( j != k) {
                    /* Only for items the user has not seen */
                    if(!user.containsKey(k)){
                        float newVal = 0;
                        if(k < j) {
                            newVal = mFreq1[j][k] * (mDiff[j][k] + user.get(j).floatValue());
                        }
                        else {
                            newVal = mFreq1[j][k] * (-1 * mDiff[j][k] + user.get(j).floatValue());
                        }
                        totalFreq[k] = totalFreq[k] + mFreq1[j][k];
                        predictions.put(k, predictions.get(k).floatValue() + newVal);
                    }
                } 
            }
        }

        /* Calculate the average */
        for (int j : predictions.keySet()) {
            predictions.put(j, predictions.get(j).floatValue()/(totalFreq[j] ));
        }

        /* Fill the predictions vector with the already known rating values */
        for (int j : user.keySet()) {
            predictions.put(j, user.get(j));
        }
        
        /* Print predictions */
        
        List<Map.Entry> lists = new ArrayList<Map.Entry>(predictions.entrySet());
        java.util.Collections.sort(lists, new FloatEntryComparator());
        
        System.out.println("\n" + "#### Predictions Here #### ");
        for (Map.Entry<Integer, Float> entry:lists){
            System.out.println(entry.getKey().toString()+" : "+predictions.get(entry.getKey()).floatValue());
        }

}