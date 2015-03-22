package com.jkjk.GameObjects;

public class HitBoxExposure {
	private int hitBoxTime;
	private boolean hitBoxExposed;
	private long timeStartHitBox;

	public HitBoxExposure(int hitBoxTime) {
		this.hitBoxTime = hitBoxTime;
	}

	public void startExposure() {
		this.hitBoxExposed = true;
		this.timeStartHitBox = System.currentTimeMillis();

	}

	public boolean isHitBoxExposed() {
		return hitBoxExposed;
	}

	public void update() {
		if (this.hitBoxExposed) {
			long diff = (System.currentTimeMillis() - this.timeStartHitBox);
			if (diff >= hitBoxTime) {
				this.hitBoxExposed = false;

			}
		}
	}
}
