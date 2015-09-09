package com.squid.movie.models.enumerations;

public enum Genre {
	ACTION, ADVENTURE, SUSPENSE, THRILLER, DRAMA, HORROR, COMEDY,
		ROMANCE, FAMILY, SCIFI, FANTASY;
	
	public static Genre getGenre(final String _genreString) {
		Genre result = Genre.ACTION;
		
		for (Genre genre : Genre.values()) {
			if (genre.name().equalsIgnoreCase(_genreString)) {
				result = genre;
				break;
			}
		}
		
		return result;
	}
}
