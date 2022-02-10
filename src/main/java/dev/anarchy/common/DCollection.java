package dev.anarchy.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DCollection extends DFolder {
	@JsonProperty("_IsCollection")
	private final boolean isCollection = true;
	
	public DCollection() {
		super();
	}
	
	/**
	 * Performs deep copy on DFolder.
	 */
	@Override
	public DCollection clone() {
		return (DCollection)super.clone();
	}
}
