package com.jkjk.GameWorld;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.GameObjects.Characters.Murderer;
import com.jkjk.Input.DesktopPlayerInputController;
import com.jkjk.Input.PlayerInputController;
import com.jkjk.Input.TouchPlayerInputController;
import com.jkjk.MMHelpers.AssetLoader;
import com.jkjk.MMHelpers.PresentationFrame;
import com.jkjk.MMHelpers.ToastMessage;
import com.jkjk.MurderMansion.MurderMansion;
import com.jkjk.Screens.MenuScreen;

/**
 * HudRenderer contains the rendering of all HUD icons, such as touchpad, item slots and timers.
 * 
 * @author LeeJunXiang
 */
public class HudRenderer {
	private MurderMansion game;
	private GameSession session;
	private float gameWidth;
	private float gameHeight;
	private float BUTTON_WIDTH;
	private float BUTTON_HEIGHT;

	private GameWorld gWorld;
	private GameCharacter player;

	private TextureRegionDrawable civ_bat, civ_item, civ_dash, mur_knife, mur_item, mur_CtM, mur_MtC, haunt;
	private Texture emptySlot;
	private Actor emptySlot_actor;
	private Texture timebox;
	private Actor timebox_actor;
	private Texture weapon_parts_counter;
	private Actor counter_actor;
	private BitmapFont font;
	private String time;
	private Float playTime;

	// PAUSE SCREEN
	private TextureRegionDrawable settingsButtonDraw;
	private TextureRegionDrawable settingsCloseDraw;
	private ImageButton settingsButton;
	private boolean inSettings = false;
	private Texture pause_main;

	private float x, y;
	private ImageButton weaponButton, itemButton, dashButton, disguiseToCiv, disguiseToMur, hauntButton;

	private SpriteBatch batch;
	private Stage stage;
	private Stage settingsStage;

	private Touchpad touchpad;
	private Drawable touchKnob;

	private boolean PanicCD;
	private boolean DisguiseCD;
	private boolean HauntCD;
	private boolean WeaponsCD;
	private Animation PanicCoolDownAnimation;
	private Animation DisguiseCoolDownAnimation;
	private Animation HauntCoolDownAnimation;
	private Animation WeaponsCoolDownAnimation;
	private float PanicAnimationRunTime;
	private float DisguiseAnimationRunTime;
	private float WeaponsAnimationRunTime;
	private float HauntAnimationRunTime;

	private TextButtonStyle normalSettings;
	private TextButton buttonMainMenu;
	private ImageButton settingsCloseButton;
	private TextButton unmuteButton;
	private TextButton muteButton;

	private int minutes;
	private int seconds;

	private boolean mute;

	private ToastMessage TM;
	private ToastMessage GWTM;
	private boolean welcomeMsg;
	private float scale;
	private BitmapFont syncFont;
	private boolean prevState;

	private final boolean tutorial;
	private Image nextButton;
	private Image backButton;
	private Image hudOverlay;
	private Image civCharTut;
	private Image murCharTut;
	private Image itemTutBegin;
	private Image abilityTut;
	private Image weaponTut;
	private Image itemTut;
	private Image shotgunTut;
	private Image shotgunTutMur;
	private Image mapTut;
	private Image nextButtonToMenu;
	private Image ghostCharTut;
	private final boolean desktopControls;
	private final PlayerInputController playerInputController;

