package com.jkjk.GameWorld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.jkjk.GameObjects.Characters.Murderer;
import com.jkjk.MMHelpers.AssetLoader;

/**
 * HudRenderer contains the rendering of all HUD icons, such as touchpad, item slots and timers.
 * 
 * @author LeeJunXiang
 */
public class HudRenderer {
	private static HudRenderer instance;

	private GameWorld gWorld;
	private GameCharacter player;

	private TextureRegionDrawable civ_bat, civ_item, civ_dash, mur_knife, mur_item, mur_CtM, mur_MtC;
	private Texture emptySlot;
	private Actor emptySlot_actor;
	private Texture timebox;
	private Actor timebox_actor;
	private Texture weapon_parts_counter;
	private Actor counter_actor;
	private BitmapFont font;
	private String time;
	private Float playTime;

	private float x, y;
	private ImageButton weaponButton, itemButton, dashButton, disguiseToCiv, disguiseToMur, hauntButton;
	private boolean clickable;

	private SpriteBatch batch;
	private OrthographicCamera hudCam;
	private Stage stage;

	private Touchpad touchpad;
	private Drawable touchKnob;

	private boolean PanicCD;
	private boolean DisguiseCD;
	//private boolean ItemsCD;
	private boolean WeaponsCD;
	private Animation PanicCoolDownAnimation;
	private Animation DisguiseCoolDownAnimation;
	private Animation WeaponsCoolDownAnimation;
	private float PanicAnimationRunTime;
	private float DisguiseAnimationRunTime;
	private float WeaponsAnimationRunTime;
	
	private TextureRegion prohibitedButton = AssetLoader.prohibitedButton;

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
	private HudRenderer(GameWorld gWorld, float gameWidth, float gameHeight) {
		initAssets(gameWidth, gameHeight);
		this.gWorld = gWorld;

		// countdown
		playTime = 240.0f;

		hudCam = new OrthographicCamera();
		hudCam.setToOrtho(false, gameWidth, gameHeight);
		batch = new SpriteBatch();

		// Create a Stage and add TouchPad
		stage = new Stage(new ExtendViewport(gameWidth, gameHeight, hudCam), batch);
		stage.addActor(touchpad);
		stage.addActor(getTimebox());
		stage.addActor(getWeaponPartsCounter());
		stage.addActor(getEmptySlot());
		abilityCheck();

		Gdx.input.setInputProcessor(stage);
	}

