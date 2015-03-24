package com.jkjk.Host;

import java.util.ArrayList;
import java.util.Random;

public class ItemSpawner {

	private ArrayList<Location> itemLocations;
	private Random randInt;
	private Location location;
	private int index;

	ItemSpawner() {
		itemLocations = new ArrayList<Location>();
		itemLocations.add(new Location(new float[] { 1, 1 }));
		itemLocations.add(new Location(new float[] { 1, 1 }));
		itemLocations.add(new Location(new float[] { 1, 1 }));
		itemLocations.add(new Location(new float[] { 1, 1 }));
		itemLocations.add(new Location(new float[] { 1, 1 }));
		itemLocations.add(new Location(new float[] { 1, 1 }));
		itemLocations.add(new Location(new float[] { 1, 1 }));
		itemLocations.add(new Location(new float[] { 1, 1 }));
		itemLocations.add(new Location(new float[] { 1, 1 }));
		
		randInt = new Random();
	}

	public Location spawn() {
		index = randInt.nextInt(itemLocations.size());
		location = itemLocations.get(index);
		itemLocations.remove(index);
		return location;
	}
	
	public void restore(Location restoreLocation){
		itemLocations.add(restoreLocation);
	}
}
