package com.jkjk.Input;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public interface PlayerInputController {
	void update(Viewport worldViewport);

	Vector2 getMovementVector();

	boolean isMovementActive();

	boolean hasAimTarget();

	Vector2 getAimTargetWorld();

	boolean consumeUseWeapon();

	boolean consumeUseAbility();

	boolean consumeUseItem();

	boolean consumePauseToggle();

	void clearPendingActions();
}
