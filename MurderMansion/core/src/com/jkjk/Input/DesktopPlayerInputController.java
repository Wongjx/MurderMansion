package com.jkjk.Input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

public class DesktopPlayerInputController implements PlayerInputController {
	private final Vector2 movementVector;
	private final Vector2 aimTargetWorld;
	private final Vector3 mousePosition;

	private boolean weaponPending;
	private boolean abilityPending;
	private boolean itemPending;
	private boolean pausePending;

	private boolean previousWeaponPressed;
	private boolean previousAbilityPressed;
	private boolean previousItemPressed;
	private boolean previousPausePressed;

	public DesktopPlayerInputController() {
		movementVector = new Vector2();
		aimTargetWorld = new Vector2();
		mousePosition = new Vector3();
	}

	@Override
	public void update(Viewport worldViewport) {
		float movementX = 0f;
		float movementY = 0f;

		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			movementX -= 1f;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			movementX += 1f;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			movementY -= 1f;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			movementY += 1f;
		}

		movementVector.set(movementX, movementY);
		if (movementVector.len2() > 1f) {
			movementVector.nor();
		}

		if (worldViewport != null) {
			mousePosition.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
			worldViewport.unproject(mousePosition);
			aimTargetWorld.set(mousePosition.x, mousePosition.y);
		}

		boolean weaponPressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
		boolean abilityPressed = Gdx.input.isButtonPressed(Input.Buttons.RIGHT);
		boolean itemPressed = Gdx.input.isKeyPressed(Input.Keys.E);
		boolean pausePressed = Gdx.input.isKeyPressed(Input.Keys.ESCAPE);

		weaponPending |= weaponPressed && !previousWeaponPressed;
		abilityPending |= abilityPressed && !previousAbilityPressed;
		itemPending |= itemPressed && !previousItemPressed;
		pausePending |= pausePressed && !previousPausePressed;

		previousWeaponPressed = weaponPressed;
		previousAbilityPressed = abilityPressed;
		previousItemPressed = itemPressed;
		previousPausePressed = pausePressed;
	}

	@Override
	public Vector2 getMovementVector() {
		return movementVector;
	}

	@Override
	public boolean isMovementActive() {
		return !movementVector.isZero(0.01f);
	}

	@Override
	public boolean hasAimTarget() {
		return true;
	}

	@Override
	public Vector2 getAimTargetWorld() {
		return aimTargetWorld;
	}

	@Override
	public boolean consumeUseWeapon() {
		boolean result = weaponPending;
		weaponPending = false;
		return result;
	}

	@Override
	public boolean consumeUseAbility() {
		boolean result = abilityPending;
		abilityPending = false;
		return result;
	}

	@Override
	public boolean consumeUseItem() {
		boolean result = itemPending;
		itemPending = false;
		return result;
	}

	@Override
	public boolean consumePauseToggle() {
		boolean result = pausePending;
		pausePending = false;
		return result;
	}

	@Override
	public void clearPendingActions() {
		weaponPending = false;
		abilityPending = false;
		itemPending = false;
		pausePending = false;
	}
}
