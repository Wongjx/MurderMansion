package com.jkjk.Server;


public class Location {

	private int x, y;

	Location(int[] a) {
		this.x = a[0];
		this.y = a[1];
	}

	public synchronized int[] get() {
		return new int[] { x, y };
	}

	public synchronized void set(int x, int y) {
		this.x = x;
		this.y = y;
	}

}