package com.deloitte.movie.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.deloitte.movie.models.Movie;
import com.deloitte.movie.models.Showtime;
import com.deloitte.movie.models.Theater;
import com.deloitte.movie.services.GoogleMovieParsingService;

public class GoogleMovieParsingServiceImpl implements GoogleMovieParsingService {	
	private static final DateTimeFormatter TIME_FORMATTER = new DateTimeFormatterBuilder()
		.appendPattern("h:mm[a]")
		.parseDefaulting(ChronoField.AMPM_OF_DAY, 1)
		.toFormatter();
	
	@Override
	public List<Theater> parseTheaters(InputStream ios) {
		final List<Theater> theaters = new ArrayList<Theater>();
		
		try {
			Document htmlDoc = Jsoup.parse(ios, StandardCharsets.UTF_8.name(), "");
			final int date = findSelectedDate(htmlDoc);
			
			
			
			htmlDoc.select(".theater")
				.stream()
				.forEach(theaterElement -> {
					Theater theater = new Theater();
					
					//Get the name & id
					theaterElement.select("h2.name > a")
						.stream()
						.findAny()
						.ifPresent(theaterNameElement -> {
							String theaterURL = theaterNameElement.attr("href");
							theater.setId(theaterURL.substring(theaterURL.lastIndexOf('=') + 1));
							theater.setName(theaterNameElement.text());
						});
					
					//Get the address and phone
					theaterElement.select("div.info")
						.stream()
						.findAny()
						.ifPresent(infoElement -> {
							String[] infoSplit = infoElement.text().split(" - ");
							
							theater.setAddress(infoSplit[0]);
							theater.setPhone(infoSplit[1]);
						});
					
					//Get showtimes
					theaterElement.select("div.movie")
						.stream()
						.forEach(movieElement -> {
							Movie movie = getMovie(movieElement);
							List<Showtime> showtimes = getShowtimes(movieElement, date);
							
							showtimes
								.stream()
								.forEach(showtime -> {
									showtime.setId(id);
									showtime.setMovie(movie);
									showtime.setTheater(theater);
								});

							
												
						});
					
					Collections.sort(theater.getShowtimes());
					
					theaters.add(theater);
				});
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		Collections.sort(theaters);
		
		return theaters;
	}

	@Override
	public Movie parseMovie(InputStream ios) {
		Movie movie = null;
		
		try {
			Document htmlDoc = Jsoup.parse(ios, StandardCharsets.UTF_8.name(), "");
			Elements movieElements = htmlDoc.getElementsByClass("movie");
			
			if (!movieElements.isEmpty()) {
				movie = new Movie();
				Element movieElement = movieElements.get(0);				
			}
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}		
		
		return movie;
	}
	
	private int findSelectedDate(Element doc) {
		final int result;
		
		//TODO: Get the date
		Element selectedDateElement = doc.select("#left_nav > div.section")
			.first()
				.children()
					.stream()
					.filter(dateElement -> {
						return dateElement.childNodeSize() == 1 ? true : false;
					})
					.findAny()
					.orElse(null);
		
		if (selectedDateElement != null) {
			result = selectedDateElement.siblingIndex();
		} else {
			result = 0;
		}
		
		return result;
	}
	
	private Date getDate(int date, String time) {
		LocalTime formattedTime = LocalTime.parse(time, TIME_FORMATTER);
		return Date.from(LocalDateTime.of(LocalDate.now().plusDays(date), formattedTime).atZone(ZoneId.systemDefault()).toInstant());
	}
	
	private Movie getMovie(Element movieElement) {
		final Movie movie = new Movie();
		
		//Get the movie name
		movieElement.select("div.name > a")
			.stream()
			.findAny()
			.ifPresent(movieNameElement -> {
				String movieURL = movieNameElement.attr("href");
				
				movie.setId(movieURL.substring(movieURL.lastIndexOf('=') + 1));								
				movie.setName(movieNameElement.text());
			});
		
		return movie;
	}
	
	private List<Showtime> getShowtimes(final Element movieElement, final int date) {
		final List<Showtime> showtimes = new ArrayList<Showtime>();
		
		Elements showtimeElements = null;
		showtimeElements = movieElement.select("div.times a");
		
		if(!showtimeElements.isEmpty()) {
			showtimeElements
				.stream()
				.forEach(showtimeElement -> {
					Showtime showtime = new Showtime();
					
					showtime.setDateTime(getDate(date, showtimeElement.text().replaceAll("\u00A0", "").toUpperCase(Locale.US)));
					
					showtimes.add(showtime);					
				});
		} else {
			showtimeElements = movieElement.select("div.times > span");
			showtimeElements
				.stream()
				.forEach(showtimeElement -> {
					Showtime showtime = new Showtime();
					
					showtime.setDateTime(getDate(date, showtimeElement.text().replaceAll("\u00A0", "").toUpperCase(Locale.US)));				
					
					showtimes.add(showtime);
				});								
		}
		
		return showtimes;
	}
}