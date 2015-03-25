package com.jkjk.GameObjects;

public class Duration {

	private int duration; // This cooldown's time (in seconds)
	private boolean isCountdown; // Currently on cooldown?
	private long timeStart; // The time when the cooldown started ( in milli seconds)

	public Duration(int duration) {

		// get the time this cooldown should be on for
		this.duration = duration;

	}

	// Start cooldown
	public void startCountdown() {
		// on cooldown
		this.isCountdown = true;
		// time we started is now
		this.timeStart = System.currentTimeMillis();

	}
	
	public boolean isCountingDown(){
		return isCountdown;
	}

	// Update method; check if the cooldown is over
	public void update() {
		// why check if not on cooldown?
		if (this.isCountdown) {
			// check the difference between currentTime and when the cooldown started (milliseconds)
			long diff = (System.currentTimeMillis() - this.timeStart);
			// convert the cooldown int (in seconds) to milliseconds and see if the difference is greater or
			// equal to it
			if (diff >= duration) {
				// not on cooldown anymore :D
				this.isCountdown = false;

			}
		}
	}

}
