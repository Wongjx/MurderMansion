package com.jkjk.Input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TouchPlayerInputController implements PlayerInputController {
	private final Touchpad touchpad;
	private final Vector2 movementVector;
	private final Vector2 aimTargetWorld;
	private Viewport worldViewport;
	private final Vector2 playerWorldPosition;
	private final Vector2 aimAnchorScreen;
	private final Vector2 aimCurrentScreen;
	private final Vector2 aimDirection;
	private int freeAimPointer;
	private int actionAimPointer;
	private boolean actionAimDragged;

	public TouchPlayerInputController(Touchpad touchpad) {
		this.touchpad = touchpad;
		movementVector = new Vector2();
		aimTargetWorld = new Vector2();
		playerWorldPosition = new Vector2();
		aimAnchorScreen = new Vector2();
		aimCurrentScreen = new Vector2();
		aimDirection = new Vector2();
		freeAimPointer = -1;
		actionAimPointer = -1;
		actionAimDragged = false;
	}

	@Override
	public void update(Viewport worldViewport, Vector2 playerPosition) {
		this.worldViewport = worldViewport;
		if (playerPosition != null) {
			playerWorldPosition.set(playerPosition);
		}
		movementVector.set(touchpad.getKnobPercentX(), touchpad.getKnobPercentY());
		updateAimTarget();
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
		return freeAimPointer != -1 || (actionAimPointer != -1 && actionAimDragged);
	}

	@Override
	public Vector2 getAimTargetWorld() {
		return aimTargetWorld;
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
	public boolean isWeaponControlHeld() {
		return false;
	}

	@Override
	public boolean isAbilityControlHeld() {
		return false;
	}

	@Override
	public boolean isItemControlHeld() {
		return false;
	}

	@Override
	public void clearPendingActions() {
	}

	public void beginFreeAim(int pointer) {
		if (actionAimPointer != -1) {
			return;
		}
		freeAimPointer = pointer;
		setPointerPosition(pointer, aimAnchorScreen);
		aimCurrentScreen.set(aimAnchorScreen);
		updateAimTarget();
	}

	public void updateFreeAim(int pointer) {
		if (pointer == freeAimPointer && actionAimPointer == -1) {
			setPointerPosition(pointer, aimCurrentScreen);
			updateAimTarget();
		}
	}

	public void endFreeAim(int pointer) {
		if (pointer == freeAimPointer) {
			freeAimPointer = -1;
		}
	}

	public void beginActionAim(int pointer) {
		actionAimPointer = pointer;
		actionAimDragged = false;
		setPointerPosition(pointer, aimAnchorScreen);
		aimCurrentScreen.set(aimAnchorScreen);
	}

	public void updateActionAim(int pointer) {
		if (pointer == actionAimPointer) {
			actionAimDragged = true;
			setPointerPosition(pointer, aimCurrentScreen);
			updateAimTarget();
		}
	}

	public void endActionAim(int pointer) {
		if (pointer == actionAimPointer) {
			actionAimPointer = -1;
			actionAimDragged = false;
		}
	}

	public void clearAim() {
		freeAimPointer = -1;
		actionAimPointer = -1;
		actionAimDragged = false;
	}

	public void clearActionAim() {
		actionAimPointer = -1;
		actionAimDragged = false;
	}

	private void updateAimTarget() {
		if (worldViewport == null || !hasAimTarget()) {
			return;
		}
		aimDirection.set(aimCurrentScreen.x - aimAnchorScreen.x, aimAnchorScreen.y - aimCurrentScreen.y);
		if (aimDirection.isZero(4f)) {
			return;
		}
		aimDirection.nor().scl(100f);
		aimTargetWorld.set(playerWorldPosition.x + aimDirection.x, playerWorldPosition.y + aimDirection.y);
	}

	private void setPointerPosition(int pointer, Vector2 target) {
		target.set(Gdx.input.getX(pointer), Gdx.input.getY(pointer));
	}
}
