package com.jkjk.GameWorld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.MMHelpers.AssetLoader;

public class HudRenderer {

	private TextureRegionDrawable emptySlot, bat, disarmTrap;
	private Texture timebox;
	private Actor timebox_actor;
	private Texture civ_profile;
	private Actor civ_profile_actor;
	private BitmapFont font;
	private String time;
	private Float playTime;
	
	private float x, y, width, height;
	private ImageButton emptyItemSlot, emptyWeaponSlot, weaponButton, itemButton, disguiseToCiv, disguiseToMur;
	
	private GameCharacter player;
	private GameWorld gWorld;
	
	private float gameWidth, gameHeight;
	
	private SpriteBatch batch;
	private OrthographicCamera hudCam;
	private Stage stage;
	
	private Touchpad touchpad;
	private Drawable touchKnob;

	public HudRenderer(GameWorld gWorld, float gameWidth, float gameHeight) {
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		initAssets(gameWidth, gameHeight);

		this.gWorld = gWorld;
		player = gWorld.getPlayer();
		
		// countdown 
		playTime = 180.0f;
		
		hudCam = new OrthographicCamera();
		hudCam.setToOrtho(false, gameWidth, gameHeight);
		batch = new SpriteBatch();
		
		// Create a Stage and add TouchPad
		stage = new Stage(new ExtendViewport(gameWidth, gameHeight, hudCam), batch);
		stage.addActor(touchpad);
		stage.addActor(getTimebox());
		stage.addActor(getProfile());
		stage.addActor(getEmptyItemSlot());
		stage.addActor(getEmptyWeaponSlot());
		Gdx.input.setInputProcessor(stage);
	}
	
	private void initAssets(float w, float h){
		emptySlot = AssetLoader.emptySlot;
		bat = AssetLoader.bat;
		disarmTrap = AssetLoader.disarmTrap;

		// Touchpad stuff
		touchpad = AssetLoader.touchpad;
		touchpad.setName("touchpad");
		touchpad.setBounds(w / 14, h / 14, w / 5, w / 5);
		touchKnob = AssetLoader.touchKnob;
		touchKnob.setMinHeight(touchpad.getHeight() / 4);
		touchKnob.setMinWidth(touchpad.getWidth() / 4);
		
		// Top Left of the screen
		timebox = AssetLoader.time;
		civ_profile = AssetLoader.civ_profile;
		font = AssetLoader.basker32blackTime;
		
	}
	
	public void render(float delta){

		batch.begin();
		batch.draw(timebox, 55, 280);
		batch.draw(civ_profile, 180, 282);
		font.draw(batch,getTime(), 75, 330);
		batch.end();
		
		if (player.getItemChange())
			itemCheck();
		if (player.getWeaponChange())
			weaponCheck();
		
		stage.draw(); // Draw touchpad
		stage.act(Gdx.graphics.getDeltaTime()); // Acts stage at deltatime
		
	}
	
	public String getTime(){
		
		playTime -= Gdx.graphics.getDeltaTime(); //
		int minutes = (int) Math.floor(playTime/60.0f);
		int seconds = (int) Math.floor(playTime - minutes*60);
		time = String.format("%d:%02d", minutes, seconds);
		
		return time;
	}
	

	private void itemCheck() {
		player.setItemChange(false);
		if (player.getItem() != null) {
			for (Actor actors : stage.getActors()) {
				if (actors.getName().equals("Empty Item Slot"))
					actors.remove();
			}
			if (player.getName().equals("Civilian"))
				stage.addActor(getDisarmTrap());
			else if (player.getName().equals("Murderer"))
				stage.addActor(getTrap());
		} else {
			for (Actor actors : stage.getActors()) {
				if (actors.getName().equals("Item Button"))
					actors.remove();
			}
			stage.addActor(getEmptyItemSlot());
		}
	}

	private void weaponCheck() {
		player.setWeaponChange(false);
		if (player.getWeapon() != null) {
			for (Actor actors : stage.getActors()) {
				if (actors.getName().equals("Empty Weapon Slot"))
					actors.remove();
			}
			if (player.getName().equals("Civilian"))
				stage.addActor(getBat());
			else if (player.getName().equals("Murderer"))
				stage.addActor(getKnife());
		} else {
			for (Actor actors : stage.getActors()) {
				if (actors.getName().equals("Weapon Button"))
					actors.remove();
			}
			stage.addActor(getEmptyWeaponSlot());
		}
	}
	
	public Actor getTimebox(){
		
		timebox_actor = new Actor();
		timebox_actor.draw(batch, 1);
		timebox_actor.setName("timebox actor");
		
		return timebox_actor;
	}
	
	public Actor getProfile(){
		
		civ_profile_actor = new Actor();
		civ_profile_actor.draw(batch, 1);
		civ_profile_actor.scaleBy(3);
		civ_profile_actor.setName("civ profile actor");
		
		return civ_profile_actor;
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
		emptyItemSlot.setName("Empty Item Slot");
		
		emptyItemSlot.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked on empty item slot");
				// Used to check character position FOR TESTING
				System.out.println(player.getBody().getPosition());
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
		emptyWeaponSlot.setName("Empty Weapon Slot");
		
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
		weaponButton.setName("Weapon Button");
		
		weaponButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked on weapon button");
				player.useWeapon(gWorld);
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
		itemButton.setName("Item Button");
		
		itemButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked on item button");
				player.useItem(gWorld);
			}
		});
		
		return itemButton;
	}

	public ImageButton getKnife() {
		x = 555;
		y = 95;
		width = 50;
		height = 50;

		weaponButton = new ImageButton(bat);
		weaponButton.setX(x);
		weaponButton.setY(y);
		weaponButton.setWidth(width);
		weaponButton.setHeight(height);
		weaponButton.setName("Weapon Button");
		
		weaponButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked on weapon button");
				player.useWeapon(gWorld);
			}
		});
		
		return weaponButton;
	}

	public ImageButton getTrap() {

		x = 485;
		y = 25;
		width = 50;
		height = 50;

		itemButton = new ImageButton(disarmTrap);
		itemButton.setX(x);
		itemButton.setY(y);
		itemButton.setWidth(width);
		itemButton.setHeight(height);
		itemButton.setName("Item Button");
		
		itemButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked on item button");
				player.useItem(gWorld);
			}
		});
		
		return itemButton;
	}

	public ImageButton getDisguiseToCiv() {
		return disguiseToCiv;
	}

	public ImageButton getDisguiseToMur() {
		return disguiseToMur;
	}
	
	public void hudDispose(){
		stage.dispose();
	}

}