	// private boolean
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
	public HudRenderer(final GameWorld gWorld, GameSession session, final float gameWidth,
			final float gameHeight, final MurderMansion game, final boolean tutorial) {
		initAssets(gameWidth, gameHeight);

		this.gWorld = gWorld;
		this.session = session;
		this.game = game;
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		this.tutorial = tutorial;
		this.desktopControls = Gdx.app.getType() == ApplicationType.Desktop;
		this.playerInputController = desktopControls ? new DesktopPlayerInputController()
				: new TouchPlayerInputController(touchpad);

		BUTTON_WIDTH = 70;
		BUTTON_HEIGHT = 30;

		// countdown
		playTime = 240.0f;

		batch = new SpriteBatch();

		civCharTut = new Image(AssetLoader.civCharTut);
		civCharTut.setName("civ char tut");
		murCharTut = new Image(AssetLoader.murCharTut);
		murCharTut.setName("mur char tut");
		ghostCharTut = new Image(AssetLoader.ghostCharTut);
		ghostCharTut.setName("ghost char tut");
		itemTutBegin = new Image(AssetLoader.itemTutBegin);
		itemTutBegin.setName("item tut begin");
		hudOverlay = new Image(AssetLoader.hudOverlay);
		hudOverlay.setName("hud overlay");
		if ("Civilian".equals(gWorld.getPlayer().getType())) {
			abilityTut = new Image(AssetLoader.abilityTutCiv);
			weaponTut = new Image(AssetLoader.weaponTutCiv);
			itemTut = new Image(AssetLoader.itemTutCiv);
			shotgunTut = new Image(AssetLoader.shotgunTut);
			shotgunTut.setName("shotgun tut");
		} else {
			abilityTut = new Image(AssetLoader.abilityTutMur);
			weaponTut = new Image(AssetLoader.weaponTutMur);
			itemTut = new Image(AssetLoader.itemTutMur);
			shotgunTutMur = new Image(AssetLoader.shotgunTutMur);
			shotgunTutMur.setName("shotgun tut mur");
		}
		abilityTut.setName("ability tut");
		weaponTut.setName("weapon tut");
		itemTut.setName("item tut");
		mapTut = new Image(AssetLoader.mapTutorial);
		mapTut.setName("map tut");
		backButton = new Image(AssetLoader.backButton);
		backButton.setPosition(20, 150);
		backButton.setName("back button");
		nextButton = new Image(AssetLoader.nextButton);
		nextButton.setPosition(550, 150);
		nextButton.setName("next button");
		nextButtonToMenu = new Image(AssetLoader.nextButtonToMenu);
		nextButtonToMenu.setPosition(550, 150);
		nextButtonToMenu.setName("next button to menu");
		if (tutorial)
			TM = new ToastMessage(PresentationFrame.WIDTH, 330, 15000);
		else
			TM = new ToastMessage(PresentationFrame.WIDTH, 330, 5000);
		GWTM = gWorld.getTM();
		GWTM.setFrameWidth(PresentationFrame.WIDTH);

		nextButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
				boolean cont = false;
				if (stage.getActors().first().equals(touchpad)) {
					stage.clear();
					stage.addActor(hudOverlay);
					stage.addActor(nextButton);
					TM.setDisplayMessage("Here are your controls... learn them well!");
					GWTM.setDisplayMessage("");
				} else if (stage.getActors().first().equals(hudOverlay)) {
					stage.clear();
					stage.addActor(abilityTut);
					stage.addActor(nextButton);
					TM.setDisplayMessage("Each class has unique ability, item and weapon");
					GWTM.setDisplayMessage("Try using your ability!");
				} else if (stage.getActors().first().equals(abilityTut)) {
					stage.clear();
					stage.addActor(itemTutBegin);
					TM.setDisplayMessage("A weapon was spawned for you to your left");
					GWTM.setDisplayMessage("Try picking it up!");
					gWorld.createTutorialWeapon();
				} else if (stage.getActors().first().equals(weaponTut)) {
					stage.clear();
					TM.setDisplayMessage("An item was spawned for you to your left");
					GWTM.setDisplayMessage("Try picking it up!");
					gWorld.createTutorialItem();
				} else if (stage.getActors().first().equals(itemTut)) {
					stage.clear();
					if ("Murderer".equals(gWorld.getPlayer().getType())) {
						stage.addActor(shotgunTutMur);
						stage.addActor(nextButton);
					}
					TM.setDisplayMessage("2 weapon parts were spawned for you to your left");
					GWTM.setDisplayMessage("Try picking them up!");
					gWorld.createTutorialWP();
				} else if (stage.getActors().first().equals(shotgunTut)
						|| stage.getActors().first().equals(shotgunTutMur)
						|| stage.getActors().first().equals(ghostCharTut)) {
					stage.clear();
					stage.addActor(nextButton);
					TM.setDisplayMessage("Congrats! You completed the gameplay tutorial!");
					GWTM.setDisplayMessage("Click next to learn more about the map");
				} else if (stage.getActors().first().equals(nextButton)) {
					stage.clear();
					stage.addActor(mapTut);
					stage.addActor(nextButtonToMenu);
					cont = true;
				}
				if (!cont) {
					stage.addActor(touchpad);
					stage.addActor(getTimebox());
					stage.addActor(getWeaponPartsCounter());
					stage.addActor(getEmptySlot());
					stage.addActor(getSettingsButton());
					if (!"Ghost".equals(gWorld.getPlayer().getType()))
						abilityCheck();
				}

			}
		});

		nextButtonToMenu.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
				stage.clear();

				if ((game.actionResolver.getSignedInGPGS()) && (tutorial)) {
					if ("Civilian".equals(gWorld.getPlayer().getType())) {
						game.actionResolver.unlockAchievementGPGS(game.actionResolver.ACHEIVEMENT_8);
					} else if ("Murderer".equals(gWorld.getPlayer().getType())) {
						game.actionResolver.unlockAchievementGPGS(game.actionResolver.ACHEIVEMENT_7);
					}
				}

				((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen(game, gameWidth,
						gameHeight));
			}
		});

		// Create a Stage and add TouchPad
		stage = new Stage(PresentationFrame.createViewport(), batch);
		stage.addActor(touchpad);
		stage.addActor(getTimebox());
		stage.addActor(getWeaponPartsCounter());
		stage.addActor(getEmptySlot());
		stage.addActor(getSettingsButton());
		abilityCheck();
		if (tutorial) {
			if ("Civilian".equals(gWorld.getPlayer().getType())) {
				stage.addActor(civCharTut);
			} else {
				stage.addActor(murCharTut);
			}
			stage.addActor(nextButton);
		}

		settingsStage = new Stage(PresentationFrame.createViewport(), batch);
		settingsStage.addActor(getMainMenuButton());
		settingsStage.addActor(getMuteButton());
		settingsStage.addActor(getSettingsCloseButton());
		welcomeMsg = true;
		scale = 1f;
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
		HauntCoolDownAnimation = AssetLoader.HauntCoolDownAnimation;
		WeaponsCoolDownAnimation = AssetLoader.WeaponsCoolDownAnimation;
		PanicCD = DisguiseCD = WeaponsCD = HauntCD = false;
		WeaponsAnimationRunTime = DisguiseAnimationRunTime = PanicAnimationRunTime = HauntAnimationRunTime = 0f;
		civ_bat = AssetLoader.civ_weapon_bat_draw;
		civ_item = AssetLoader.civ_item_draw;
		civ_dash = AssetLoader.civ_dash_draw;
		mur_knife = AssetLoader.mur_weapon_draw;
		mur_item = AssetLoader.mur_item_draw;
		mur_CtM = AssetLoader.mur_swap_M_draw;
		mur_MtC = AssetLoader.mur_swap_C_draw;
		haunt = AssetLoader.haunt_draw;

		// Touchpad stuff
		touchpad = AssetLoader.touchpad;
		touchpad.setName("touchpad");
		touchpad.setBounds(w / 14, h / 14, w / 5, w / 5);
		touchKnob = AssetLoader.touchKnob;
		touchKnob.setMinHeight(touchpad.getHeight() / 4);
		touchKnob.setMinWidth(touchpad.getWidth() / 4);

		// Top part of the screen
		timebox = AssetLoader.time;
		weapon_parts_counter = AssetLoader.weapon_parts_counter;
		font = AssetLoader.crimesFont36Time;
		syncFont = AssetLoader.crimesFont36Sync;
		settingsButtonDraw = AssetLoader.settings_button_draw;
		pause_main = AssetLoader.pause_main;
		normalSettings = AssetLoader.normalSettings;
		settingsCloseDraw = AssetLoader.settings_cancel_draw;

	}

	/**
	 * Renders all HUD images on the player's screen
	 * 
	 * @param delta
	 *            The time between each render.
	 */
	public void render(float delta, boolean gameStarted) {
		stage.getViewport().apply();
		batch.setProjectionMatrix(stage.getCamera().combined);
		batch.begin();

		if (!gameStarted) {
			String s = "Synchronizing...";
			GlyphLayout layout = new GlyphLayout(syncFont, s);
			syncFont.draw(batch, s, (PresentationFrame.WIDTH - layout.width) / 2f, 330f);
			GWTM.render(batch);
			batch.end();
		} else {
			if (gameStarted != prevState) {
				Gdx.input.setInputProcessor(stage);
				prevState = gameStarted;
			}
			handleDesktopActions();
			if (welcomeMsg) {
				welcomeMsg = false;
				if (tutorial) {
					TM.setDisplayMessage("Welcome to the game tutorial");
					GWTM.setDisplayMessage("The following is your character's objectives");
				} else {
					if (gWorld.getPlayer().getType().equals("Murderer")) {
						GWTM.setDisplayMessage("Welcome... Murderer...");
					} else {
						GWTM.setDisplayMessage("Welcome... Civilian...");
					}
				}
			}
			batch.draw(timebox, 40, 280);
			batch.draw(weapon_parts_counter, 480, 235);
			batch.draw(emptySlot, 480, 22, 120, 120);
			font.draw(batch, getTime(delta), 70, 328);
			WeaponPartsDisplay();
			drawDesktopHotkeys();
			coolDownAnimationCheck(delta);
			prohibitButtonsCheck();
			TM.render(batch);
			GWTM.render(batch);

			batch.end();

			if (gWorld.getPlayer().getItemChange())
				itemCheck();
			if (gWorld.getPlayer().getWeaponChange())
				weaponCheck();
			if (gWorld.getPlayer().getAbilityChange())
				abilityCheck();

			stage.draw(); // Draw touchpad
			stage.act(Gdx.graphics.getDeltaTime()); // Acts stage at deltatime

			if (inSettings) {
				settingsStage.getViewport().apply();
				batch.setProjectionMatrix(settingsStage.getCamera().combined);
				batch.begin();
				batch.draw(pause_main, 0, 0);
				if (mute)
					batch.draw(AssetLoader.soundoff_tex, 390, 180);
				else
					batch.draw(AssetLoader.soundon_tex, 390, 180);
				batch.end();
				settingsStage.draw();
				settingsStage.act(Gdx.graphics.getDeltaTime());
			}
		}
	}

	/**
	 * @return Time left in the game
	 */
	public String getTime(float delta) {

		playTime -= delta; //
		minutes = (int) Math.floor(playTime / 60.0f);
		seconds = (int) Math.floor(playTime - minutes * 60);
		time = String.format("%d:%02d", minutes, seconds);
		if (minutes < 0) {
			return String.format("%d:%02d", 0, 0);
		}

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

	public Actor getMainMenuButton() {
		buttonMainMenu = new TextButton("Main Menu", normalSettings);
		buttonMainMenu.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
		buttonMainMenu.setPosition(225, 130);

		buttonMainMenu.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
				try {
					if (game.mMultiplayerSession.isServer) {
						game.mMultiplayerSession.getServer().endSession();
						// System.out.println("Ended server session.");
					}
					game.mMultiplayerSession.getClient().endSession();
					// System.out.println("Leave room");
					game.actionResolver.leaveRoom();

					// System.out.println("End mMultiplayer session");
					game.mMultiplayerSession.endSession();
				} catch (Exception e) {
					System.out.println("Error on button press: " + e.getMessage());
				}
				((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen(game, gameWidth,
						gameHeight));
			}
		});

		return buttonMainMenu;
	}

	public Actor getSettingsCloseButton() {
		settingsCloseButton = new ImageButton(settingsCloseDraw);
		settingsCloseButton.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
		settingsCloseButton.setPosition(485, 260);

		settingsCloseButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				inSettings = false;
				Gdx.input.setInputProcessor(stage);
			}
		});
		return settingsCloseButton;
	}

	public Actor getUnmuteButton() {
		unmuteButton = new TextButton("Sound off", normalSettings);
		unmuteButton.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
		unmuteButton.setPosition(348, 130);

		unmuteButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Unmute is pressed");
				mute = false;
				unmuteButton.remove();
				settingsStage.addActor(getMuteButton());
				AssetLoader.unmuteSFX();
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
			}
		});

		return unmuteButton;
	}

	public Actor getMuteButton() {
		muteButton = new TextButton("Sound on", normalSettings);
		muteButton.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
		muteButton.setPosition(348, 130);

		muteButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Mute is pressed");
				mute = true;
				muteButton.remove();
				settingsStage.addActor(getUnmuteButton());
				AssetLoader.muteSFX();
			}
		});

		return muteButton;
	}

	/**
	 * @return Actor for displaying the profile of the player.
	 */
	public Actor getWeaponPartsCounter() {

		counter_actor = new Actor();
		counter_actor.draw(batch, 1);
		counter_actor.setName("weapon part counter"); // what to put ah?

		return counter_actor;
	}

	private void WeaponPartsDisplay() {
		int numParts = gWorld.getNumOfWeaponPartsCollected();
		font.draw(batch, Integer.toString(numParts), 506, 328);
		font.draw(batch, Integer.toString(session.getNumOfPlayers() * 2), 550, 313);
	}

	private void drawDesktopHotkeys() {
		if (!desktopControls) {
			return;
		}

		font.getData().setScale(0.35f, 0.35f);
		drawHotkeyLabel("LMB", 498f, 30f);
		drawHotkeyLabel("E", 559f, 30f);
		drawHotkeyLabel("RMB", 520f, 84f);
		font.getData().setScale(1f, 1f);
	}

	private void drawHotkeyLabel(String label, float centerX, float baselineY) {
		GlyphLayout layout = new GlyphLayout(font, label);
		font.draw(batch, label, centerX - (layout.width / 2f), baselineY);
	}

	/**
	 * Handles the cool down animations of the item slots
	 */
	private void coolDownAnimationCheck(float delta) {
		if (player.getType().equals("Murderer")) {
			if (((Murderer) player).isDisguised()) {
				WeaponsCD = false;
			}
		}
		if (WeaponsCD) {
			if (player.getType().equals("Civilian") || player.getType().equals("Murderer")) {
				if (player.getWeapon() != null) {
					WeaponsAnimationRunTime += delta;
					if (WeaponsCoolDownAnimation.isAnimationFinished(WeaponsAnimationRunTime)) {
						WeaponsAnimationRunTime = 0f;
						WeaponsCD = false;
					} else {
						batch.draw((TextureRegion) WeaponsCoolDownAnimation.getKeyFrame(
								WeaponsAnimationRunTime), 477, 25,
								72, 72);
					}
				} else {
					WeaponsCD = false;
				}
			}
		}
		if (PanicCD) {
			PanicAnimationRunTime += delta;
			if (PanicCoolDownAnimation.isAnimationFinished(PanicAnimationRunTime)) {
				PanicAnimationRunTime = 0f;
				PanicCD = false;
			} else {
				batch.draw((TextureRegion) PanicCoolDownAnimation.getKeyFrame(PanicAnimationRunTime),
						503, 70, 72, 72);
			}
		}
		if (DisguiseCD) {
			DisguiseAnimationRunTime += delta;
			if (DisguiseCoolDownAnimation.isAnimationFinished(DisguiseAnimationRunTime)) {
				DisguiseAnimationRunTime = 0f;
				DisguiseCD = false;
			} else {
				batch.draw((TextureRegion) DisguiseCoolDownAnimation.getKeyFrame(
						DisguiseAnimationRunTime), 503, 70, 72, 72);
			}
		}
		if (HauntCD) {
			HauntAnimationRunTime += delta;
			if (HauntCoolDownAnimation.isAnimationFinished(HauntAnimationRunTime)) {
				HauntAnimationRunTime = 0f;
				HauntCD = false;
			} else {
				batch.draw((TextureRegion) HauntCoolDownAnimation.getKeyFrame(HauntAnimationRunTime),
						503, 70, 72, 72);
			}
		}

	}

	private void prohibitButtonsCheck() {
		if (player.getType().equals("Murderer")) {
			if (((Murderer) player).isDisguised()) {
				batch.draw(AssetLoader.prohibitedButton, 477, 25, 72, 72);
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
			if (player.getType().equals("Murderer")) {
				if (tutorial) {
					stage.clear();
					stage.addActor(itemTut);
					stage.addActor(nextButton);
					GWTM.setDisplayMessage("Excellent! Try using your item");
					stage.addActor(touchpad);
					stage.addActor(getTimebox());
					stage.addActor(getWeaponPartsCounter());
					stage.addActor(getEmptySlot());
					stage.addActor(getSettingsButton());
					abilityCheck();
				}
				stage.addActor(getTrap());
			} else {
				if (tutorial) {
					if (!"Ghost".equals(gWorld.getPlayer().getType())) {
						stage.clear();
						stage.addActor(itemTut);
						stage.addActor(nextButton);
						GWTM.setDisplayMessage("Excellent! Try using your item on the trap");
						stage.addActor(touchpad);
						stage.addActor(getTimebox());
						stage.addActor(getWeaponPartsCounter());
						stage.addActor(getEmptySlot());
						stage.addActor(getSettingsButton());
						abilityCheck();
						gWorld.createTutorialTrap();
					}
				}
				stage.addActor(getDisarmTrap());
			}
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
				if (tutorial) {
					stage.clear();
					stage.addActor(shotgunTut);
					stage.addActor(nextButton);
					GWTM.setDisplayMessage("Excellent! Try using your weapon");
					stage.addActor(touchpad);
					stage.addActor(getTimebox());
					stage.addActor(getWeaponPartsCounter());
					stage.addActor(getEmptySlot());
					stage.addActor(getSettingsButton());
					abilityCheck();
				}
				stage.addActor(getShotgun());
			} else if (player.getWeapon().getName().equals("Bat")) {
				if (tutorial) {
					if (!"Ghost".equals(gWorld.getPlayer().getType())) {
						stage.clear();
						stage.addActor(weaponTut);
						stage.addActor(nextButton);
						GWTM.setDisplayMessage("Excellent! Try using your weapon");
						stage.addActor(touchpad);
						stage.addActor(getTimebox());
						stage.addActor(getWeaponPartsCounter());
						stage.addActor(getEmptySlot());
						stage.addActor(getSettingsButton());
						abilityCheck();
						session.createTutorialDummy();
					}
				}
				stage.addActor(getBat());
			} else if (player.getWeapon().getName().equals("Knife")) {
				if (tutorial) {
					stage.clear();
					stage.addActor(weaponTut);
					stage.addActor(nextButton);
					GWTM.setDisplayMessage("Excellent! Try using your weapon");
					stage.addActor(touchpad);
					stage.addActor(getTimebox());
					stage.addActor(getWeaponPartsCounter());
					stage.addActor(getEmptySlot());
					stage.addActor(getSettingsButton());
					abilityCheck();
					session.createTutorialDummy();
				}
				stage.addActor(getKnife());
			}
		} else {
			for (Actor actors : stage.getActors()) {
				if ("Weapon Button".equals(actors.getName()))
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
				if ("Disguise to civilian".equals(actors.getName())
						|| "Disguise to murderer".equals(actors.getName()))
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
				if ("Disguise to civilian".equals(actors.getName())
						|| "Disguise to murderer".equals(actors.getName())
						|| "Panic".equals(actors.getName())) {
					actors.remove();
				}
			}
			if (tutorial) {
				stage.clear();
				stage.addActor(ghostCharTut);
				stage.addActor(nextButton);
				TM.setDisplayMessage("Oops! Looks like you died. You're now a ghost!");
				stage.addActor(touchpad);
				stage.addActor(getTimebox());
				stage.addActor(getWeaponPartsCounter());
				stage.addActor(getEmptySlot());
				stage.addActor(getSettingsButton());
				gWorld.createTutorialItem();
				gWorld.createTutorialWeapon();
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
	 * Creates the actor for the PAUSE BUTTON at 502,253.
	 * 
	 * @return Actor for Bat slot
	 */
	public ImageButton getSettingsButton() {

		x = 595;
		y = 294;

		settingsButton = new ImageButton(settingsButtonDraw);
		settingsButton.setX(x);
		settingsButton.setY(y);
		settingsButton.setWidth(30);
		settingsButton.setHeight(30);
		settingsButton.setName("Pause Button");

		settingsButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked on settings button");
				inSettings = true;
				Gdx.input.setInputProcessor(settingsStage);
			}
		});

		return settingsButton;
	}

	/**
	 * Creates the actor for the bat slot at 502,43.
	 * 
	 * @return Actor for Bat slot
	 */
	public ImageButton getBat() {

		x = 493;
		y = 40;

		weaponButton = new ImageButton(civ_bat);
		weaponButton.setX(x);
		weaponButton.setY(y);
		weaponButton.setName("Weapon Button");
		AssetLoader.pickUpItemSound.play(AssetLoader.VOLUME);
		if ("Ghost".equals(gWorld.getPlayer().getType())) {
			TM.setDisplayMessage("Picked Bat Up");
		} else
			TM.setDisplayMessage("Obtained Bat");

		weaponButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked on bat button");
				if (gWorld.getPlayer().useWeapon()) {
					// start drawing cool down animation.
					WeaponsCD = true;
					if ("Ghost".equals(gWorld.getPlayer().getType())) {
						AssetLoader.pickUpItemSound.play(AssetLoader.VOLUME);
						TM.setDisplayMessage("Placed Bat Down");
					} else {
						TM.setDisplayMessage("Swung Bat");
						AssetLoader.batSwingSound.play(AssetLoader.VOLUME);
					}
					session.updatePlayerUseWeapon();
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

		x = 493;
		y = 40;

		weaponButton = new ImageButton(AssetLoader.civ_weapon_gun_draw);
		weaponButton.setX(x);
		weaponButton.setY(y);
		weaponButton.setName("Weapon Button");
		AssetLoader.pickUpItemSound.play(AssetLoader.VOLUME);
		TM.setDisplayMessage("Obtained Shotgun");
		weaponButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("Shotgun button touch down, draw hitbox");
				return super.touchDown(event, x, y, pointer, button);
			}

			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked on shotgun button");
				if (gWorld.getPlayer().useWeapon()) {
					// start drawing cool down animation
					TM.setDisplayMessage("Shotgun Fired!");
					WeaponsCD = true;
					session.updatePlayerUseWeapon();
					AssetLoader.shotgunBlastSound.play(AssetLoader.VOLUME);
				}
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

		x = 547;
		y = 40;

		itemButton = new ImageButton(civ_item);
		itemButton.setX(x);
		itemButton.setY(y);
		itemButton.setName("Item Button");
		AssetLoader.pickUpItemSound.play(AssetLoader.VOLUME);
		if ("Ghost".equals(gWorld.getPlayer().getType()))
			TM.setDisplayMessage("Picked Up Item");
		else
			TM.setDisplayMessage("Obtained Disarm Trap");

		itemButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("Disarm trap button touch down, draw hitbox");
				return super.touchDown(event, x, y, pointer, button);
			}

			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked on disarm trap button");
				gWorld.getPlayer().useItem();
				session.updatePlayerUseItem();
				if ("Ghost".equals(gWorld.getPlayer().getType())) {
					TM.setDisplayMessage("Placing Item Down");
					AssetLoader.pickUpItemSound.play(AssetLoader.VOLUME);
				}
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

				if (gWorld.getPlayer().useAbility()) {
					// start drawing cool down animation with ability frame time.
					TM.setDisplayMessage("Quick! Run!");
					PanicCD = true;
					session.updatePlayerUseAbility();
				}
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
		x = 493;
		y = 40;

		weaponButton = new ImageButton(mur_knife);
		weaponButton.setX(x);
		weaponButton.setY(y);
		weaponButton.setName("Weapon Button");
		AssetLoader.pickUpItemSound.play(AssetLoader.VOLUME);
		TM.setDisplayMessage("Obtained Knife");
		weaponButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked on knife button");
				if (gWorld.getPlayer().useWeapon()) {
					// start to draw cool down animation
					TM.setDisplayMessage("Knife Thrust!");
					WeaponsCD = true;
					session.updatePlayerUseWeapon();
					AssetLoader.knifeThrustSound.play(AssetLoader.VOLUME);
				}
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
		AssetLoader.pickUpItemSound.play(AssetLoader.VOLUME);
		TM.setDisplayMessage("Obtained Trap");

		itemButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked on trap button");
				gWorld.getPlayer().useItem();
				session.updatePlayerUseItem();
				TM.setDisplayMessage("Planting Trap...");
				AssetLoader.disarmTrapSound.play(AssetLoader.VOLUME);
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
				if (gWorld.getPlayer().useAbility()) {
					// start drawing cool down animation with ability frame time.
					TM.setDisplayMessage("Disguising...");
					DisguiseCD = true;
					session.updatePlayerUseAbility();
				}
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
		disguiseToMur = new ImageButton(mur_CtM);
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
				if (gWorld.getPlayer().useAbility()) {
					// start drawing cool down animation with ability frame time.
					TM.setDisplayMessage("Revealing true identity");
					DisguiseCD = true;
					session.updatePlayerUseAbility();
				}
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
		x = 518;
		y = 85;

		hauntButton = new ImageButton(haunt);
		hauntButton.setX(x);
		hauntButton.setY(y);
		hauntButton.setName("Haunt");

		hauntButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Clicked haunt button");
				// Used to check character position FOR TESTING
				System.out.println(gWorld.getPlayer().getBody().getPosition());
				if (gWorld.getPlayer().useAbility()) {
					// start drawing cool down animation with ability frame time.
					HauntCD = true;
					session.updatePlayerUseAbility();
				}
			}
		});

		return hauntButton;
	}

	public ToastMessage getTM() {
		return TM;
	}

	public void updateInput(Viewport worldViewport) {
		playerInputController.update(worldViewport);
	}

	public PlayerInputController getPlayerInputController() {
		return playerInputController;
	}

	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		settingsStage.getViewport().update(width, height, true);
		TM.setFrameWidth(PresentationFrame.WIDTH);
		GWTM.setFrameWidth(PresentationFrame.WIDTH);
	}

	private void handleDesktopActions() {
		if (!desktopControls) {
			return;
		}
		if (playerInputController.consumePauseToggle()) {
			inSettings = !inSettings;
			Gdx.input.setInputProcessor(inSettings ? settingsStage : stage);
			playerInputController.clearPendingActions();
			return;
		}
		if (inSettings) {
			playerInputController.clearPendingActions();
			return;
		}
		if (playerInputController.consumeUseWeapon()) {
			for (Actor actor : stage.getActors()) {
				if ("Weapon Button".equals(actor.getName())) {
					((ImageButton) actor).toggle();
					break;
				}
			}
			if (player.getWeapon() != null && player.getWeapon().getName().equals("Shotgun")) {
				if (gWorld.getPlayer().useWeapon()) {
					TM.setDisplayMessage("Shotgun Fired!");
					WeaponsCD = true;
					session.updatePlayerUseWeapon();
					AssetLoader.shotgunBlastSound.play(AssetLoader.VOLUME);
				}
			} else if (player.getWeapon() != null && player.getWeapon().getName().equals("Knife")) {
				if (gWorld.getPlayer().useWeapon()) {
					TM.setDisplayMessage("Knife Thrust!");
					WeaponsCD = true;
					session.updatePlayerUseWeapon();
					AssetLoader.knifeThrustSound.play(AssetLoader.VOLUME);
				}
			} else if (player.getWeapon() != null && gWorld.getPlayer().useWeapon()) {
				WeaponsCD = true;
				if ("Ghost".equals(gWorld.getPlayer().getType())) {
					AssetLoader.pickUpItemSound.play(AssetLoader.VOLUME);
					TM.setDisplayMessage("Placed Bat Down");
				} else {
					TM.setDisplayMessage("Swung Bat");
					AssetLoader.batSwingSound.play(AssetLoader.VOLUME);
				}
				session.updatePlayerUseWeapon();
			}
		}
		if (playerInputController.consumeUseItem() && gWorld.getPlayer().getItem() != null) {
			gWorld.getPlayer().useItem();
			session.updatePlayerUseItem();
			if ("Ghost".equals(gWorld.getPlayer().getType())) {
				TM.setDisplayMessage("Placing Item Down");
				AssetLoader.pickUpItemSound.play(AssetLoader.VOLUME);
			} else if ("Murderer".equals(gWorld.getPlayer().getType())) {
				TM.setDisplayMessage("Planting Trap...");
				AssetLoader.disarmTrapSound.play(AssetLoader.VOLUME);
			}
		}
		if (playerInputController.consumeUseAbility() && gWorld.getPlayer().useAbility()) {
			if ("Civilian".equals(gWorld.getPlayer().getType())) {
				TM.setDisplayMessage("Quick! Run!");
				PanicCD = true;
			} else if ("Murderer".equals(gWorld.getPlayer().getType())) {
				if (((Murderer) gWorld.getPlayer()).isDisguised()) {
					TM.setDisplayMessage("Revealing true identity");
				} else {
					TM.setDisplayMessage("Disguising...");
				}
				DisguiseCD = true;
			} else {
				HauntCD = true;
			}
			session.updatePlayerUseAbility();
		}
	}

	/**
	 * Releases the resources held by objects or images loaded.
	 */
	public void hudDispose() {
		stage.dispose();
		settingsStage.dispose();
		batch.dispose();
	}

}
