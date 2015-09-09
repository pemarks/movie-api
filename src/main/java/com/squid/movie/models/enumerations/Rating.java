package com.squid.movie.models.enumerations;

public enum Rating {
	G("G"), PG("PG"), PG13("PG-13"), R("R"), NC17("NC-17"), X("X");
	
	private final String value;
	private Rating(final String _value) {
		this.value = _value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static Rating getRating(final String _value) {
		Rating result = Rating.R;
		
		for (Rating rating : Rating.values()) {
			if (rating.value.equalsIgnoreCase(_value)) {
				result = rating;
				break;
			}
		}
		
		return result;
	}
}