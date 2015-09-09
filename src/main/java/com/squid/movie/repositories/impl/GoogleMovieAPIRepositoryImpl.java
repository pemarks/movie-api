package com.squid.movie.repositories.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import com.squid.movie.models.Movie;
import com.squid.movie.models.Theater;
import com.squid.movie.repositories.GoogleMovieAPIRepository;
import com.squid.movie.services.GoogleMovieParsingService;
import com.squid.movie.services.impl.GoogleMovieParsingServiceImpl;

public class GoogleMovieAPIRepositoryImpl implements GoogleMovieAPIRepository {
	private final CloseableHttpClient CLIENTS;
	private final GoogleMovieParsingService PARSING_SERVICE;
	
	public GoogleMovieAPIRepositoryImpl() {
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(20);
		
		CLIENTS = HttpClients
			.custom()
				.setConnectionManager(connectionManager)
				.build();
		
		PARSING_SERVICE = new GoogleMovieParsingServiceImpl();
	}
	
	@Override
	public List<Theater> getTheaters(String location, Date date) {
		List<Theater> theaters = new ArrayList<Theater>();
		URI uri = null;
		
		try {
			uri = new URIBuilder()
				.setScheme("http")
				.setHost("www.google.com")
				.setPort(80)
				.setPath("/movies")
				.setParameter("near", location)
				.build();
			
			System.out.println(uri.toString());
			
			try (CloseableHttpResponse response = CLIENTS.execute(new HttpGet(uri));) {				
				if (response != null) {
					theaters = PARSING_SERVICE.parseTheaters(response.getEntity().getContent());
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();			
			}
		} catch (URISyntaxException use) {
			use.printStackTrace();
		}

		return theaters;
	}
	
	@Override
	public Theater getTheater(String id, Date date) {
		Theater theater = null;
		URI uri = null;
		
		try {
			uri = new URIBuilder()
				.setScheme("http")
				.setHost("www.google.com")
				.setPort(80)
				.setPath("/movies")
				.setParameter("tid", id + "")
				.build();
			
			try {
				CloseableHttpResponse response = CLIENTS.execute(new HttpGet(uri));
				
				if (response != null) {
					
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();			
			}
		} catch (URISyntaxException use) {
			use.printStackTrace();
		}

		return theater;
	}

	@Override
	public Movie getMovie(String id) {
		Movie movie = null;
		URI uri = null;
		
		try {
			uri = new URIBuilder()
				.setScheme("http")
				.setHost("www.google.com")
				.setPort(80)
				.setPath("/movies")
				.setParameter("mid", id + "")
				.build();
			
			try {
				CloseableHttpResponse response = CLIENTS.execute(new HttpGet(uri));
				
				if (response != null) {
					System.out.println(EntityUtils.toString(response.getEntity()));
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();			
			}
		} catch (URISyntaxException use) {
			use.printStackTrace();
		}

		return movie;
	}
}
