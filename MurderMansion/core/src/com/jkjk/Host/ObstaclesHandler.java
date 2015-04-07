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
		obstacleLocations.add(new Location(new float[] { 915.2f, 511.8f })); // MAIN DOOR. ONLY DESTROY WHEN AT 0MIN
		obstacleLocations.add(new Location(new float[] { 736.5f, 809.4f }));
		obstacleLocations.add(new Location(new float[] { 185.2f, 476.5f }));
		obstacleLocations.add(new Location(new float[] { 308.7f, 244.8f }));
		obstacleLocations.add(new Location(new float[] { 750.9f, 269.6f }));
		obstacleLocations.add(new Location(new float[] { 2151.3f, 685.85f }));
		obstacleLocations.add(new Location(new float[] { 2640.4f, 268 }));
		obstacleLocations.add(new Location(new float[] { 2802.6f, 490.1f }));

		randInt = new Random();
	}

	/**
	 * Randomly destroys an obstacle. Main door obstacle will only be destroyed at the last of 8 obstacles.
	 * 
	 * @return location of obstacle.
	 */
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
