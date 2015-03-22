package com.jkjk.GameObjects;

public class Duration {

	private int cooldown; // This cooldown's time (in seconds)
	private boolean isOnCooldown; // Currently on cooldown?
	private long timeStartCooldown; // The time when the cooldown started ( in milli seconds)

	public Duration(int cooldown) {

		// get the time this cooldown should be on for
		this.cooldown = cooldown;

	}

	// Start cooldown
	public void startCooldown() {
		// on cooldown
		this.isOnCooldown = true;
		// time we started is now
		this.timeStartCooldown = System.currentTimeMillis();

	}
	
	public boolean isOnCooldown(){
		return isOnCooldown;
	}

	// Update method; check if the cooldown is over
	public void update() {
		// why check if not on cooldown?
		if (this.isOnCooldown) {
			// check the difference between currentTime and when the cooldown started (milliseconds)
			long diff = (System.currentTimeMillis() - this.timeStartCooldown);
			// convert the cooldown int (in seconds) to milliseconds and see if the difference is greater or
			// equal to it
			if (diff >= cooldown) {
				// not on cooldown anymore :D
				this.isOnCooldown = false;

			}
		}
	}

}
