package com.jkjk.Input;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TouchPlayerInputController implements PlayerInputController {
	private final Touchpad touchpad;
	private final Vector2 movementVector;

	public TouchPlayerInputController(Touchpad touchpad) {
		this.touchpad = touchpad;
		movementVector = new Vector2();
	}

	@Override
	public void update(Viewport worldViewport) {
		movementVector.set(touchpad.getKnobPercentX(), touchpad.getKnobPercentY());
	}

	@Override
	public Vector2 getMovementVector() {
		return movementVector;
	}

	@Override
	public boolean isMovementActive() {
		return touchpad.isTouched() && !movementVector.isZero(0.01f);
	}

	@Override
	public boolean hasAimTarget() {
		return false;
	}

	@Override
	public Vector2 getAimTargetWorld() {
		return null;
	}

	@Override
	public boolean consumeUseWeapon() {
		return false;
	}

	@Override
	public boolean consumeUseAbility() {
		return false;
	}

	@Override
	public boolean consumeUseItem() {
		return false;
	}

	@Override
	public boolean consumePauseToggle() {
		return false;
	}

	@Override
	public void clearPendingActions() {
	}
}
