package dev.anarchy.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DCollection extends DFolder {
	@JsonProperty("_IsCollection")
	private final boolean isCollection = true;
	
	public DCollection() {
		super();
	}
}
