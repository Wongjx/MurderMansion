package com.jkjk.Host;

import java.util.ArrayList;
import java.util.Random;

public class WeaponSpawner {

	private ArrayList<Location> weaponLocations;
	private Random randInt;
	private Location location;
	private int index;

	WeaponSpawner() {
		weaponLocations = new ArrayList<Location>();
		weaponLocations.add(new Location(new float[] { 892.6f, 889.1f }));
		weaponLocations.add(new Location(new float[] { 310.4f, 730.2f }));
		weaponLocations.add(new Location(new float[] { 293.5f, 687.6f }));
		weaponLocations.add(new Location(new float[] { 160, 204 }));
		weaponLocations.add(new Location(new float[] { 709, 227 }));
		weaponLocations.add(new Location(new float[] { 763.8f, 222 }));
		weaponLocations.add(new Location(new float[] { 2237, 897 }));
		weaponLocations.add(new Location(new float[] { 2820, 869 }));
		weaponLocations.add(new Location(new float[] { 2675.7f, 229 }));
		weaponLocations.add(new Location(new float[] { 2140.6f, 576.4f }));
		weaponLocations.add(new Location(new float[] { 2073, 134 }));
		weaponLocations.add(new Location(new float[] { 3580.26f, 531 }));

		randInt = new Random();
	}

	public synchronized Location spawn() {
		index = randInt.nextInt(weaponLocations.size());
		location = weaponLocations.get(index);
		weaponLocations.remove(index);
		return location;
	}

	public synchronized void restore(Location restoreLocation) {
		weaponLocations.add(restoreLocation);
	}
}
