package com.jkjk.Host.Helpers;

import java.util.ArrayList;
import java.util.Random;

public class ItemSpawner {

	private ArrayList<Location> itemLocations;
	private Random randInt;
	private Location location;
	private int index;

	public ItemSpawner() {
		itemLocations = new ArrayList<Location>();
		itemLocations.add(new Location(new float[] { 310.76f, 885.68f }));
		itemLocations.add(new Location(new float[] { 355.6f, 749.3f }));
		itemLocations.add(new Location(new float[] { 705.21f, 751.4f }));
		itemLocations.add(new Location(new float[] { 774.8f, 729.5f }));
		itemLocations.add(new Location(new float[] { 290.6f, 497.7f }));
		itemLocations.add(new Location(new float[] { 267.7f, 425 }));
		itemLocations.add(new Location(new float[] { 317.9f, 296 }));
		itemLocations.add(new Location(new float[] { 375, 161 }));
		itemLocations.add(new Location(new float[] { 521.8f, 205 }));
		itemLocations.add(new Location(new float[] { 770, 149 }));
		itemLocations.add(new Location(new float[] { 2669, 897 }));
		itemLocations.add(new Location(new float[] { 2234, 736 }));
		itemLocations.add(new Location(new float[] { 2692, 657.9f }));
		itemLocations.add(new Location(new float[] { 2728, 513 }));
		itemLocations.add(new Location(new float[] { 2818, 337.6f }));
		itemLocations.add(new Location(new float[] { 2215.4f, 556 }));
		itemLocations.add(new Location(new float[] { 2183.7f, 342 }));
		itemLocations.add(new Location(new float[] { 2171.5f, 224.8f }));
		itemLocations.add(new Location(new float[] { 2496, 153.3f }));
		itemLocations.add(new Location(new float[] { 2627.6f, 151.9f }));
		itemLocations.add(new Location(new float[] { 2794, 199 }));
		itemLocations.add(new Location(new float[] { 3752.4f, 473 }));
		itemLocations.add(new Location(new float[] { 3600,413.8f }));
		itemLocations.add(new Location(new float[] { 3310.1f, 395.8f }));
		itemLocations.add(new Location(new float[] { 3310.1f, 513.5f }));
		itemLocations.add(new Location(new float[] { 896.9f, 576f }));
		itemLocations.add(new Location(new float[] { 896.9f, 447.2f }));

		randInt = new Random();
	}

	public synchronized Location spawn() {
		index = randInt.nextInt(itemLocations.size());
		location = itemLocations.get(index);
		itemLocations.remove(index);
		return location;
	}

	public synchronized void restore(Location restoreLocation) {
		itemLocations.add(restoreLocation);
	}
}
