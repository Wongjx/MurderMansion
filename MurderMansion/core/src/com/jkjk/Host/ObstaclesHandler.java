/**
 * 
 */
package com.jkjk.Host;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author LeeJunXiang
 * 
 */
public class ObstaclesHandler {

	private ArrayList<Location> obstacleLocations;
	private Random randInt;
	private Location location;
	private int index;

	ObstaclesHandler() {
		obstacleLocations = new ArrayList<Location>();
		obstacleLocations.add(new Location(new float[] { 0, 0 })); // MAIN DOOR. ONLY DESTROY WHEN AT 0MIN
		obstacleLocations.add(new Location(new float[] { 0, 0 }));
		obstacleLocations.add(new Location(new float[] { 0, 0 }));
		obstacleLocations.add(new Location(new float[] { 0, 0 }));

		randInt = new Random();
	}

	public synchronized Location destroyObstacle() {
		if (obstacleLocations.size() == 1) {
			location = obstacleLocations.get(0);
			obstacleLocations.remove(index);
		} else if (obstacleLocations.size() > 1) {
			index = randInt.nextInt(obstacleLocations.size() - 1) + 1;
			location = obstacleLocations.get(index);
			obstacleLocations.remove(index);
		} else {
			location = null;
		}
		return location;
	}

	public synchronized ArrayList<Location> getObstacles() {
		ArrayList<Location> release = new ArrayList<Location>();
		for (int i = 0; i < obstacleLocations.size(); i++) {
			release.add(obstacleLocations.get(i));
		}
		return release;
	}
}
