package com.deloitte.movie.models;

import java.util.ArrayList;
import java.util.List;

import com.deloitte.movie.models.enumerations.Genre;
import com.deloitte.movie.models.enumerations.Rating;

public class Movie implements Comparable<Movie> {
	private String id;
	
	private int hours, minutes;
	private Rating rating;
	private List<Genre> genres = new ArrayList<Genre>();
	
	private String name;
	private String description;
	
	private String imdbURL;
	private String trailerURL;
	private String posterURL;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getMinutes() {
		return minutes;
	}
	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}
	public int getHours() {
		return hours;
	}
	public void setHours(int hours) {
		this.hours = hours;
	}
	public List<Genre> getGenres() {
		return genres;
	}
	public void setGenres(List<Genre> genres) {
		this.genres = genres;
	}
	public Rating getRating() {
		return rating;
	}
	public void setRating(Rating rating) {
		this.rating = rating;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImdbURL() {
		return imdbURL;
	}
	public void setImdbURL(String imdbURL) {
		this.imdbURL = imdbURL;
	}
	public String getPosterURL() {
		return posterURL;
	}
	public void setPosterURL(String posterURL) {
		this.posterURL = posterURL;
	}
	public String getTrailerURL() {
		return trailerURL;
	}
	public void setTrailerURL(String trailerURL) {
		this.trailerURL = trailerURL;
	}
	@Override
	public int compareTo(Movie movie) {
		return this.name.compareTo(movie.getName());
	}
}
