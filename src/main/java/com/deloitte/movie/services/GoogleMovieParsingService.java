package com.deloitte.movie.services;

import java.io.InputStream;
import java.util.List;

import com.deloitte.movie.models.*;

public interface GoogleMovieParsingService {
	List<Theater> parseTheaters(InputStream ios);
	Movie parseMovie(InputStream ios);
}
