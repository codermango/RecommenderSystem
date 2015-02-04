package mark;

import java.util.ArrayList;
import java.util.HashMap;

public class Test {
	
	public static HashMap<String, Movie> moviesHash = new HashMap<String, Movie>();
	public static ArrayList<Movie> allMovieList = new ArrayList<Movie>(); 
	
	public static void main(String[] args) {	

		CBRecommender cbr = new CBRecommender();
		moviesHash = cbr.moviesHash;
		allMovieList = cbr.allMovieList;
		
		System.out.println(moviesHash);
		System.out.println(allMovieList.size());
		System.out.println(moviesHash.get("4460").getGenres());
		
		// 生成测试数据
		ArrayList<String> testUserLikedMovieId = new ArrayList<String>();
		for (int i=0; i<50; i++) {
			Movie movie = allMovieList.get(i);
			String id = movie.getMovieId();
			testUserLikedMovieId.add(id);
		}

		// 把userLikedList中的Movie的genre各项相加,生成uerPreferenceVector
		ArrayList<Integer> userPreferenceVector = new ArrayList<Integer>();
		userPreferenceVector = getSumOfGenreVector(testUserLikedMovieId);
		System.out.println(userPreferenceVector);

		int numOfRecommendedMovies = 100;
		
		int totalNumOfGenreInLikedMovies = getTotalNumOfGenreInLikedMovies(testUserLikedMovieId);
		
		long totalGenreInAllMovies = getTotalGenreInAllMovies();
		
		
		// 推荐
		ArrayList<String> recommendedMovieId = cbr.recommend(
				userPreferenceVector, numOfRecommendedMovies,
				totalNumOfGenreInLikedMovies, totalGenreInAllMovies);

		// 显示
		System.out.println("推荐的电影：");

		for (String id : recommendedMovieId) {
			Movie movie = cbr.moviesHash.get(id);
			String movieId = movie.getMovieId();
			String movieName = movie.getMovieName();
			String movieGenre = movie.getGenres();
			System.out.println(movieId + "==" + movieName + "==" + movieGenre);
		}

	}

	
	
	//=================================================================================================================================
	// 以下都是为数据做预处理的程序
	private static long getTotalGenreInAllMovies() {
		long result = 0;
		
		for(Movie movie: allMovieList) {
			result += movie.getNumOfGenres();
		}
		
		return result;
	}

	private static int getTotalNumOfGenreInLikedMovies(
			ArrayList<String> testUserLikedMovieId) {
		
		int result = 0;
		for(String id: testUserLikedMovieId) {
			int num = moviesHash.get(id).getNumOfGenres();
			result += num;
		}
		
		return result;
	}

	private static ArrayList<Integer> getSumOfGenreVector(
			ArrayList<String> testUserLikedMovieId) {

		// 以第一个电影的标签值初始化
		ArrayList<Integer> sumVector = getGenreVectorFromMovieId(testUserLikedMovieId.get(0));

		for (int i = 1; i < testUserLikedMovieId.size(); i++) {
			String movieId = testUserLikedMovieId.get(i);
			ArrayList<Integer> vector = getGenreVectorFromMovieId(movieId);
			sumVector = addTwoArrayList(sumVector, vector);
		}

		return sumVector;
	}

	private static ArrayList<Integer> getGenreVectorFromMovieId(String id) {
		ArrayList<Integer> genreVector = new ArrayList<Integer>();
		ArrayList<Genre> genreList = moviesHash.get(id).getMovieGenres();
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

	private static ArrayList<Integer> addTwoArrayList(ArrayList<Integer> list1, ArrayList<Integer> list2) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < list1.size(); i++) {
			result.add(list1.get(i) + list2.get(i));
		}
		return result;
	}

}
