package com.convert;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

public class MovieToJSON {

	final String[] tags = { "unknown", "Action", "Adventure", "Animation",
			"Children", "Comedy", "Crime", "Documentary", "Drama", "Fantasy",
			"Film-Noir", "Horror", "Musical", "Mystery", "Romance", "Sci-Fi",
			"Thriller", "War", "Western" };
	final HashMap<Integer, String> tagNames = new HashMap<Integer, String>();

	public MovieToJSON() {
		for (int i = 0; i < tags.length; i++) {
			tagNames.put(i, tags[i]);
		}

		// System.out.println(tagNames.get(18));
	}

	public void writeMovieToJSON(String inputFilePath, String outputfilePath) {
		BufferedReader br;
		BufferedWriter bw;
		try {
			br = new BufferedReader(new FileReader(inputFilePath));
			bw = new BufferedWriter(new FileWriter(outputfilePath));

			String line;
			while ((line = br.readLine()) != null) {
				// System.out.println(line);
				String[] movieItem = line.split("\\|", 6);
				// bw.write(movieItem[1]+ ": " + movieItem[5] + "\r\n");

				// 提取tag，构造新电影信息数组
				String[] strTags = movieItem[5].split("\\|");
				ArrayList<String> tags = new ArrayList<String>();
				for (int i = 0; i < strTags.length; i++) {
					if (Integer.parseInt(strTags[i]) != 0) {
						tags.add(tagNames.get(i));
					}
				}

				// 构造json
				JSONStringer oneMovie = new JSONStringer();
				String s = oneMovie.object().key("id").value(movieItem[0])
						.key("title").value(movieItem[1]).key("tags")
						.value(tags).endObject().toString();
				bw.write(s + "\r\n");
				System.out.println(s);
			}

			br.close();
			bw.close();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public void writeTagToJSON(String outputfilePath) {

		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(outputfilePath));


			JSONObject obj = new JSONObject();
			for (int i = 0; i < tagNames.size(); i++) {
//				JSONStringer item = new JSONStringer();
//				content = item.object().key("id").value(i).key("genre").value(tagNames.get(i)).endObject().toString();
							
				obj.put(String.valueOf(i), tagNames.get(i));
		
			}
			bw.write(obj.toString());
			bw.close();
			System.out.println(obj);
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
	}

	
	public void generateMovieIdWithGenreId(String inputFilePath, String outputfilePath) {
		BufferedReader br;
		BufferedWriter bw;
		try {
			br = new BufferedReader(new FileReader(inputFilePath));
			bw = new BufferedWriter(new FileWriter(outputfilePath));
			

			String line;

			while((line = br.readLine()) != null) {

				String[] s = line.split("::");
				
				String movieId = s[0];
				String movieGenre = s[s.length - 1];
				
				
				
				
				ArrayList<String> genreList = new ArrayList<String>();
				
				String[] movieGenreList = movieGenre.split("\\|");
				
				JSONObject genreObj = new JSONObject(new JSONTokener(new FileReader(new File("data/test/genre.json"))));
				for(String key: genreObj.keySet()) {
					for(int i=0; i<movieGenreList.length; i++) {
						String genre = genreObj.getString(key);
						if(movieGenreList[i].equals(genre)) {
							genreList.add(key);
						}
					}
				}
				
				JSONObject obj = new JSONObject();
				obj.put(movieId, genreList);
				bw.write(obj.toString()+"\r\n");
				
				
				System.out.println(s[0]+": "+s[1] + ":"+obj.toString());
			}
			
			br.close();
			bw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	

	
	
	
	
	
	public static void main(String[] args) {

		MovieToJSON movieToJSON = new MovieToJSON();
//		movieToJSON.writeMovieToJSON("data/ml-100k/u.item",
//				"data/test/movies.json");
//		movieToJSON.writeTagToJSON("data/test/genre.json");
		
		movieToJSON.generateMovieIdWithGenreId("data/ml-10M100K/movies.dat", "data/test/movie_id_genre_id.json");

	}

}
