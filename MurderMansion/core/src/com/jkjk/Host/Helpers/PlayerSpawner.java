/**
 * 
 */
package com.jkjk.Host.Helpers;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author LeeJunXiang
 * 
 */
public class PlayerSpawner {
	private ArrayList<Location> playerLocations;
	private ArrayList<Float> playerAngle;
	private Random randInt;
	private float[] location;
	private int index;

	public PlayerSpawner() {
		playerLocations = new ArrayList<Location>();
		playerLocations.add(new Location(new float[] { 558.85f, 554.58f }));
		playerLocations.add(new Location(new float[] { 665.4f, 818f }));
		playerLocations.add(new Location(new float[] { 208f, 603.4f }));
		playerLocations.add(new Location(new float[] { 172.67f, 286.48f }));
		playerLocations.add(new Location(new float[] { 459f, 178.7f }));
		playerLocations.add(new Location(new float[] { 694.5f, 175.5f }));
		
		playerAngle = new ArrayList<Float>();
		playerAngle.add(3.927f);
		playerAngle.add(4.712f);
		playerAngle.add(0f);
		playerAngle.add(3.142f);
		playerAngle.add(5.498f);
		playerAngle.add(4.712f);

		randInt = new Random();
	}

	/**
	 * @return location and angle of player spawn. [0] x-coord, [1] y-coord, [2] angle
	 */
	public float[] getSpawnLocation() {
		location = new float[3];
		index = randInt.nextInt(playerLocations.size());
		location[0] = playerLocations.get(index).get()[0];
		location[1] = playerLocations.get(index).get()[1];
		location[2] = playerAngle.get(index);
		playerLocations.remove(index);

		return location;
	}
}