	public static HudRenderer getInstance(GameWorld gWorld, float gameWidth, float gameHeight) {
		if (instance == null) {
			instance = new HudRenderer(gWorld, gameWidth, gameHeight);
		}
		return instance;
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
		PanicCoolDownAnimation = AssetLoader.PanicCoolDownAnimation;
		DisguiseCoolDownAnimation = AssetLoader.DisguiseCoolDownAnimation;
		WeaponsCoolDownAnimation = AssetLoader.WeaponsCoolDownAnimation;
		PanicCD = DisguiseCD = WeaponsCD = false;
		WeaponsAnimationRunTime = DisguiseAnimationRunTime = PanicAnimationRunTime = 0f;
		civ_bat = AssetLoader.civ_weapon_bat_draw;
		civ_item = AssetLoader.civ_item_draw;
		civ_dash = AssetLoader.civ_dash_draw;
		mur_knife = AssetLoader.mur_weapon_draw;
		mur_item = AssetLoader.mur_item_draw;
		mur_CtM = AssetLoader.mur_swap_M_draw;
		mur_MtC = AssetLoader.mur_swap_C_draw;

		// Touchpad stuff
		touchpad = AssetLoader.touchpad;
		touchpad.setName("touchpad");
		touchpad.setBounds(w / 14, h / 14, w / 5, w / 5);
		touchKnob = AssetLoader.touchKnob;
		touchKnob.setMinHeight(touchpad.getHeight() / 4);
		touchKnob.setMinWidth(touchpad.getWidth() / 4);

		// Top Left of the screen
		timebox = AssetLoader.time;
		weapon_parts_counter = AssetLoader.weapon_parts_counter;
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
		batch.draw(weapon_parts_counter, 480, 235);
		batch.draw(emptySlot, 480, 22, 120, 120);
		font.draw(batch, getTime(), 75, 330);
		if(player.getType().equals("Civilian")||player.getType().equals("Murderer")){
			coolDownAnimationCheck();
			prohibitButtonsCheck();
		}
		batch.end();

		if (gWorld.getPlayer().getItemChange())
			itemCheck();
		if (gWorld.getPlayer().getWeaponChange())
			weaponCheck();
		if (gWorld.getPlayer().getAbilityChange())
			abilityCheck();

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
	public Actor getWeaponPartsCounter() {

		counter_actor = new Actor();
		counter_actor.draw(batch, 1);
		counter_actor.setName("civ profile actor"); // what to put ah?

		return counter_actor;
	}
	
	/**
	 * Handles the cool down animations of the item slots
	 */
	private void coolDownAnimationCheck(){
		if(player.getType().equals("Murderer")){
			if (((Murderer)player).isDisguised()==true){
				WeaponsCD = false;
			}
		}
		System.out.println("cool down animation check: WeaponsCD == " + WeaponsCD);
		if(WeaponsCD == true){
			if(player.getWeapon()!=null){
				WeaponsAnimationRunTime += Gdx.graphics.getRawDeltaTime();
				if(WeaponsCoolDownAnimation.isAnimationFinished(WeaponsAnimationRunTime)){
					WeaponsAnimationRunTime = 0f;
					WeaponsCD = false;
				}
				else{
					batch.draw(WeaponsCoolDownAnimation.getKeyFrame(WeaponsAnimationRunTime),477,25,72,72);
				}
			}
			else{
				WeaponsCD = false;
			}
		}
		if(PanicCD == true){
			PanicAnimationRunTime += Gdx.graphics.getRawDeltaTime();
			if(PanicCoolDownAnimation.isAnimationFinished(PanicAnimationRunTime)){
				PanicAnimationRunTime = 0f;
				PanicCD = false;
			}
			else{
				batch.draw(PanicCoolDownAnimation.getKeyFrame(PanicAnimationRunTime),503,70,72,72);
			}
		}
		if(DisguiseCD == true){
			DisguiseAnimationRunTime += Gdx.graphics.getRawDeltaTime();
			if(DisguiseCoolDownAnimation.isAnimationFinished(DisguiseAnimationRunTime)){
				DisguiseAnimationRunTime = 0f;
				DisguiseCD = false;
			}
			else{
				batch.draw(DisguiseCoolDownAnimation.getKeyFrame(DisguiseAnimationRunTime),503,70,72,72);
			}
		}
	
	}
	
	private void prohibitButtonsCheck(){
		if(player.getType().equals("Murderer")){
			if (((Murderer)player).isDisguised()==true){
				batch.draw(AssetLoader.prohibitedButton,477,25,72,72);
			}
		}
	}

	/**
	 * When a change in the player's item is detected, itemCheck() will be called, setting itemChange to false
	 * and updating the new item for the player's item slot.
	 */
	private void itemCheck() {
		player = gWorld.getPlayer();
		player.setItemChange(false);
		if (player.getItem() != null) {
			if (player.getType().equals("Murderer"))
				stage.addActor(getTrap());
			else
				stage.addActor(getDisarmTrap());
		} else {
			for (Actor actors : stage.getActors()) {
				if (actors.getName().equals("Item Button"))
					actors.remove();
			}
		}
	}

	/**
	 * When a change in the player's weapon is detected, weaponCheck() will be called, setting weaponChange to
	 * false and updating the new item for the player's weapon slot.
	 */
	private void weaponCheck() {
		player = gWorld.getPlayer();
		player.setWeaponChange(false);
		if (player.getWeapon() != null) {
			for (Actor actors : stage.getActors()) {
				if (actors.getName().equals("Weapon Button"))
					actors.remove();
			}
			if (player.getWeapon().getName().equals("Shotgun")) {
				stage.addActor(getShotgun());
			} else if (player.getWeapon().getName().equals("Bat"))
				stage.addActor(getBat());
			else if (player.getWeapon().getName().equals("Knife"))
				stage.addActor(getKnife());
		} else {
			for (Actor actors : stage.getActors()) {
				if (actors.getName().equals("Weapon Button"))
					actors.remove();
			}
		}
	}

	/**
	 * When a change in the player's ability is detected, abilityCheck() will be called, setting abilityChange
	 * to false and updating the new item for the player's ability slot.
	 */
	private void abilityCheck() {
		player = gWorld.getPlayer();
		System.out.println("CHECK ABILITY");
		player.setAbilityChange(false);
		if (player.getType().equals("Civilian")) {
			System.out.println("CIV");
			stage.addActor(getPanic());
		} else if (player.getType().equals("Murderer")) {
			System.out.println("MUR");
			for (Actor actors : stage.getActors()) {
				if (actors.getName().equals("Disguise to civilian")
						|| actors.getName().equals("Disguise to murderer"))
					actors.remove();
			}
			if (((Murderer) player).isDisguised()) {
				System.out.println("TOMUR");
				stage.addActor(getDisguiseToMur());
			} else {
				System.out.println("TOCIV");
				stage.addActor(getDisguiseToCiv());
			}
		} else {
			System.out.println("EMPTY");
			for (Actor actors : stage.getActors()) {
				if (actors.getName().equals("Disguise to civilian")
						|| actors.getName().equals("Disguise to murderer")
						|| actors.getName().equals("Panic")) {
					actors.remove();
				}
			}
			stage.addActor(getHaunt());
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
	 * Creates the actor for the bat slot at 502,43.
	 * 
	 * @return Actor for Bat slot
	 */
	public ImageButton getBat() {

		x = 502;
		y = 43;

		weaponButton = new ImageButton(civ_bat);
		weaponButton.setX(x);
		weaponButton.setY(y);
		weaponButton.setName("Weapon Button");

		weaponButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

				System.out.println("Bat button touch down, draw hitbox");
				return super.touchDown(event, x, y, pointer, button);
			}

			public void clicked(InputEvent event, float x, float y) {

				System.out.println("Clicked on bat button");
				if(gWorld.getPlayer().useWeapon()){
					// start drawing cool down animation.
					WeaponsCD = true;
				}
			}
		});

		return weaponButton;
	}

