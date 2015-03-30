package com.jkjk.Host;

import java.util.ArrayList;

public class SpawnBuffer {
	private int capacity;
	private ArrayList<Location> buffer;

	public SpawnBuffer(int capacity) {
		this.capacity = capacity;
		buffer = new ArrayList<Location>(capacity);
	}

	public void produce(Location location) {
		if (buffer.size() < capacity) {
			buffer.add(location);
			notify();
		}

	}

	public void consume(Location location) {
		if (buffer.size() > 0) {
			buffer.remove(location);
			notify();
		}
	}
	
	public boolean isEmpty(){
		return buffer.size() == 0;
	}
	
	public boolean isFull(){
		return buffer.size() == capacity;
	}

	public ArrayList<Location> getBuffer() {
			return buffer;
	}
}
