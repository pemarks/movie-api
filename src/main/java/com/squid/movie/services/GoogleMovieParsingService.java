package com.squid.movie.services;

import java.io.InputStream;
import java.util.List;

import com.squid.movie.models.*;

public interface GoogleMovieParsingService {
	List<Theater> parseTheaters(InputStream ios);
	Movie parseMovie(InputStream ios);
}
