package mark;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

public class CBRecommender {

	public HashMap<String, Movie> moviesHash = new HashMap<String, Movie>();
	public ArrayList<Movie> allMovieList = new ArrayList<Movie>(); 

	// ==========================================================================================================================
	public CBRecommender() {
		
		try {
			BufferedReader br = new BufferedReader(new FileReader("data/movie/movies.json"));

			String id;
			String line;
			while ((line = br.readLine()) != null) {
				Movie movie = new Movie();
				JSONObject obj = new JSONObject(line);
				id = obj.getString("movieId").toString();
				movie.setMovieId(obj.get("movieId").toString());
				movie.setMovieName(obj.get("movieName").toString());

				JSONArray genres = new JSONArray(obj.get("genres").toString());
				ArrayList<Genre> movieGenres = new ArrayList<Genre>();

				for (int i = 0; i < genres.length(); i++) {
					JSONObject genreItem = genres.getJSONObject(i);

					Genre genre = new Genre();
					genre.setGenreId(genreItem.get("genre_id").toString());
					genre.setGenreName(genreItem.get("genre_name").toString());
					genre.setGenreWeight(Integer.parseInt(genreItem.get(
							"genre_weight").toString()));
					genre.setOwned((boolean) genreItem.get("is_owned"));

					movieGenres.add(genre);
				}

				movie.setMovieGenres(movieGenres);
				moviesHash.put(id, movie);
				allMovieList.add(movie);
			}

			br.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



	// ==========================================================================================================================
	/**
	 * userLikedList为以下格式：
	 * {"genres":[{"genre_weight":1,"is_owned":false,"genre_name":"unknown","genre_id":"0"},
	 * 			  {"genre_weight":1,"is_owned":false,"genre_name":"Action","genre_id":"1"},
	 *            {"genre_weight":1,"is_owned":true,"genre_name":"Adventure","genre_id":"2"},
	 *            {"genre_weight":1,"is_owned":true,"genre_name":"Animation","genre_id":"3"},
	 *            {"genre_weight":1,"is_owned":true,"genre_name":"Children","genre_id":"4"},
	 *            {"genre_weight":1,"is_owned":true,"genre_name":"Comedy","genre_id":"5"},
	 *            {"genre_weight":1,"is_owned":false,"genre_name":"Crime","genre_id":"6"},
	 *            {"genre_weight":1,"is_owned":false,"genre_name":"Documentary","genre_id":"7"},
	 *            {"genre_weight":1,"is_owned":false,"genre_name":"Drama","genre_id":"8"},
	 *            {"genre_weight":1,"is_owned":true,"genre_name":"Fantasy","genre_id":"9"},
	 *            {"genre_weight":1,"is_owned":false,"genre_name":"Film-Noir","genre_id":"10"},
	 *            {"genre_weight":1,"is_owned":false,"genre_name":"Horror","genre_id":"11"},
	 *            {"genre_weight":1,"is_owned":false,"genre_name":"Musical","genre_id":"12"},
	 *            {"genre_weight":1,"is_owned":false,"genre_name":"Mystery","genre_id":"13"},
	 *            {"genre_weight":1,"is_owned":false,"genre_name":"Romance","genre_id":"14"},
	 *            {"genre_weight":1,"is_owned":false,"genre_name":"Sci-Fi","genre_id":"15"},
	 *            {"genre_weight":1,"is_owned":false,"genre_name":"Thriller","genre_id":"16"},
	 *            {"genre_weight":1,"is_owned":false,"genre_name":"War","genre_id":"17"},
	 *            {"genre_weight":1,"is_owned":false,"genre_name":"Western","genre_id":"18"}],
	 * "movieId":"1",
	 * "movieName":"Toy Story (1995)"}
	 * 以上整个JSON格式代表一个电影，一个电影由movieId，movieName和genres组成，其中genres包括genre_id，genre_name，is_owned
	 * 和genre_weight组成
	 * @param userLikedList 用户的电影喜好列表
	 * @return 返回推荐的电影的id列表
	 */
	/**
	 * 重构recommend函数，使其更为通用，只接收经过计算后的用户喜好向量。
	 * 通常情况下，userPreferenceVector可是是用户喜好列表中电影的标签的总和向量，如[2, 4, 6, 8, 2]
	 * @param userPreferenceVector 经过计算后的用户喜好向量
	 * @param numOfRecommendedMovies 需要推荐的电影数量
	 * @param totalNumOfGenreInLikedMovies 用户喜欢的电影中，Genre的总和
	 * @param totalGenreInAllMovies 所有电影的Genre总和
	 * @return 返回推荐的电影的id
	 */
	public ArrayList<String> recommend(ArrayList<Integer> userPreferenceVector, 
			int numOfRecommendedMovies, 
			int totalNumOfGenreInLikedMovies, 
			long totalGenreInAllMovies) {
		
		// 计算标签的TF-IDF
		ArrayList<Double> tfidfVector = getTFIDF(userPreferenceVector,  totalNumOfGenreInLikedMovies, totalGenreInAllMovies);
		
		// 剔除向量空间标签为0的电影，返回新电影列表
		ArrayList<Movie> relevantMovies = getRelevantMovies(tfidfVector, allMovieList);
		
		// 获取relevantMovies中电影和TF-IDF进行余弦相似计算后的列表
		HashMap<String, Double> cosValues = getCosValues(relevantMovies, tfidfVector);
		
		// 以value排序，取得前numOfRecommendedMovies个电影，作为推荐电影
		ArrayList<String> recommendedMovieId = getRecommendedMovieId(cosValues, numOfRecommendedMovies);
		
		return recommendedMovieId;
	}
	
	
	// ==========================================================================================================================
	/**
	 * 
	 * @param cosValues 每个电影的id和对应的余弦值
	 * @param numOfRecommendedMovies 需要推荐电影的个数
	 * @return 返回推荐的电影的id列表
	 */
	private ArrayList<String> getRecommendedMovieId(HashMap<String, Double> cosValues, int numOfRecommendedMovies) {

		List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(
				cosValues.entrySet());
		//System.out.println(list);
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1,
					Map.Entry<String, Double> o2) {
				if ((o2.getValue() - o1.getValue()) > 0)
					return 1;
				else if ((o2.getValue() - o1.getValue()) == 0)
					return 0;
				else
					return -1;
			}
		});

