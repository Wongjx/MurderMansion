package com.jkjk.GameWorld;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jkjk.MMHelpers.AssetLoader;

public class HUD implements InputProcessor {

	private TextureRegion emptySlot;
	private TextureRegion bat;
	private TextureRegion disarmTrap;

	public HUD(GameWorld gWorld, float scaleFactorX, float scaleFactorY) {
		emptySlot = AssetLoader.emptySlot;
		bat = AssetLoader.bat;
		disarmTrap = AssetLoader.disarmTrap;
	}

	public void drawEmptyItemSlot(SpriteBatch sb) {
		sb.draw(emptySlot, 485, 25);
	}

	public void drawEmptyWeaponSlot(SpriteBatch sb) {
		sb.draw(emptySlot, 555, 95);
	}

	public void drawBat(SpriteBatch sb) {
		sb.draw(bat, 555, 95);
	}

	public void drawDisarmTrap(SpriteBatch sb) {
		sb.draw(disarmTrap, 485, 25);
	}

	public void drawKnife(SpriteBatch sb) {

	}

	public void drawTrap(SpriteBatch sb) {

	}

	public void drawDisguiseToCiv(SpriteBatch sb) {

	}

	public void drawDisguiseToMur(SpriteBatch sb) {

	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		System.out.println("screenX: " + screenX + " screenY: " + screenY + " pointer: " + pointer
				+ " button: " + button);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

}
