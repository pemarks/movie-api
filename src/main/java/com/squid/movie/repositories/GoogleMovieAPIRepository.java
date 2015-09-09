package com.squid.movie.repositories;

import java.util.Date;
import java.util.List;

import com.squid.movie.models.*;

public interface GoogleMovieAPIRepository {
	List<Theater> getTheaters(String location, Date date);
	Theater getTheater(String id, Date date);
	
	Movie getMovie(String id);
}
