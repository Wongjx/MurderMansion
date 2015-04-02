package com.jkjk.Host;


public class Location {

	private float x, y;

	public Location(float[] a) {
		this.x = a[0];
		this.y = a[1];
	}

	public synchronized float[] get() {
		return new float[] { x, y };
	}

	public synchronized void set(float x, float y) {
		this.x = x;
		this.y = y;
	}

}