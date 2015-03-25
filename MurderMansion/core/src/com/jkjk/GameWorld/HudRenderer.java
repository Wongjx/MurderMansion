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

/**
 * HudRenderer contains the rendering of all HUD icons, such as touchpad, item slots and timers.
 * 
 * @author LeeJunXiang
 */
public class HudRenderer {

	private TextureRegionDrawable civ_bat, civ_item, civ_dash;
	private Texture emptySlot;
	private Actor emptySlot_actor;
	private Texture timebox;
	private Actor timebox_actor;
	private Texture civ_profile;
	private Actor civ_profile_actor;
	private BitmapFont font;
	private String time;
	private Float playTime;

	private float x, y, width, height;
	private ImageButton weaponButton, itemButton, dashButton, disguiseToCiv, disguiseToMur;

	private GameCharacter player;
	private SpriteBatch batch;
	private OrthographicCamera hudCam;
	private Stage stage;

	private Touchpad touchpad;
	private Drawable touchKnob;

	/**
	 * Constructs the link from the Box2D world created in GameWorld to HudRenderer. Allows rendering of the
	 * player's touchpad, item slots, time left and weapon parts collected based on what had happened in the
	 * game world.
	 * 
	 * @param gWorld
	 *            Link to the GameWorld, accessing box2d objected created in the world.
	 * @param gameWidth
	 *            Accesses the virtual game width.
	 * @param gameHeight
	 *            Accesses the virtual game height.
	 */
	public HudRenderer(GameWorld gWorld, float gameWidth, float gameHeight) {
		initAssets(gameWidth, gameHeight);

		player = gWorld.getPlayer();

		// countdown
		playTime = 240.0f;

		hudCam = new OrthographicCamera();
		hudCam.setToOrtho(false, gameWidth, gameHeight);
		batch = new SpriteBatch();

		// Create a Stage and add TouchPad
		stage = new Stage(new ExtendViewport(gameWidth, gameHeight, hudCam), batch);
		stage.addActor(touchpad);
		stage.addActor(getTimebox());
		stage.addActor(getProfile());
		stage.addActor(getEmptySlot());
		stage.addActor(getPanic());
		Gdx.input.setInputProcessor(stage);
	}

