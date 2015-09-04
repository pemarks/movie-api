package com.deloitte.movie.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

public class Theater implements Comparable<Theater> {
	private String id;
	
	private String name;
	private String address;
	private String phone;
	
	@JsonManagedReference
	private List<Showtime> showtimes = new ArrayList<Showtime>();

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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public List<Showtime> getShowtimes() {
		return showtimes;
	}

	public void setShowtimes(List<Showtime> showtimes) {
		this.showtimes = showtimes;
	}
	@Override
	public int compareTo(Theater theater) {
		return this.name.compareTo(theater.getName());
	}
}
