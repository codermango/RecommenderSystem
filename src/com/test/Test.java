package com.test;

import java.util.List;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import com.recommender.DatabaseUtility;
import com.recommender.UserBasedRecommender;

public class Test {

	public static void main(String[] args) {
		UserBasedRecommender recommender = new UserBasedRecommender(1);
		List<RecommendedItem> items = recommender.getRecommendation();
		
		
		
		DatabaseUtility.connect();
		String[] recommendedMovies = new String[items.size()];
		for(int i = 0; i < items.size(); i++) {
			recommendedMovies[i] = DatabaseUtility.getFullMovieInfo(items.get(i).getItemID());
		}
		
		System.out.println("The recommended movies for user 1 is:");
		for(String name: recommendedMovies) {
			System.out.println(name);
		}
	}

}
