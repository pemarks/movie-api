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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.deloitte.movie.models.Movie;
import com.deloitte.movie.models.Showtime;
import com.deloitte.movie.models.Theater;
import com.deloitte.movie.models.enumerations.Genre;
import com.deloitte.movie.models.enumerations.Rating;
import com.deloitte.movie.services.GoogleMovieParsingService;

public class GoogleMovieParsingServiceImpl implements GoogleMovieParsingService {	
	private static final DateTimeFormatter TIME_FORMATTER = new DateTimeFormatterBuilder()
		.appendPattern("h:mm[a]")
		.parseDefaulting(ChronoField.AMPM_OF_DAY, 1)
		.toFormatter();
	
	private static final Map<String, Pattern> REGEX_MAP = new HashMap<String, Pattern>();
	
	static {
		REGEX_MAP.put("runtime", Pattern.compile("^\\d*hr\\s*\\d*min$"));
		REGEX_MAP.put("rating", Pattern.compile("^Rated\\s*(G|PG-13|PG|R|NC-17)$"));
		REGEX_MAP.put("genre", Pattern.compile("^((\\w?)/?)*$"));
	}
	
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
									showtime.setMovie(movie);
									showtime.setTheater(theater);
									
									theater.getShowtimes().add(showtime);
									showtime.setId(theater.getShowtimes().size());
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
		
		//Get the movie information
		movieElement.select("span.info")
			.stream()
			.findAny()
			.ifPresent(movieInfoElement -> {
				final List<String> infoParts = Arrays.asList(movieInfoElement.text().split(" - "));
				
				REGEX_MAP
					.entrySet()
						.stream()
						.forEach(entry -> {
							String matchedString = infoParts
								.stream()
								.filter(entry.getValue().asPredicate())
								.findAny()
								.orElse(null);
							
							if (matchedString != null) {
								switch(entry.getKey()) {
									case "runtime": {
										String hourString = matchedString.substring(0, matchedString.indexOf('h')),
											minuteString = matchedString.substring(matchedString.lastIndexOf(' '), matchedString.indexOf('m'));
										
										if (hourString != null) {
											movie.setHours(Integer.valueOf(hourString.trim()));
										} else {
											movie.setHours(1);
										}
										
										if (minuteString != null) {
											movie.setMinutes(Integer.valueOf(minuteString.trim()));
										} else {
											movie.setMinutes(45);
										}
										
										break;
									}
									case "rating": {
										String ratingString = matchedString.substring(matchedString.lastIndexOf(' '));
										
										if (ratingString != null) {
											movie.setRating(Rating.getRating(ratingString.trim()));
										} else {
											movie.setRating(Rating.PG13);
										}
										
										break;
									}
									case "genre": {
										String[] genres = matchedString.split("/");
										
										for (String genre : genres) {
											movie.getGenres().add(Genre.getGenre(genre.trim()));
										}
										break;
									}
									default: {
										
									}
								}
							}
						});
				
				movieInfoElement.children()
					.stream()
					.forEach(urlElement -> {
						if ("trailer".equalsIgnoreCase(urlElement.text().trim())) {							
							movie.setTrailerURL(getFormattedURL(urlElement.attr("href")));
						} else if ("imdb".equalsIgnoreCase(urlElement.text().trim())) {
							movie.setImdbURL(getFormattedURL(urlElement.attr("href")));							
						}
					});
			});
		
		return movie;
	}
	
	private String getFormattedURL(final String _url) {
		String url = _url;
		
		if (_url.startsWith("/url?q=")) {
			url = _url.substring(7);
		}
		
		return url;
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