package mark;

import java.util.ArrayList;


public class Movie {
	private String movieId;
	private String movieName;
	private ArrayList<Genre> movieGenres;
	/**
	 * @return the movieId
	 */
	public String getMovieId() {
		return movieId;
	}
	/**
	 * @param movieId the movieId to set
	 */
	public void setMovieId(String movieId) {
		this.movieId = movieId;
	}
	/**
	 * @return the movieName
	 */
	public String getMovieName() {
		return movieName;
	}
	/**
	 * @param movieName the movieName to set
	 */
	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}
	/**
	 * @return the movieGenres
	 */
	public ArrayList<Genre> getMovieGenres() {
		return movieGenres;
	}
	/**
	 * @param movieGenres the movieGenres to set
	 */
	public void setMovieGenres(ArrayList<Genre> movieGenres) {
		this.movieGenres = movieGenres;
	}

	
	public int getNumOfGenres() {
		int num = 0;
		for(Genre genre: movieGenres) {
			if(genre.isOwned()) {
				num += 1;
			}
		}
		return num;
	}
	
	public String getGenres() {
		String genres = "";
		for(Genre genre: movieGenres) {
			if(genre.isOwned()) {
				genres += genre.getGenreName() + " ";
			}
		}
		return genres;
	}
	
}

