		ArrayList<String> recommendedMovieId = new ArrayList<String>();
		for (int i = 0; i < numOfRecommendedMovies; i++) {
			Entry<String, Double> entry = list.get(i);
			recommendedMovieId.add(entry.getKey());
			System.out.println("id:"+entry.getKey()+"===value:"+entry.getValue());
		}	
		
		return recommendedMovieId;
	}

	// ==========================================================================================================================
	/**
	 * 
	 * @param relevantMovies
	 * @param tfidfVector
	 * @return 每个相关电影和用户的喜好向量做余弦，返回电影id和相应余弦值的pair
	 */
	private HashMap<String, Double> getCosValues(ArrayList<Movie> relevantMovies, ArrayList<Double> tfidfVector) {

		HashMap<String, Double> cosValues = new HashMap<String, Double>();

		for (int i = 0; i < relevantMovies.size(); i++) {
			Movie movie = relevantMovies.get(i);
			ArrayList<Integer> genreVector = getGenreVectorFromMovie(movie);

			// 计算余弦值
			double num1 = 0;// num1=a1*b1+a2*b2+a3*b3
			double num2 = 0;// num2=sqrt(a1^2+a2^2+a3^2) * sqrt(b1^2+b2^2+b3^2)
			for (int j = 0; j < tfidfVector.size(); j++) {
				num1 += tfidfVector.get(j) * genreVector.get(j);
			}

			double tmp1 = 0;
			double tmp2 = 0;
			for (int j = 0; j < tfidfVector.size(); j++) {
				tmp1 += Math.pow(tfidfVector.get(j), 2);
				tmp2 += Math.pow(genreVector.get(j), 2);
			}
			num2 = tmp1 * tmp2;

			double cos = num1 / num2;
			cosValues.put(movie.getMovieId(), cos);

		}

		return cosValues;
	}

	// ==========================================================================================================================
	/**
	 * 去除包含tfidf中为0的标签的电影
	 * @param tfidfVector 根据用户喜好列表计算后的TF-IDF向量
	 * @param allMovieList 所有电影的列表
	 * @return 返回去除标签数为0的那些标签后剩下的电影
	 */
	private ArrayList<Movie> getRelevantMovies(ArrayList<Double> tfidfVector, ArrayList<Movie> allMovieList) {
		
		ArrayList<Integer> irrelevantGenreId = new ArrayList<Integer>();
		ArrayList<Movie> relevantMovies = allMovieList;

		for (int i = 0; i < tfidfVector.size(); i++) {
			if (tfidfVector.get(i) == 0.0) {
				irrelevantGenreId.add(i);
			}
		}

		for (int i = 0; i < irrelevantGenreId.size(); i++) {
			int irrelevantId = irrelevantGenreId.get(i);

			for (int j = 0; j < relevantMovies.size(); j++) {
				Movie movie = relevantMovies.get(j);
				ArrayList<Genre> genres = movie.getMovieGenres();
				if (genres.get(irrelevantId).isOwned() == true) {
					relevantMovies.remove(movie);
				}
			}
		}

		return relevantMovies;
	}

	// ==========================================================================================================================
	/**
	 * 根据用户喜好向量，计算TF-IDF
	 * @param userPreferenceVector 用户喜好列表
	 * @param totalNumOfGenreInLikedMovies 用户喜好列表中genre的总数
	 * @param totalGenreInAllMovies 所有电影中genre总数
	 * @return 返回TF-IDF向量
	 */
	private ArrayList<Double> getTFIDF(ArrayList<Integer> userPreferenceVector, int totalNumOfGenreInLikedMovies, long totalGenreInAllMovies) {

		ArrayList<Integer> numOfGenreInAllMovies = getSumOfGenreVector(allMovieList); // 所有标签在所有电影中出现的次数
		ArrayList<Integer> numOfGenreInLikedMovies = userPreferenceVector;

		System.out.println("用户喜好列表中Genre的总数：" + totalNumOfGenreInLikedMovies);
		System.out.println("所有电影中Genre总数：" + totalGenreInAllMovies);

		ArrayList<Double> tfidfVector = new ArrayList<Double>();
		for (int i = 0; i < numOfGenreInLikedMovies.size(); i++) {
			long num1 = numOfGenreInLikedMovies.get(i);
			long num2 = numOfGenreInAllMovies.get(i);

			double ifidf;
			if (num2 == 0) {
				ifidf = 0;
			} else {
				ifidf = ((double) num1 / (double) totalNumOfGenreInLikedMovies)
						/ ((double) num2 / (double) totalGenreInAllMovies);
			}

			tfidfVector.add(ifidf);
		}

		return tfidfVector;
	}


	// ==========================================================================================================================
	/**
	 * 计算用户LikedList各电影标签的总数
	 * @param userLikedList
	 * @return
	 */
	private ArrayList<Integer> getSumOfGenreVector(ArrayList<Movie> userLikedList) {

		//以第一个电影的标签值初始化
		ArrayList<Integer> sumVector = getGenreVectorFromMovie(userLikedList.get(0));

		for (int i = 1; i < userLikedList.size(); i++) {
			Movie movie = userLikedList.get(i);
			ArrayList<Integer> vector = getGenreVectorFromMovie(movie);
			sumVector = addTwoArrayList(sumVector, vector);
		}

		return sumVector;
	}

	// ==========================================================================================================================
	/**
	 * 两个向量相加
	 * @param list1
	 * @param list2
	 * @return
	 */
	private ArrayList<Integer> addTwoArrayList(ArrayList<Integer> list1,
			ArrayList<Integer> list2) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < list1.size(); i++) {
			result.add(list1.get(i) + list2.get(i));
		}
		return result;
	}

	// ==========================================================================================================================
	/**
	 * 构造并返回电影的Genre向量
	 * @param movie a movie instance
	 * @return return like this {1,0,1,1,1,0}
	 */
	private ArrayList<Integer> getGenreVectorFromMovie(Movie movie) {
		ArrayList<Integer> genreVector = new ArrayList<Integer>();
		ArrayList<Genre> genreList = movie.getMovieGenres();
		for (int i = 0; i < genreList.size(); i++) {
			Genre genre = genreList.get(i);
			if (genre.isOwned()) {
				genreVector.add(1);
				// System.out.println(genre.getGenreId());
			} else {
				genreVector.add(0);
				// System.out.println(genre.getGenreId());
			}
		}
		return genreVector;
	}

	

}
