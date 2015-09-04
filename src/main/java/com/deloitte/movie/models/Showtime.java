package com.deloitte.movie.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;

public class Showtime implements Comparable<Showtime> {
	private long id;
	private Date dateTime;
	
	@JsonBackReference()
	private Theater theater;
	private Movie movie;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getDateTime() {
		return dateTime;
	}
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
	public Movie getMovie() {
		return movie;
	}
	public void setMovie(Movie movie) {
		this.movie = movie;
	}
	public Theater getTheater() {
		return theater;
	}
	public void setTheater(Theater theater) {
		this.theater = theater;
	}
	@Override
	public int compareTo(Showtime showtime) {
		final int movieComparison = this.movie.compareTo(showtime.getMovie());
		return movieComparison != 0 ? movieComparison : this.dateTime.compareTo(showtime.getDateTime());
	}
}
