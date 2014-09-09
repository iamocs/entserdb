package com.oracle.oes.datapoolintegration.location;

/**
 * Object for representing a Location.
 * 
 * @author OCS
 * 
 */
public class Location {

	// properties
	private String index;
	private String x;
	private String y;

	// constructor
	public Location(String index, String x, String y) {
		super();
		this.index = index;
		this.x = x;
		this.y = y;
	}

	// methods
	@Override
	public String toString() {
		return "(" + x + " " + y + ")";
	}

	// setters and getters
	public void setIndex(String index) {
		this.index = index;
	}

	public String getIndex() {
		return index;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getX() {
		return x;
	}

	public void setY(String y) {
		this.y = y;
	}

	public String getY() {
		return y;
	}

}