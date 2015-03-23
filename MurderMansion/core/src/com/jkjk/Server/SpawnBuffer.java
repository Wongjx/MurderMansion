package com.jkjk.Server;

import java.util.ArrayList;

public class SpawnBuffer {
	private int capacity;
	private ArrayList<Location> buffer;

	public SpawnBuffer(int capacity) {
		this.capacity = capacity;
		buffer = new ArrayList<Location>(capacity);
	}

	public void produce(Location location) throws InterruptedException {
		synchronized (this) {
			while (buffer.size() == capacity) {
				wait();
			}
			buffer.add(location);
			notify();
		}

	}

	public void consume(Location location) throws InterruptedException {
		synchronized (this) {
			while (buffer.size() == 0) {
				wait();
			}
			buffer.remove(location);
			notify();
		}

	}
	
	public ArrayList<Location> getItem(){
		synchronized (this){
			return buffer;
		}
	}

}
