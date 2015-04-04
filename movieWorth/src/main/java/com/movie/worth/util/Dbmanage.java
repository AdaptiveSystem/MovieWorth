/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.movie.worth.util;


  
  import java.sql.Connection;
  import java.sql.DriverManager;
import java.sql.PreparedStatement;
  import java.sql.ResultSet;
  import java.sql.SQLException;
  import java.sql.Statement;
  
  public class Dbmanage {
     public Connection initDB() {
         // 鍒濆鍖栨暟鎹簱杩炴帴鏂规硶
         Connection conn = null;
         // 鍒涘缓涓�釜Connection鍙ユ焺
         try {
             Class.forName("com.mysql.jdbc.Driver");
             // 鍔犺浇鏁版嵁搴撻┍鍔�
             String url = "jdbc:mysql://localhost:8889/movielens";
             // 瀹氫箟鏁版嵁搴撳湴鍧�rl锛屽苟璁剧疆缂栫爜鏍煎紡
             conn = DriverManager.getConnection(url, "root", "root");
             System.out.println("Connection is succeed!!");
             // 寰楀埌鏁版嵁杩炴帴
         } catch (ClassNotFoundException e) {
 
             e.printStackTrace();
         } catch (SQLException e) {
 
             e.printStackTrace();
         }
         return conn;
         // 杩斿洖鏁版嵁搴撹繛鎺�
     }
 
     public void closeDB(Statement sta, Connection conn) {
         // 鍏抽棴鏁版嵁搴撹繛鎺ワ紙鏃犵粨鏋滈泦锛�
         try {
             sta.close();
             conn.close();
         } catch (SQLException e) {
 
             e.printStackTrace();
         }
 
     }
     public void closeDB(ResultSet rs, Statement sta, Connection conn) {
         // 鍏抽棴鏁版嵁搴撹繛鎺ワ紙鏈夌粨鏋滈泦锛�
         try {
             rs.close();
             sta.close();
             conn.close();
         } catch (SQLException e) {
 
             e.printStackTrace();
         }
 
     }
     
     public void closeDB(PreparedStatement stm, Connection conn) {
         // 鍏抽棴鏁版嵁搴撹繛鎺ワ紙鏈夌粨鏋滈泦锛�
         try {
             
             stm.close();
             conn.close();
         } catch (SQLException e) {
 
             e.printStackTrace();
         }
 
     }
     
     
 
 }