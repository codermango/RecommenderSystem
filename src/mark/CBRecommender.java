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

	public static HashMap<String, Movie> moviesHash = new HashMap<String, Movie>();
	public static ArrayList<Movie> allMovieList = new ArrayList<Movie>();

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
	public static void main(String[] args) {
		// System.out.println(9/950.0);
		CBRecommender cbr = new CBRecommender();

		User user1 = new User();
		user1.setLikedMovies(cbr.getTestData(50));

		ArrayList<String> recommendedMoviesId = cbr.recommend(user1.getLikedMovies());
		
		System.out.println("Recommended movies:");
		for(String item: recommendedMoviesId) {
			Movie movie = moviesHash.get(item);
			ArrayList<Genre> ownedGenres = getMovieOwnedGenres(movie);
			
			String genres = "";
			for(Genre genre: ownedGenres) {
				genres += genre.getGenreName();
			}
			
			System.out.println(movie.getMovieId()+" : "+movie.getMovieName() + "===" + genres);
			
		}
	}

	// ==========================================================================================================================
	/**
	 * 给一个电影，返回该电影拥有的电影标签
	 * @param movie
	 * @return
	 */
	private static ArrayList<Genre> getMovieOwnedGenres(Movie movie) {
		
		ArrayList<Genre> genres = movie.getMovieGenres();
		ArrayList<Genre> ownedGenres = new ArrayList<Genre>();
		for(int i=0; i<genres.size(); i++) {
			if(genres.get(i).isOwned()) {
				ownedGenres.add(genres.get(i));
			}
		}
		
		return ownedGenres;
	}

	// ==========================================================================================================================
	/**
	 * userLikedList必须遵循以下格式：
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
	public ArrayList<String> recommend(ArrayList<Movie> userLikedList) {
		// 把userLikedList中的Movie的genre各项相加
		ArrayList<Integer> sumGenreVector = new ArrayList<Integer>();

		sumGenreVector = getSumOfGenreVector(userLikedList);
		System.out.println(sumGenreVector);

		// 获取[0, 9, 8, 3, 6, 16, 10, 1, 27, 5, 0, 3, 1, 4, 14, 3, 10, 1, 0]这种向量的百分比
		int numOfLikedMovies = userLikedList.size();
		ArrayList<Double> sumGenreVectorPercent = getSumGenreVectorPercent(
				sumGenreVector, numOfLikedMovies);
		System.out.println(sumGenreVectorPercent);

		// 计算标签的TF-IDF
		ArrayList<Double> tfidfVector = getTFIDF(userLikedList);
		System.out.println(tfidfVector);
		System.out.println(getSumOfGenreVector(allMovieList));
		System.out.println("===================================================");

		// 剔除向量空间标签为0的电影，返回新电影列表
		ArrayList<Movie> relevantMovies = getRelevantMovies(tfidfVector);
		System.out.println(relevantMovies.size());

		// 获取relevantMovies中电影和TF-IDF进行余弦相似计算后的列表
		HashMap<String, Double> cosValues = getCosValues(relevantMovies,
				tfidfVector);
		//System.out.println(cosValues);

		// 以value排序，取得前20个电影，作为推荐电影
		ArrayList<String> recommendedMovieId = getRecommendedMovieId(cosValues);
		
		return recommendedMovieId;
	}


	
	/**
	 * 重构recommend函数，使其更为通用，只接收经过计算后的用户喜好向量。
	 * 通常情况下，userPreferenceVector可是是用户喜好列表中电影的标签的总和向量，如[2, 4, 6, 8, 2]
	 * @param userPreferenceVector 经过计算后的用户喜好向量
	 * @param numOfRecommendedMovies 需要推荐的电影数量
	 * @return 返回推荐的电影的id
	 */
	public ArrayList<String> recommend2(ArrayList<Integer> userPreferenceVector, int numOfRecommendedMovies) {
		
		// 计算标签的TF-IDF
		ArrayList<Double> tfidfVector = getTFIDF(userPreferenceVector);
		
		return null;
	}
	
	
	
	
	
	// ==========================================================================================================================
	/**
	 * 
	 * @param cosValues 每个电影的id和对应的余弦值
	 * @return 返回推荐的电影的id列表
	 */
	private ArrayList<String> getRecommendedMovieId(HashMap<String, Double> cosValues) {

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
		for (int i = 0; i < 20; i++) {
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
	 * @param ifidfVector
	 * @return 每个相关电影和用户的喜好向量做余弦，返回电影id和相应余弦值的pair
	 */
	private HashMap<String, Double> getCosValues(
			ArrayList<Movie> relevantMovies, ArrayList<Double> ifidfVector) {

		HashMap<String, Double> cosValues = new HashMap<String, Double>();

		for (int i = 0; i < relevantMovies.size(); i++) {
			Movie movie = relevantMovies.get(i);
			ArrayList<Integer> genreVector = getGenreVectorFromMovie(movie);

			// 计算余弦值
			double num1 = 0;// num1=a1*b1+a2*b2+a3*b3
			double num2 = 0;// num2=sqrt(a1^2+a2^2+a3^2) * sqrt(b1^2+b2^2+b3^2)
			for (int j = 0; j < ifidfVector.size(); j++) {
				num1 += ifidfVector.get(j) * genreVector.get(j);
			}

			double tmp1 = 0;
			double tmp2 = 0;
			for (int j = 0; j < ifidfVector.size(); j++) {
				tmp1 += Math.pow(ifidfVector.get(j), 2);
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
	 * 去除包含ifidf中为0的标签的电影
	 * @param ifidfVector
	 * @return 
	 */
	private static ArrayList<Movie> getRelevantMovies(
			ArrayList<Double> ifidfVector) {
		ArrayList<Integer> irrelevantGenreId = new ArrayList<Integer>();
		ArrayList<Movie> relevantMovies = allMovieList;

		for (int i = 0; i < ifidfVector.size(); i++) {
			if (ifidfVector.get(i) == 0.0) {
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
	 * @param userLikedList
	 * @return
	 */
	private ArrayList<Double> getTFIDF(ArrayList<Movie> userLikedList) {

		ArrayList<Integer> numOfGenreInLikedMovies = getSumOfGenreVector(userLikedList); // 所有标签在50个电影中出现的次数
		ArrayList<Integer> numOfGenreInAllMovies = getSumOfGenreVector(allMovieList); // 所有标签在所有电影中出现的次数

		// 获取50个电影的genre总数
		int totalNumOfGenreInLikedMovies = 0;
		for (int i = 0; i < userLikedList.size(); i++) {
			Movie movie = userLikedList.get(i);
			ArrayList<Genre> genre = movie.getMovieGenres();
			totalNumOfGenreInLikedMovies += genre.size();
		}

		// 获取所有电影的genre总数
		long totalGenreInAllMovies = 0;
		for (int i = 0; i < allMovieList.size(); i++) {
			Movie movie = allMovieList.get(i);
			ArrayList<Genre> genre = movie.getMovieGenres();
			totalGenreInAllMovies += genre.size();
		}

		System.out.println("50: " + totalNumOfGenreInLikedMovies + "\nAll: "
				+ totalGenreInAllMovies);

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
	 * 计算GenreVector百分比
	 * @param sumGenreVector
	 * @param numOfLikedMovies
	 * @return
	 */
	private ArrayList<Double> getSumGenreVectorPercent(
			ArrayList<Integer> sumGenreVector, int numOfLikedMovies) {

		ArrayList<Double> result = new ArrayList<Double>();
		for (int i = 0; i < sumGenreVector.size(); i++) {
			double p = sumGenreVector.get(i) / (double) numOfLikedMovies;
			result.add(p);
		}
		return result;
	}

	// ==========================================================================================================================
	/**
	 * 计算用户LikedList各电影标签的总数
	 * @param userLikedList
	 * @return
	 */
	private static ArrayList<Integer> getSumOfGenreVector(ArrayList<Movie> userLikedList) {

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
	private static ArrayList<Integer> addTwoArrayList(ArrayList<Integer> list1,
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
	private static ArrayList<Integer> getGenreVectorFromMovie(Movie movie) {
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

	// ==========================================================================================================================
	/**
	 * 生成测试数据
	 * @param num the number of liked movie a user owned
	 * @return return the Movie list
	 */
	public ArrayList<Movie> getTestData(int num) {
		ArrayList<Movie> testData = new ArrayList<Movie>();
		for (int i = 1; i <= num; i++) {
			testData.add(moviesHash.get(String.valueOf(i)));
		}
		return testData;
	}

}
