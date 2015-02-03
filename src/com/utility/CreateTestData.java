package com.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

public class CreateTestData {

	public static void main(String[] args) {
		
		final String[] tags = { "unknown", "Action", "Adventure", "Animation",
				"Children", "Comedy", "Crime", "Documentary", "Drama", "Fantasy",
				"Film-Noir", "Horror", "Musical", "Mystery", "Romance", "Sci-Fi",
				"Thriller", "War", "Western" };
		
//		{
//			  "movieId": "0",
//			  "movieName": "Harry Potter",
//			  "genres": [{"genre_id": "0", "genre_name": "Action", "genre_weight": 0.8, "is_owned": true}, {}]
//			  
//		}

		JSONArray genreList = new JSONArray();
		for(int i=0; i<tags.length; i++) {
			JSONObject genreObj = new JSONObject();
			genreObj.put("genre_id", String.valueOf(i));
			genreObj.put("genre_name", tags[i]);
			genreObj.put("genre_weight", 1);
			genreObj.put("is_owned", false);
			
			genreList.put(genreObj);
		}
		
		System.out.println(genreList);		
		
		
		BufferedReader br;
		BufferedWriter bw;
		try {
			br = new BufferedReader(new FileReader("data/ml-10M100K/movies.dat"));
			bw = new BufferedWriter(new FileWriter("data/movie/movies.json"));
			
			String line;
			String movieId, movieName, movieGenres;
			while((line = br.readLine()) != null) {
				String[] item = line.split("::");
				movieId = item[0];
				movieName = item[1];
				movieGenres = item[2];
				
				String[] movieGenresList = movieGenres.split("\\|");
				
				//JSONArray genres = new JSONArray(movieGenresList);
				JSONArray finalGenres = new JSONArray();
				finalGenres = setIsOwn(genreList, movieGenresList);
				
				JSONObject movieObj = new JSONObject();	
				movieObj.put("movieId", movieId);
				movieObj.put("movieName", movieName);
				movieObj.put("genres", finalGenres);
				
				bw.write(movieObj+"\r\n");
				System.out.println(movieObj);
			}
				
			
			br.close();
			bw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

	private static JSONArray setIsOwn(JSONArray genreList, String[] movieGenresList) {
		
		JSONArray result = new JSONArray();
		for(int i=0; i<genreList.length(); i++) {
			JSONObject obj = genreList.getJSONObject(i);
			
			for(int j=0; j<movieGenresList.length; j++) {
				if(obj.get("genre_name").equals(movieGenresList[j])) {
					obj.put("is_owned", true);
					result.put(obj);
					break;
				} else if(j == movieGenresList.length - 1) {
					obj.put("is_owned", false);
					result.put(obj);
				}
			}
			
		}
		
		return result;
	}

}
