package com.jkjk.GameWorld;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jkjk.MMHelpers.AssetLoader;

public class HUD implements InputProcessor {

	private TextureRegion emptySlot;
	private TextureRegion bat;
	private TextureRegion disarmTrap;

	public HUD() {
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

	private void drawDisguiseToCiv(SpriteBatch sb) {

	}

	private void drawDisguiseToMur(SpriteBatch sb) {

	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
