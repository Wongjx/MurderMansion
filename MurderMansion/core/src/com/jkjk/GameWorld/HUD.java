package com.jkjk.GameWorld;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.MMHelpers.AssetLoader;

public class HUD {

	private TextureRegionDrawable emptySlot, bat, disarmTrap;
	
	private float x, y, width, height;
	private ImageButton emptyItemSlot, emptyWeaponSlot, weaponButton, itemButton, disguiseToCiv, disguiseToMur;
	
	private GameCharacter player;

	public HUD(GameWorld gWorld, float scaleFactorX, float scaleFactorY) {
		emptySlot = AssetLoader.emptySlot;
		bat = AssetLoader.bat;
		disarmTrap = AssetLoader.disarmTrap;
		player = gWorld.getPlayer();
	}

	public ImageButton getEmptyItemSlot() {

		x = 485;
		y = 25;
		width = 50;
		height = 50;

		emptyItemSlot = new ImageButton(emptySlot);
		emptyItemSlot.setX(x);
		emptyItemSlot.setY(y);
		emptyItemSlot.setWidth(width);
		emptyItemSlot.setHeight(height);
		
		emptyItemSlot.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked on empty item slot");
			}
		});
		
		return emptyItemSlot;
	}

	public ImageButton getEmptyWeaponSlot() {

		x = 555;
		y = 95;
		width = 50;
		height = 50;

		emptyWeaponSlot = new ImageButton(emptySlot);
		emptyWeaponSlot.setX(x);
		emptyWeaponSlot.setY(y);
		emptyWeaponSlot.setWidth(width);
		emptyWeaponSlot.setHeight(height);
		
		emptyWeaponSlot.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked on empty weapon slot");
			}
		});
		
		return emptyWeaponSlot;
	}

	public ImageButton getBat() {

		x = 555;
		y = 95;
		width = 50;
		height = 50;

		weaponButton = new ImageButton(bat);
		weaponButton.setX(x);
		weaponButton.setY(y);
		weaponButton.setWidth(width);
		weaponButton.setHeight(height);
		
		weaponButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked on weapon button");
				player.useWeapon();
			}
		});
		
		return weaponButton;
	}

	public ImageButton getDisarmTrap() {

		x = 485;
		y = 25;
		width = 50;
		height = 50;

		itemButton = new ImageButton(disarmTrap);
		itemButton.setX(x);
		itemButton.setY(y);
		itemButton.setWidth(width);
		itemButton.setHeight(height);
		
		itemButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked on item button");
				player.useItem();
			}
		});
		
		return itemButton;
	}

	public ImageButton getKnife() {
		return weaponButton;
	}

	public ImageButton getTrap() {
		return itemButton;
	}

	public ImageButton getDisguiseToCiv() {
		return disguiseToCiv;
	}

	public ImageButton getDisguiseToMur() {
		return disguiseToMur;
	}

}
