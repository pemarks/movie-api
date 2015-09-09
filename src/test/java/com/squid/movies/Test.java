package com.squid.movies;

import java.text.SimpleDateFormat;
import java.util.List;

import com.squid.movie.models.Theater;
import com.squid.movie.repositories.GoogleMovieAPIRepository;
import com.squid.movie.repositories.impl.GoogleMovieAPIRepositoryImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class Test {
	private final static ObjectMapper mapper;
	
	static {
		mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat("MM-dd-yyyy hh:mm a"));
	}
	
	public static String toJSON(Object obj) {
		String jsonString = null;
		
		try {
			jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		} catch (JsonProcessingException jpe) {
			jpe.printStackTrace();
		}
		
		return jsonString;
	}
	
	public static void main(String[] args) {
		GoogleMovieAPIRepository movieRepo = new GoogleMovieAPIRepositoryImpl();
		
		List<Theater> theaters = movieRepo.getTheaters("32811", null);
		for (Theater theater : theaters) {
			System.out.println(toJSON(theater));
		}
	}
}
