/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.movie.worth.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author lanzhang_mini
 */
public class KNN {

    private static int tempRating;
    private static HashMap<Integer, Integer> CurrUser = null;

    public KNN(int userid) {
//        HashMap<Integer, Integer> CurrUser = null;
        CurrUser = getCurrUser(userid);

        HashMap<Integer, Map<Integer, Integer>> RelateUser = new HashMap<Integer, Map<Integer,Integer>>();
        HashMap<Integer, Integer> MovieAvg = new HashMap<Integer, Integer>();

        Iterator iter_curruser = CurrUser.entrySet().iterator();
        int relateuser_size = CurrUser.size();
        while (iter_curruser.hasNext()) {
            Map.Entry entry = (Map.Entry) iter_curruser.next();
            int movieid = (Integer) entry.getKey();
            RelateUser = getRelateUser(RelateUser, movieid);
            int avg = tempRating / relateuser_size;
            MovieAvg.put(movieid, avg);
        }

        HashMap<Integer, Double> UserSimilarity = new HashMap<Integer, Double>();// the result of this method

        Iterator iter_relateuser = RelateUser.entrySet().iterator();
        while (iter_relateuser.hasNext()) {
            Map.Entry entry = (Map.Entry) iter_relateuser.next();
            int userid_relate = (Integer) entry.getKey();
            Map<Integer, Integer> User_movie = (Map<Integer, Integer>) entry.getValue();
            double similarity = CalSimilarity(User_movie, MovieAvg);
            UserSimilarity.put(userid_relate, similarity);
        }

    }

    //get the target user's profile
    public static HashMap<Integer, Integer> getCurrUser(int userid) {
        HashMap<Integer, Integer> UserRating = new HashMap<Integer, Integer>();

        Connection conn = null;
        Statement sta = null;
        Dbmanage db = new Dbmanage();
        ResultSet rs = null;
        try {
            conn = db.initDB();
            sta = conn.createStatement();
            String query_rating = "Select itemid, rating where uid=" + userid;
            rs = sta.executeQuery(query_rating);
            while (rs.next()) {
                UserRating.put(rs.getInt("itemid"), rs.getInt("rating"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.closeDB(rs, sta, conn);
        return UserRating;
    }

    public static HashMap<Integer, Map<Integer, Integer>> getRelateUser(HashMap<Integer, Map<Integer, Integer>> relateUser, Integer Movieid) {
//        HashMap<Integer, Map<Integer, Integer>> relateUser = new HashMap<>();
        tempRating = 0;
        Connection conn = null;
        Statement sta = null;
        Dbmanage db = new Dbmanage();
        ResultSet rs = null;
        try {
            conn = db.initDB();
            sta = conn.createStatement();
            String query_rating = "Select uid,itemid, rating where itemid=" + Movieid;
            rs = sta.executeQuery(query_rating);
            while (rs.next()) {
                Map<Integer, Integer> hashrating = null;
                int userid = rs.getInt("uid");
                int movieid = rs.getInt("itemid");
                int rating = rs.getInt("rating");
                if (relateUser.containsKey(userid)) {
                    hashrating = relateUser.get(userid);
                    hashrating.put(movieid, rating);

                } else {
                    hashrating = new HashMap<Integer, Integer>();
                    hashrating.put(movieid, rating);
                }
                tempRating = tempRating + rating;
                relateUser.put(userid, hashrating);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.closeDB(rs, sta, conn);
        return relateUser;
    }

    public static double CalSimilarity(Map<Integer, Integer> relateuser, HashMap<Integer, Integer> Average) {

        double sim = 0;
        double up = 0;
        double down1 = 0;
        double down2 = 0;

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
