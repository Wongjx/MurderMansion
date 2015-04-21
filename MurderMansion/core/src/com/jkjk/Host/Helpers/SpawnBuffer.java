package com.jkjk.Host.Helpers;

import java.util.concurrent.ConcurrentHashMap;

public class SpawnBuffer {
	private int capacity;
	private ConcurrentHashMap<Float, Location> buffer;

	public SpawnBuffer(int capacity) {
		this.capacity = capacity;
		buffer = new ConcurrentHashMap<Float, Location>(capacity);
	}

	public void produce(Location location) {
		System.out.println("SpawnBuffer: tried to produce");
		if (buffer.size() < capacity) {
			System.out.println("SpawnBuffer: produced successfully");
			buffer.put(location.get()[0] * location.get()[1], new Location(location.get()));
		}

	}

	public void consume(Location location) {
		System.out.println("SpawnBuffer: tried to consume");
		if (buffer.size() > 0) {
			System.out.println("SpawnBuffer: consumed successfully");
			System.out.println("SpawnBuffer requesting to consume key: " + location.get()[0]
					* location.get()[1]);
			if (!buffer.containsKey(location.get()[0] * location.get()[1])) {
				System.out.println("SpawnBuffer: location not found when consume");
			}
			buffer.remove(location.get()[0] * location.get()[1]);
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

	public ConcurrentHashMap<Float, Location> getBuffer() {
		return buffer;
	}
}
