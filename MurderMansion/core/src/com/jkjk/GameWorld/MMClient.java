package com.jkjk.GameWorld;

/**
 * @author LeeJunXiang MMClient listens to input from the Server by the host. Inputs include sharable data
 *         such as player position, item spawns and player status. MMClient will also output to the server the
 *         changes made by the player.
 * 
 *         More importantly, client-side processing will handle all actions by the player (movement, contact).
 *         The CONSEQUENCE of the action will be passed to the server, which will retransmit the results to
 *         all other clients. Consequences include the removal of an item when picking it up, or change in
 *         body position due to movement.
 * 
 */
public class MMClient {

	MMClient() {
		// Attempt to connect to Server
	}

	public void update() {
		playerPositions();
		playerAngles();
		itemLocations();
		weaponLocations();
		weaponPartLocations();
		trapLocations();
	}

	public void render() {
	}

	private void playerPositions() {

	}

	private void playerAngles() {

	}

	private void itemLocations() {

	}

	private void weaponLocations() {

	}

	private void weaponPartLocations() {

	}

	private void trapLocations() {

	}

}
