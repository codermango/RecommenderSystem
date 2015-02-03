package com.recommender;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtility {
	private static String url = "jdbc:mysql://localhost:3306/?user=root";
	private static String username = "root";
	private static String password = "root";
	
	private static Connection conn;
	private static Statement stmt;
	private static ResultSet rs;
	
	public DatabaseUtility() {
		
	}
	
	public static void myprint(Object o) {
		System.out.println(o);
	}
	
	public static void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Driver is loaded successfully!");
		} catch (ClassNotFoundException e) {
			System.out.println("Driver loading failed!!!!!");
			e.printStackTrace();
		}
		
		try {
			conn = DriverManager.getConnection(url, username, password);
			stmt = conn.createStatement();
			System.out.println("Connect mysql successfully!");
			
		} catch (SQLException e) {
			System.out.println("Connection failed!!!!!");
			e.printStackTrace();
		}
		
	}
	
//	public static void select(String sql) {
//		try {
//			rs = stmt.executeQuery(sql);
//			ResultSetMetaData meta_data = rs.getMetaData(); // 列名
//			
//			while(rs.next()) {
//				for(int i = 1; i <= meta_data.getColumnCount(); i++) {
//					System.out.println(rs.getString(i));
//				}
//			}
//			
//			rs.close();
//			
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	public static String getNameById(long id) {
		String sql = "select name from db_movie.movies where id=" + id;
		String name = "";
		try {
			rs = stmt.executeQuery(sql);
			//ResultSetMetaData meta_data = rs.getMetaData(); // 列名
			
			rs.next();
			name = rs.getString(1);
			rs.close();
			
			
		} catch (SQLException e) {
			System.out.println("The movie doesn't exit!!!!");
			e.printStackTrace();
		}
		
		return name;
	}
	
	public static String getTypeById(long id) {
		String sql = "select type from db_movie.movies where id=" + id;
		String type = "";
		try {
			rs = stmt.executeQuery(sql);
			//ResultSetMetaData meta_data = rs.getMetaData(); // 列名
			
			rs.next();
			type = rs.getString(1);
			rs.close();
			
			
		} catch (SQLException e) {
			System.out.println("The movie doesn't exit!!!!");
			e.printStackTrace();
		}
		
		return type;
	}
	
	public static String getFullMovieInfo(long id) {
		String sql = "select id, name, type from db_movie.movies where id=" + id;
		String movieInfo = "";
		try {
			rs = stmt.executeQuery(sql);
			//ResultSetMetaData meta_data = rs.getMetaData(); // 列名
			
			rs.next();
			movieInfo = rs.getString(1) + "-----" + rs.getString(2) + "====" + rs.getString(3);
			
			rs.close();
			
			
		} catch (SQLException e) {
			System.out.println("The movie doesn't exit!!!!");
			e.printStackTrace();
		}
		
		return movieInfo;
	}
	
	
	public static void main(String args[]) {
		connect();
		System.out.println(getNameById(1000));
		System.out.println("aaaa");
	}
}




























































