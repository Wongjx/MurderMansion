package com.jkjk.Host;

import java.util.ArrayList;
import java.util.Random;

public class WeaponPartSpawner {

	private ArrayList<Location> weaponPartLocations;
	private Random randInt;
	private Location location;
	private int index;

	WeaponPartSpawner() {
		weaponPartLocations = new ArrayList<Location>();
		weaponPartLocations.add(new Location(new float[] { 191, 804 }));
		weaponPartLocations.add(new Location(new float[] { 870.8f, 661.9f }));
		weaponPartLocations.add(new Location(new float[] { 822, 144 }));
		weaponPartLocations.add(new Location(new float[] { 921, 84.4f }));
		weaponPartLocations.add(new Location(new float[] { 2071, 773 }));
		weaponPartLocations.add(new Location(new float[] { 2655, 745 }));
		weaponPartLocations.add(new Location(new float[] { 2498.2f, 229 }));
		weaponPartLocations.add(new Location(new float[] { 2238, 129 }));
		weaponPartLocations.add(new Location(new float[] { 2135.5f, 451.6f }));
		weaponPartLocations.add(new Location(new float[] { 2207.6f, 684.7f }));

		randInt = new Random();
	}

	public synchronized Location spawn() {
		index = randInt.nextInt(weaponPartLocations.size());
		location = weaponPartLocations.get(index);
		weaponPartLocations.remove(index);
		return location;
	}

	public synchronized void restore(Location restoreLocation) {
		weaponPartLocations.add(restoreLocation);
	}
}
