package com.recommender;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class UserBasedRecommender {

	private int userID;
	private int length;
	
	public UserBasedRecommender(int userID) {
		this.userID = userID;
	}
	
	public List<RecommendedItem> getRecommendation() {
		
		List<RecommendedItem> recommendations = null;
		length = (int)((Math.random() + 3) * 10);
		
		System.out.println(length);
		
		try {
			//1. 构建模型
			DataModel model = new FileDataModel(new File("data/ratings.dat"), "::");
			
			//2. 计算相似度
			UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
			
			//3. 查找k相邻
			UserNeighborhood neighborhood = new NearestNUserNeighborhood(length, similarity, model);
			
			//4. 构造推荐引擎	
			Recommender recommender = new CachingRecommender(new GenericUserBasedRecommender(model, neighborhood, similarity));
			recommendations = recommender.recommend(this.userID, 20);
			
		} catch (IOException | TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return recommendations;
		
	}
	
	
	public static void main(String args[]) { 
		
		UserBasedRecommender recommender = new UserBasedRecommender(1);
		List<RecommendedItem> items = recommender.getRecommendation();
		
		System.out.println(items.size());
		
		for(RecommendedItem item: items) {
			System.out.println("==" + item);
		}
		
		System.out.println("aaaa");
	}
	
	
}










































































