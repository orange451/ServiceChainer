package dev.anarchy.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.anarchy.event.Event;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DGraphElement {
	@JsonProperty("_X")
	private double x;
	
	@JsonProperty("_Y")
	private double y;

	@JsonProperty("_Width")
	private double width;

	@JsonProperty("_Height")
	private double height;

	@JsonProperty("_Name")
	private String name;

	@JsonProperty("_Color")
	private String color;
	
	@JsonIgnore()
	protected Event onChangedEvent = new Event();

	@JsonIgnore()
	public void setName(String name) {
		this.name = name;
		this.onChangedEvent.fire();
	}

	@JsonIgnore()
	public String getName() {
		return this.name;
	}

	@JsonIgnore()
	public void setSize(double width, double height) {
		this.width = width;
		this.height = height;
		this.onChangedEvent.fire();
	}

	@JsonIgnore()
	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
		this.onChangedEvent.fire();
	}

	@JsonIgnore()
	public double getWidth() {
		return this.width;
	}

	@JsonIgnore()
	public double getHeight() {
		return this.height;
	}

	@JsonIgnore()
	public double getX() {
		return this.x;
	}

	@JsonIgnore()
	public double getY() {
		return this.y;
	}

	@JsonIgnore()
	public void setColor(String hexString) {
		this.color = hexString;
		this.onChangedEvent.fire();
	}

	@JsonIgnore()
	public String getColor() {
		return this.color;
	}

}
