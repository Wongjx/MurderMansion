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
		weaponLocations.add(new Location(new float[] { 1, 1 }));
		weaponLocations.add(new Location(new float[] { 1, 1 }));
		weaponLocations.add(new Location(new float[] { 1, 1 }));
		weaponLocations.add(new Location(new float[] { 1, 1 }));
		weaponLocations.add(new Location(new float[] { 1, 1 }));
		weaponLocations.add(new Location(new float[] { 1, 1 }));
		weaponLocations.add(new Location(new float[] { 1, 1 }));
		weaponLocations.add(new Location(new float[] { 1, 1 }));
		weaponLocations.add(new Location(new float[] { 1, 1 }));
		
		randInt = new Random();
	}

	public Location spawn() {
		index = randInt.nextInt(weaponLocations.size());
		location = weaponLocations.get(index);
		weaponLocations.remove(index);
		return location;
	}
	
	public void restore(Location restoreLocation){
		weaponLocations.add(restoreLocation);
	}
}