	/**
	 * Loads images used for the HUD.
	 * 
	 * @param w
	 *            Game Width.
	 * @param h
	 *            Game Height.
	 */
	private void initAssets(float w, float h) {
		emptySlot = AssetLoader.emptySlot;
		civ_bat = AssetLoader.civ_weapon_bat_draw;
		civ_item = AssetLoader.civ_item_draw;
		civ_dash = AssetLoader.civ_dash_draw;

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

	/**
	 * Renders all HUD images on the player's screen
	 * 
	 * @param delta
	 *            The time between each render.
	 */
	public void render(float delta) {

		batch.begin();
		batch.draw(timebox, 55, 280);
		batch.draw(civ_profile, 180, 282);
		batch.draw(emptySlot, 485, 25);
		font.draw(batch, getTime(), 75, 330);
		batch.end();

		if (player.getItemChange())
			itemCheck();
		if (player.getWeaponChange())
			weaponCheck();

		stage.draw(); // Draw touchpad
		stage.act(Gdx.graphics.getDeltaTime()); // Acts stage at deltatime

	}

	/**
	 * @return Time left in the game
	 */
	public String getTime() {

		playTime -= Gdx.graphics.getDeltaTime(); //
		int minutes = (int) Math.floor(playTime / 60.0f);
		int seconds = (int) Math.floor(playTime - minutes * 60);
		time = String.format("%d:%02d", minutes, seconds);

		return time;
	}

	/**
	 * @return Actor for the box containing the time.
	 */
	public Actor getTimebox() {

		timebox_actor = new Actor();
		timebox_actor.draw(batch, 1);
		timebox_actor.setName("timebox actor");

		return timebox_actor;
	}

	/**
	 * @return Actor for displaying the profile of the player.
	 */
	public Actor getProfile() {

		civ_profile_actor = new Actor();
		civ_profile_actor.draw(batch, 1);
		civ_profile_actor.setName("civ profile actor");

		return civ_profile_actor;
	}

	/**
	 * When a change in the player's item is detected, itemCheck() will be called, setting itemChange to false
	 * and updating the new item for the player's item slot.
	 */
	private void itemCheck() {
		player.setItemChange(false);
		if (player.getItem() != null) {
			for (Actor actors : stage.getActors()) {
				if (actors.getName().equals("Empty Item Slot"))
					actors.remove();
			}
			if (player.getType().equals("Civilian"))
				stage.addActor(getDisarmTrap());
			else if (player.getType().equals("Murderer"))
				stage.addActor(getTrap());
		} else {
			for (Actor actors : stage.getActors()) {
				if (actors.getName().equals("Item Button"))
					actors.remove();
			}
			// stage.addActor(getEmptyItemSlot());
		}
	}

	/**
	 * When a change in the player's weapon is detected, weaponCheck() will be called, setting weaponChange to
	 * false and updating the new item for the player's weapon slot.
	 */
	private void weaponCheck() {
		player.setWeaponChange(false);
		if (player.getWeapon() != null) {
			for (Actor actors : stage.getActors()) {
				if (actors.getName().equals("Empty Weapon Slot"))
					actors.remove();
			}
			if (player.getType().equals("Civilian"))
				stage.addActor(getBat());
			else if (player.getType().equals("Murderer"))
				stage.addActor(getKnife());
		} else {
			for (Actor actors : stage.getActors()) {
				if (actors.getName().equals("Weapon Button"))
					actors.remove();
			}
			// stage.addActor(getEmptyWeaponSlot());
		}
	}

	/**
	 * Empty slot occurs when player does not have an item/weapon/ability.
	 * 
	 * @return Actor for the empty slot
	 */
	public Actor getEmptySlot() {

		emptySlot_actor = new Actor();
		emptySlot_actor.draw(batch, 1);
		emptySlot_actor.setName("empty slot");

		return emptySlot_actor;
	}

	/**
	 * Creates the actor for the bat slot at 505,41.
	 * 
	 * @return Actor for Bat slot
	 */
	public ImageButton getBat() {

		x = 505;
		y = 41;

		weaponButton = new ImageButton(civ_bat);
		weaponButton.setX(x);
		weaponButton.setY(y);
		weaponButton.setName("Weapon Button");

		weaponButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked on weapon button");
				player.useWeapon();
			}
		});

		return weaponButton;
	}

	/**
	 * Creates the actor for the disarm trap slot at 567,43.
	 * 
	 * @return Actor for Disarm Trap slot
	 */
	public ImageButton getDisarmTrap() {

		x = 567;
		y = 43;

		itemButton = new ImageButton(civ_item);
		itemButton.setX(x);
		itemButton.setY(y);
		itemButton.setName("Item Button");

		itemButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked on item button");
				player.useItem();
			}
		});

		return itemButton;
	}

	/**
	 * Creates the actor for the panic slot at 528,100.
	 * 
	 * @return Actor for Panic slot
	 */
	public ImageButton getPanic() {

		x = 528;
		y = 100;

		dashButton = new ImageButton(civ_dash);
		dashButton.setX(x);
		dashButton.setY(y);
		dashButton.setName("Panic");

		dashButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked on panic button");
				// Used to check character position FOR TESTING
				System.out.println(player.getBody().getPosition());
				player.useAbility();
			}
		});

		return dashButton;
	}

	/**
	 * Creates the actor for the knife slot at 505,41.
	 * 
	 * @return Actor for Knife slot
	 */
	public ImageButton getKnife() {
		x = 505;
		y = 41;

		itemButton = new ImageButton(civ_bat);
		itemButton.setX(x);
		itemButton.setY(y);
		itemButton.setName("Weapon Button");

		dashButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked on weapon button");
				player.useWeapon();
			}
		});

		return itemButton;
	}

	/**
	 * Creates the actor for the trap slot at 567,43.
	 * 
	 * @return Actor for Trap slot
	 */
	public ImageButton getTrap() {

		x = 567;
		y = 43;

		itemButton = new ImageButton(civ_item);
		itemButton.setX(x);
		itemButton.setY(y);
		itemButton.setName("Item Button");

		itemButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked on item button");
				player.useItem();
			}
		});

		return itemButton;
	}

	/**
	 * Creates the actor for the disguise to civilian slot at 528,100.
	 * 
	 * @return Actor for Disguise to Civilian slot
	 */
	public ImageButton getDisguiseToCiv() {
		x = 528;
		y = 100;

		dashButton = new ImageButton(civ_dash);
		dashButton.setX(x);
		dashButton.setY(y);
		dashButton.setName("Disguise to civilian");

		dashButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked on panic button");
				// Used to check character position FOR TESTING
				System.out.println(player.getBody().getPosition());
				player.useAbility();
			}
		});

		return disguiseToCiv;
	}

	/**
	 * Creates the actor for the disguise to murderer slot at 528,100.
	 * 
	 * @return Actor for Disguise to Murderer slot
	 */
	public ImageButton getDisguiseToMur() {
		x = 528;
		y = 100;

		dashButton = new ImageButton(civ_dash);
		dashButton.setX(x);
		dashButton.setY(y);
		dashButton.setName("Disguise to murderer");

		dashButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked on panic button");
				// Used to check character position FOR TESTING
				System.out.println(player.getBody().getPosition());
				player.useAbility();
			}
		});

		return disguiseToMur;
	}

	/**
	 * Releases the resources held by objects or images loaded.
	 */
	public void hudDispose() {
		stage.dispose();
	}

}
