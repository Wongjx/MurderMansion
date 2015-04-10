package com.jkjk.Host.Helpers;

import java.util.concurrent.ConcurrentHashMap;

public class SpawnBuffer {
	private int capacity;
	private ConcurrentHashMap<String, Location> buffer;

	public SpawnBuffer(int capacity) {
		this.capacity = capacity;
		buffer = new ConcurrentHashMap<String, Location>(capacity);
	}

	public void produce(Location location) {
		if (buffer.size() < capacity) {
			buffer.put(location.toString(), new Location(location.get()));
		}

	}

	public void consume(Location location) {
		if (buffer.size() > 0) {
			if (buffer.containsKey(location.toString()))
				buffer.remove(location.toString());
		}
	}

	public synchronized void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public synchronized int getCapacity() {
		return capacity;
	}

	public boolean isEmpty() {
		return buffer.size() == 0;
	}

	public boolean isFull() {
		return buffer.size() == capacity;
	}

	public ConcurrentHashMap<String, Location> getBuffer() {
		return buffer;
	}
}