	/**
	 * Creates the actor for the shotgun slot at 505,41.
	 * 
	 * @return Actor for Shotgun slot
	 */
	public ImageButton getShotgun() {

		x = 505;
		y = 41;

		weaponButton = new ImageButton(AssetLoader.civ_weapon_gun_draw);
		weaponButton.setX(x);
		weaponButton.setY(y);
		weaponButton.setName("Weapon Button");

		weaponButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("Shotgun button touch down, draw hitbox");
				return super.touchDown(event, x, y, pointer, button);
			}

			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked on shotgun button");
				gWorld.getPlayer().useWeapon();
				// start drawing cool down animation
				WeaponsCD = true;
			}
		});

		return weaponButton;
	}

	/**
	 * Creates the actor for the disarm trap slot at 557,45.
	 * 
	 * @return Actor for Disarm Trap slot
	 */
	public ImageButton getDisarmTrap() {

		x = 557;
		y = 45;

		itemButton = new ImageButton(civ_item);
		itemButton.setX(x);
		itemButton.setY(y);
		itemButton.setName("Item Button");

		itemButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("Disarm trap button touch down, draw hitbox");
				return super.touchDown(event, x, y, pointer, button);
			}

			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked on disarm trap button");
				gWorld.getPlayer().useItem();
				// start drawing cool down animation
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

		x = 520;
		y = 94;

		dashButton = new ImageButton(civ_dash);
		dashButton.setX(x);
		dashButton.setY(y);
		dashButton.setName("Panic");

		dashButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked on panic button");
				// Used to check character position FOR TESTING
				System.out.println(gWorld.getPlayer().getBody().getPosition());
				gWorld.getPlayer().useAbility();
				// start drawing cool down animation with ability frame time.
				PanicCD = true;
			}
		});

		return dashButton;
	}

	/**
	 * Creates the actor for the knife slot at 505,40.
	 * 
	 * @return Actor for Knife slot
	 */
	public ImageButton getKnife() {
		x = 505;
		y = 40;

		weaponButton = new ImageButton(mur_knife);
		weaponButton.setX(x);
		weaponButton.setY(y);
		weaponButton.setName("Weapon Button");

		weaponButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("Knife button touch down, draw hitbox");
				return super.touchDown(event, x, y, pointer, button);
			}

			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked on knife button");
				gWorld.getPlayer().useWeapon();
				// start to draw cool down animation
				WeaponsCD = true;
			}
		});

		return weaponButton;
	}

	/**
	 * Creates the actor for the trap slot at 546,43.
	 * 
	 * @return Actor for Trap slot
	 */
	public ImageButton getTrap() {

		x = 546;
		y = 43;

		itemButton = new ImageButton(mur_item);
		itemButton.setX(x);
		itemButton.setY(y);
		itemButton.setName("Item Button");
		itemButton.setSize(40, 40);

		itemButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("Trap button touch down, draw hitbox");
				return super.touchDown(event, x, y, pointer, button);
			}

			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked on trap button");
				gWorld.getPlayer().useItem();
				//start to draw cool down animation
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
		x = 522;
		y = 92;

		disguiseToCiv = new ImageButton(mur_MtC);
		disguiseToCiv.setX(x);
		disguiseToCiv.setY(y);
		disguiseToCiv.setWidth(33);
		disguiseToCiv.setHeight(33);
		disguiseToCiv.setName("Disguise to civilian");

		disguiseToCiv.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked on disguise to civilian button");
				// Used to check character position FOR TESTING
				System.out.println(gWorld.getPlayer().getBody().getPosition());
				gWorld.getPlayer().useAbility();
				// start to draw cool down animation with ability frame time
				DisguiseCD = true;
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
		x = 522;
		y = 92;

		// disguiseToMur = new ImageButton(mur_CtM);
		// disguiseToMur.setX(x);
		// disguiseToMur.setY(y);
		// disguiseToMur.setWidth(33);
		// disguiseToMur.setHeight(33);
		// disguiseToMur.setName("Disguise to murderer");

		disguiseToMur = new ImageButton(mur_MtC);
		disguiseToMur.setX(x);
		disguiseToMur.setY(y);
		disguiseToMur.setWidth(33);
		disguiseToMur.setHeight(33);
		disguiseToMur.setName("Disguise to murderer");

		disguiseToMur.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked on disguise to murderer button");
				// Used to check character position FOR TESTING
				System.out.println(gWorld.getPlayer().getBody().getPosition());
				gWorld.getPlayer().useAbility();
				// start to draw cool down animation with ability frame time
				DisguiseCD = true;
			}
		});

		return disguiseToMur;
	}

	/**
	 * Creates the actor for the haunt slot at 528,100.
	 * 
	 * @return Actor for Haunt slot.
	 */
	public ImageButton getHaunt() {
		x = 528;
		y = 100;

		hauntButton = new ImageButton(civ_item);
		hauntButton.setX(x);
		hauntButton.setY(y);
		hauntButton.setName("Haunt");

		hauntButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked haunt button");
				// Used to check character position FOR TESTING
				System.out.println(gWorld.getPlayer().getBody().getPosition());
				gWorld.getPlayer().useAbility();
				// start to draw cool down animation with ability frame time
				DisguiseCD = true;
			}
		});

		return hauntButton;
	}

	/**
	 * Releases the resources held by objects or images loaded.
	 */
	public void hudDispose() {
		stage.dispose();
	}

}
