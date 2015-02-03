package com.recommender;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public class ContentBasedRecommender {

	
	
	public int getIntersectionNum(String[] list1, String[] list2) {
		
		ArrayList<String> tmp = new ArrayList<String>();
		ArrayList<String> common = new ArrayList<String>();
		
		for(String item: list1) {
			if(!tmp.contains(item)) {
				tmp.add(item);
			}
		}
		
		for(String item: list2) {
			if(tmp.contains(item)) {
				common.add(item);
			}
		}			
		return common.size();
	}
	
	
	public String[] recommend(String movieId) {
		
		ArrayList<JSONObject> moviesJSON = new ArrayList<JSONObject>();
		Object genres = null;
		
		moviesJSON = getAllJSONMovies();
		System.out.println(moviesJSON);
		
		for(JSONObject obj: moviesJSON) {
			Set<String> keys = obj.keySet();
			Object[] strKeys = keys.toArray();
			String id = strKeys[0].toString();
			
			if(id.equals(movieId)) {
				genres = obj.get(id);
				System.out.println(genres);
				break;
			}

		}
		
		JSONArray genreArray = new JSONArray(genres.toString());
		String[] strGenres = new String[genreArray.length()];
		for(int i=0; i<genreArray.length(); i++) {
			strGenres[i] = genreArray.get(i).toString();
		}
		
		//遍历moviesJSON,把每一项的Value和strGenres进行比较，返回交集最多的10项
		for(JSONObject obj: moviesJSON) {
			Set<String> keys = obj.keySet();
			Object[] strKeys = keys.toArray();
			String id = strKeys[0].toString();
			
			Object objValue = obj.get(id);
			JSONArray temp = new JSONArray(objValue.toString());
			String[] strComp = new String[temp.length()];
			for(int i=0; i<temp.length(); i++) {
				strComp[i] = temp.get(i).toString();
			}
			
			int num = this.getIntersectionNum(strGenres, strComp);
			System.out.println(num);
		}
		
		
		System.out.println(strGenres.length);
		
		return null;
	}
	
	
	
	private ArrayList<JSONObject> getAllJSONMovies() {
		BufferedReader br;
		ArrayList<JSONObject> movies = new ArrayList<JSONObject>();
		try {
			br = new BufferedReader(new FileReader("data/test/movie_id_genre_id.json"));
			
			String line;
			while((line = br.readLine()) != null) {
				JSONObject obj = new JSONObject(line);
				movies.add(obj);
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return movies; 
	}
	
	
	
	public static void main(String[] args) {
		ContentBasedRecommender recommender = new ContentBasedRecommender();
//		String[] l1 = {"1", "2", "4"};
//		String[] l2 = {"4", "2"};
// 		int num = recommender.getIntersectionNum(l1, l2);
// 		System.out.println(num);
		
		recommender.recommend("2");
	}

}
