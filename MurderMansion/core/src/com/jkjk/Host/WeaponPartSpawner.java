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
		weaponPartLocations.add(new Location(new float[] { 1, 1 }));
		weaponPartLocations.add(new Location(new float[] { 1, 1 }));
		weaponPartLocations.add(new Location(new float[] { 1, 1 }));
		weaponPartLocations.add(new Location(new float[] { 1, 1 }));
		weaponPartLocations.add(new Location(new float[] { 1, 1 }));
		weaponPartLocations.add(new Location(new float[] { 1, 1 }));
		weaponPartLocations.add(new Location(new float[] { 1, 1 }));
		weaponPartLocations.add(new Location(new float[] { 1, 1 }));
		weaponPartLocations.add(new Location(new float[] { 1, 1 }));
		
		randInt = new Random();
	}

	public Location spawn() {
		index = randInt.nextInt(weaponPartLocations.size());
		location = weaponPartLocations.get(index);
		weaponPartLocations.remove(index);
		return location;
	}
	
	public void restore(Location restoreLocation){
		weaponPartLocations.add(restoreLocation);
	}
}
