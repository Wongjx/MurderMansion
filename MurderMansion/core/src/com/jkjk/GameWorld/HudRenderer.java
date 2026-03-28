package com.jkjk.GameWorld;

import java.io.IOException;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
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
import com.jkjk.MMHelpers.MMLog;
import com.jkjk.MMHelpers.PresentationFrame;
import com.jkjk.MMHelpers.ToastMessage;
import com.jkjk.Multiplayer.DiscoveryApiClient;
import com.jkjk.Multiplayer.MultiplayerPreferences;
import com.jkjk.MurderMansion.MurderMansion;
import com.jkjk.Screens.MenuScreen;

/**
 * HudRenderer contains the rendering of all HUD icons, such as touchpad, item slots and timers.
 * 
 * @author LeeJunXiang
 */
public class HudRenderer {
	private enum ActionPreviewType {
		NONE, BAT, SHOTGUN, KNIFE, TRAP, DISARM_TRAP
	}

	private enum TutorialStep {
		CHARACTER_INTRO, HUD_OVERVIEW, ABILITY, ITEM_BEGIN, WEAPON, ITEM, SHOTGUN_OR_GHOST, COMPLETE, MAP
	}

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
	private BitmapFont hotkeyFont;
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
	private ShapeRenderer previewRenderer;
	private Stage stage;
	private Stage settingsStage;

	private Touchpad touchpad;
	private Actor aimDragArea;
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
	private final TouchPlayerInputController touchPlayerInputController;
	private final PlayerInputController playerInputController;
	private ActionPreviewType actionPreviewType;
	private TutorialStep tutorialStep;
	private final Color previewFillColor = new Color(0.2f, 0.9f, 0.3f, 0.22f);
	private final Color previewOutlineColor = new Color(0.35f, 1f, 0.45f, 0.85f);

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
		this.touchPlayerInputController = desktopControls ? null : new TouchPlayerInputController(touchpad);
		this.playerInputController = desktopControls ? new DesktopPlayerInputController()
				: touchPlayerInputController;

		BUTTON_WIDTH = 70;
		BUTTON_HEIGHT = 30;

		// countdown
		playTime = 240.0f;

		batch = new SpriteBatch();
		previewRenderer = new ShapeRenderer();
		actionPreviewType = ActionPreviewType.NONE;
		tutorialStep = tutorial ? TutorialStep.CHARACTER_INTRO : null;

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
				if (tutorialStep == TutorialStep.CHARACTER_INTRO) {
					stage.clear();
					stage.addActor(hudOverlay);
					stage.addActor(nextButton);
					TM.setDisplayMessage("Here are your controls... learn them well!");
					GWTM.setDisplayMessage("");
					tutorialStep = TutorialStep.HUD_OVERVIEW;
				} else if (tutorialStep == TutorialStep.HUD_OVERVIEW) {
					stage.clear();
					stage.addActor(abilityTut);
					stage.addActor(nextButton);
					TM.setDisplayMessage("Each class has unique ability, item and weapon");
					GWTM.setDisplayMessage("Try using your ability!");
					tutorialStep = TutorialStep.ABILITY;
				} else if (tutorialStep == TutorialStep.ABILITY) {
					stage.clear();
					stage.addActor(itemTutBegin);
					TM.setDisplayMessage("A weapon was spawned for you to your left");
					GWTM.setDisplayMessage("Try picking it up!");
					gWorld.createTutorialWeapon();
					tutorialStep = TutorialStep.ITEM_BEGIN;
				} else if (tutorialStep == TutorialStep.WEAPON) {
					stage.clear();
					TM.setDisplayMessage("An item was spawned for you to your left");
					GWTM.setDisplayMessage("Try picking it up!");
					gWorld.createTutorialItem();
				} else if (tutorialStep == TutorialStep.ITEM) {
					stage.clear();
					if ("Murderer".equals(gWorld.getPlayer().getType())) {
						stage.addActor(shotgunTutMur);
						stage.addActor(nextButton);
					}
					TM.setDisplayMessage("2 weapon parts were spawned for you to your left");
					GWTM.setDisplayMessage("Try picking them up!");
					gWorld.createTutorialWP();
					tutorialStep = TutorialStep.SHOTGUN_OR_GHOST;
				} else if (tutorialStep == TutorialStep.SHOTGUN_OR_GHOST) {
					stage.clear();
					stage.addActor(nextButton);
					TM.setDisplayMessage("Congrats! You completed the gameplay tutorial!");
					GWTM.setDisplayMessage("Click next to learn more about the map");
					tutorialStep = TutorialStep.COMPLETE;
				} else if (tutorialStep == TutorialStep.COMPLETE) {
					stage.clear();
					stage.addActor(mapTut);
					stage.addActor(nextButtonToMenu);
					cont = true;
					tutorialStep = TutorialStep.MAP;
				}
				if (!cont) {
					addTouchControls();
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
		stage = new Stage(PresentationFrame.createHudViewport(), batch);
		touchpad.setVisible(!desktopControls);
		aimDragArea = createAimDragArea();
		addTouchControls();
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

		settingsStage = new Stage(PresentationFrame.createHudViewport(), batch);
		settingsStage.addActor(getMainMenuButton());
		settingsStage.addActor(getMuteButton());
		settingsStage.addActor(getSettingsCloseButton());
		welcomeMsg = true;
		scale = 1f;
		layoutHud();
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
		hotkeyFont = new BitmapFont(Gdx.files.internal("Fonts/crimesFont36.fnt"));
		hotkeyFont.getData().setScale(0.35f, 0.35f);
		syncFont = AssetLoader.crimesFont36Sync;
		settingsButtonDraw = AssetLoader.settings_button_draw;
		pause_main = AssetLoader.pause_main;
		normalSettings = AssetLoader.normalSettings;
		settingsCloseDraw = AssetLoader.settings_cancel_draw;

	}

	private Actor createAimDragArea() {
		Actor actor = new Actor() {
			@Override
			public Actor hit(float x, float y, boolean touchable) {
				if (isPriorityButtonHit(x, y)) {
					return null;
				}
				return super.hit(x, y, touchable);
			}
		};
		actor.setName("aim drag area");
		actor.setBounds(0f, 0f, PresentationFrame.WIDTH, PresentationFrame.HEIGHT);
		actor.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (desktopControls || touchPlayerInputController == null
						|| x < event.getListenerActor().getWidth() / 2f) {
					return false;
				}
				touchPlayerInputController.beginFreeAim(pointer);
				return true;
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer) {
				if (touchPlayerInputController != null) {
					touchPlayerInputController.updateFreeAim(pointer);
				}
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if (touchPlayerInputController != null) {
					touchPlayerInputController.endFreeAim(pointer);
				}
			}
		});
		return actor;
	}

	private void addTouchControls() {
		if (!desktopControls && aimDragArea != null) {
			stage.addActor(aimDragArea);
		}
		stage.addActor(touchpad);
		restoreTutorialActorOrder();
	}

	private void restoreTutorialActorOrder() {
		if (!tutorial) {
			return;
		}
		raiseIfPresent(civCharTut);
		raiseIfPresent(murCharTut);
		raiseIfPresent(ghostCharTut);
		raiseIfPresent(itemTutBegin);
		raiseIfPresent(hudOverlay);
		raiseIfPresent(abilityTut);
		raiseIfPresent(weaponTut);
		raiseIfPresent(itemTut);
		raiseIfPresent(shotgunTut);
		raiseIfPresent(shotgunTutMur);
		raiseIfPresent(mapTut);
		raiseIfPresent(nextButton);
		raiseIfPresent(nextButtonToMenu);
	}

	private void raiseIfPresent(Actor actor) {
		if (actor != null && actor.getStage() == stage) {
			stage.addActor(actor);
		}
	}

	private boolean isPriorityButtonHit(float x, float y) {
		return isActorHit(nextButton, x, y) || isActorHit(nextButtonToMenu, x, y);
	}

	private boolean isActorHit(Actor actor, float x, float y) {
		return actor != null && actor.getStage() == stage && actor.isVisible() && x >= actor.getX()
				&& x <= actor.getX() + actor.getWidth() && y >= actor.getY()
				&& y <= actor.getY() + actor.getHeight();
	}

	private boolean isTutorialActorPresent(Actor actor) {
		return actor != null && actor.getStage() == stage;
	}

	private boolean isAnyTutorialOverlayPresent() {
		return isTutorialActorPresent(civCharTut) || isTutorialActorPresent(murCharTut)
				|| isTutorialActorPresent(ghostCharTut) || isTutorialActorPresent(itemTutBegin)
				|| isTutorialActorPresent(hudOverlay) || isTutorialActorPresent(abilityTut)
				|| isTutorialActorPresent(weaponTut) || isTutorialActorPresent(itemTut)
				|| isTutorialActorPresent(shotgunTut) || isTutorialActorPresent(shotgunTutMur)
				|| isTutorialActorPresent(mapTut);
	}

	private void layoutHud() {
		float width = PresentationFrame.WIDTH;
		float height = PresentationFrame.HEIGHT;
		touchpad.setBounds(gameWidth / 14f, gameHeight / 14f, gameWidth / 5f, gameWidth / 5f);
		touchKnob.setMinHeight(touchpad.getHeight() / 4f);
		touchKnob.setMinWidth(touchpad.getWidth() / 4f);
		if (aimDragArea != null) {
			aimDragArea.setBounds(0f, 0f, width, height);
		}
		settingsButton.setBounds(595f, 294f, 30f, 30f);
		nextButton.setBounds(550f, 150f, 77f, 62f);
		nextButtonToMenu.setBounds(550f, 150f, 77f, 62f);
		if (buttonMainMenu != null) {
			buttonMainMenu.setPosition(225f, 130f);
		}
		if (muteButton != null) {
			muteButton.setPosition(348f, 130f);
		}
		if (unmuteButton != null) {
			unmuteButton.setPosition(348f, 130f);
		}
		if (settingsCloseButton != null) {
			settingsCloseButton.setPosition(485f, 260f);
		}

		if (weaponButton != null) {
			weaponButton.setBounds(493f, 40f, 40f, 40f);
		}
		if (itemButton != null) {
			itemButton.setBounds(("Murderer".equals(player.getType()) || "Ghost".equals(player.getType())) ? 546f
					: 547f, ("Murderer".equals(player.getType()) || "Ghost".equals(player.getType())) ? 43f : 40f,
					40f, 40f);
		}
		if (dashButton != null) {
			dashButton.setBounds(520f, 94f, 33f, 33f);
		}
		if (disguiseToCiv != null) {
			disguiseToCiv.setBounds(522f, 92f, 33f, 33f);
		}
		if (disguiseToMur != null) {
			disguiseToMur.setBounds(522f, 92f, 33f, 33f);
		}
		if (hauntButton != null) {
			hauntButton.setBounds(518f, 85f, 33f, 33f);
		}

		layoutOverlay(civCharTut, width, height);
		layoutOverlay(murCharTut, width, height);
		layoutOverlay(ghostCharTut, width, height);
		layoutOverlay(itemTutBegin, width, height);
		layoutOverlay(hudOverlay, width, height);
		layoutOverlay(abilityTut, width, height);
		layoutOverlay(weaponTut, width, height);
		layoutOverlay(itemTut, width, height);
		layoutOverlay(shotgunTut, width, height);
		if (shotgunTutMur != null) {
			layoutOverlay(shotgunTutMur, width, height);
		}
		layoutOverlay(mapTut, width, height);
	}

	private void layoutOverlay(Image image, float width, float height) {
		if (image == null) {
			return;
		}
		image.setBounds(0f, 0f, PresentationFrame.WIDTH, PresentationFrame.HEIGHT);
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

	public void renderActionPreview(Camera worldCamera) {
		if (actionPreviewType == ActionPreviewType.NONE || inSettings || gWorld.getPlayer() == null
				|| !gWorld.getPlayer().isAlive()) {
			return;
		}

		Vector2 position = gWorld.getPlayer().getBody().getPosition();
		float angle = gWorld.getPlayer().getBody().getAngle();

		previewRenderer.setProjectionMatrix(worldCamera.combined);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		if (actionPreviewType == ActionPreviewType.TRAP) {
			float trapX = position.x + (float) (25f * Math.cos(angle));
			float trapY = position.y + (float) (25f * Math.sin(angle));

			previewRenderer.begin(ShapeRenderer.ShapeType.Filled);
			previewRenderer.setColor(previewFillColor);
			previewRenderer.circle(trapX, trapY, 10f, 24);
			previewRenderer.end();

			previewRenderer.begin(ShapeRenderer.ShapeType.Line);
			previewRenderer.setColor(previewOutlineColor);
			previewRenderer.circle(trapX, trapY, 10f, 24);
			previewRenderer.line(position.x, position.y, trapX, trapY);
			previewRenderer.end();
		} else {
			Vector2[] localVertices = getPreviewVertices(actionPreviewType);
			if (localVertices != null) {
				drawPolygonPreview(localVertices, position, angle);
			}
		}

		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	private void drawPolygonPreview(Vector2[] localVertices, Vector2 origin, float angle) {
		if (localVertices.length < 3) {
			return;
		}

		Vector2[] worldVertices = new Vector2[localVertices.length];
		float cos = (float) Math.cos(angle);
		float sin = (float) Math.sin(angle);
		for (int i = 0; i < localVertices.length; i++) {
			Vector2 vertex = localVertices[i];
			float worldX = origin.x + vertex.x * cos - vertex.y * sin;
			float worldY = origin.y + vertex.x * sin + vertex.y * cos;
			worldVertices[i] = new Vector2(worldX, worldY);
		}

		previewRenderer.begin(ShapeRenderer.ShapeType.Filled);
		previewRenderer.setColor(previewFillColor);
		for (int i = 1; i < worldVertices.length - 1; i++) {
			previewRenderer.triangle(worldVertices[0].x, worldVertices[0].y, worldVertices[i].x,
					worldVertices[i].y, worldVertices[i + 1].x, worldVertices[i + 1].y);
		}
		previewRenderer.end();

		previewRenderer.begin(ShapeRenderer.ShapeType.Line);
		previewRenderer.setColor(previewOutlineColor);
		for (int i = 0; i < worldVertices.length; i++) {
			Vector2 start = worldVertices[i];
			Vector2 end = worldVertices[(i + 1) % worldVertices.length];
			previewRenderer.line(start.x, start.y, end.x, end.y);
		}
		previewRenderer.end();
	}

	private Vector2[] getPreviewVertices(ActionPreviewType previewType) {
		switch (previewType) {
		case BAT:
			return new Vector2[] { new Vector2(18, 0), new Vector2(34.6f, 20), new Vector2(37.6f, 13.7f),
					new Vector2(39.4f, 6.95f), new Vector2(40, 0), new Vector2(39.4f, -6.95f),
					new Vector2(37.6f, -13.7f), new Vector2(34.6f, -20) };
		case SHOTGUN:
			return new Vector2[] { new Vector2(18, 0), new Vector2(43.3f, 25), new Vector2(47, 17.1f),
					new Vector2(49.2f, 8.7f), new Vector2(50, 0), new Vector2(49.2f, -8.7f),
					new Vector2(47, -17.1f), new Vector2(43.3f, -25) };
		case KNIFE:
		case DISARM_TRAP:
			return new Vector2[] { new Vector2(11, 0), new Vector2(20, 8.9f), new Vector2(28, 5.6f),
					new Vector2(32, 0), new Vector2(28, -5.6f), new Vector2(20, -8.9f) };
		case NONE:
		case TRAP:
		default:
			return null;
		}
	}

	private void showPreview(ActionPreviewType previewType) {
		actionPreviewType = previewType;
	}

	private void clearPreview() {
		actionPreviewType = ActionPreviewType.NONE;
		if (touchPlayerInputController != null) {
			touchPlayerInputController.clearActionAim();
		}
	}

	private void beginHeldActionAim(int pointer) {
		if (touchPlayerInputController != null) {
			touchPlayerInputController.beginActionAim(pointer);
		}
	}

	private void updateHeldActionAim(int pointer) {
		if (touchPlayerInputController != null) {
			touchPlayerInputController.updateActionAim(pointer);
		}
	}

	private void endHeldActionAim(int pointer) {
		if (touchPlayerInputController != null) {
			touchPlayerInputController.endActionAim(pointer);
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
				leaveCurrentMultiplayerRoom();
				try {
					if (game.mMultiplayerSession.isServer) {
						game.mMultiplayerSession.getServer().endSession();
						// System.out.println("Ended server session.");
					}
					game.mMultiplayerSession.getClient().endSession();
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

	private void leaveCurrentMultiplayerRoom() {
		if (game == null || game.mMultiplayerSession == null) {
			return;
		}
		final String roomId = game.mMultiplayerSession.mRoomId;
		final String occupantId = game.mMultiplayerSession.occupantId;
		final boolean host = game.mMultiplayerSession.isServer;
		if (roomId == null || occupantId == null) {
			return;
		}
		final String discoveryUrl = MultiplayerPreferences.getDiscoveryUrl();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					DiscoveryApiClient client = new DiscoveryApiClient(discoveryUrl);
					if (host) {
						client.closeRoom(roomId, occupantId);
					} else {
						client.leaveRoom(roomId, occupantId);
					}
				} catch (IOException e) {
					MMLog.log("MM-DISCOVERY", "Failed to leave room from in-game menu. roomId=" + roomId
							+ " occupantId=" + occupantId + " host=" + host, e);
				}
			}
		}, "leave-room-menu").start();
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
		drawHotkeyLabel("LMB", 498f, 31f);
		drawHotkeyLabel("E", 559f, 31f);
		drawHotkeyLabel("RMB", 520f, 125f);
		drawHotkeyLabel("W", 110f, 108f);
		drawHotkeyLabel("A", 82f, 80f);
		drawHotkeyLabel("S", 110f, 54f);
		drawHotkeyLabel("D", 138f, 80f);
	}

	private void drawHotkeyLabel(String label, float centerX, float baselineY) {
		GlyphLayout layout = new GlyphLayout(hotkeyFont, label);
		hotkeyFont.draw(batch, label, centerX - (layout.width / 2f), baselineY);
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
								WeaponsAnimationRunTime), 477, 25, 72, 72);
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
		clearPreview();
		player = gWorld.getPlayer();
		player.setItemChange(false);
		if (player.getItem() != null) {
			if (player.getType().equals("Murderer")) {
				if (tutorial) {
					stage.clear();
					stage.addActor(itemTut);
					stage.addActor(nextButton);
					GWTM.setDisplayMessage("Excellent! Try using your item");
					tutorialStep = TutorialStep.ITEM;
					addTouchControls();
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
						tutorialStep = TutorialStep.ITEM;
						addTouchControls();
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
		clearPreview();
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
					tutorialStep = TutorialStep.SHOTGUN_OR_GHOST;
					addTouchControls();
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
						tutorialStep = TutorialStep.WEAPON;
						addTouchControls();
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
					tutorialStep = TutorialStep.WEAPON;
					addTouchControls();
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
				tutorialStep = TutorialStep.SHOTGUN_OR_GHOST;
				addTouchControls();
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

		x = 595f;
		y = 294f;

		settingsButton = new ImageButton(settingsButtonDraw);
		settingsButton.setX(x);
		settingsButton.setY(y);
		settingsButton.setWidth(30f);
		settingsButton.setHeight(30f);
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
		weaponButton.setSize(40, 40);
		weaponButton.setName("Weapon Button");
		AssetLoader.pickUpItemSound.play(AssetLoader.VOLUME);
		if ("Ghost".equals(gWorld.getPlayer().getType())) {
			TM.setDisplayMessage("Picked Bat Up");
		} else
			TM.setDisplayMessage("Obtained Bat");

		weaponButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				showPreview(ActionPreviewType.BAT);
				beginHeldActionAim(pointer);
				return super.touchDown(event, x, y, pointer, button);
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer) {
				updateHeldActionAim(pointer);
				super.touchDragged(event, x, y, pointer);
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if (!desktopControls) {
					useCurrentWeapon();
				}
				endHeldActionAim(pointer);
				clearPreview();
				super.touchUp(event, x, y, pointer, button);
			}

			public void clicked(InputEvent event, float x, float y) {
				if (desktopControls) {
					useCurrentWeapon();
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
		weaponButton.setSize(40, 40);
		weaponButton.setName("Weapon Button");
		AssetLoader.pickUpItemSound.play(AssetLoader.VOLUME);
		TM.setDisplayMessage("Obtained Shotgun");
		weaponButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("Shotgun button touch down, draw hitbox");
				showPreview(ActionPreviewType.SHOTGUN);
				beginHeldActionAim(pointer);
				return super.touchDown(event, x, y, pointer, button);
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer) {
				updateHeldActionAim(pointer);
				super.touchDragged(event, x, y, pointer);
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if (!desktopControls) {
					useCurrentWeapon();
				}
				endHeldActionAim(pointer);
				clearPreview();
				super.touchUp(event, x, y, pointer, button);
			}

			public void clicked(InputEvent event, float x, float y) {
				if (desktopControls) {
					useCurrentWeapon();
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
		itemButton.setSize(40, 40);
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
				showPreview(ActionPreviewType.DISARM_TRAP);
				beginHeldActionAim(pointer);
				return super.touchDown(event, x, y, pointer, button);
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer) {
				updateHeldActionAim(pointer);
				super.touchDragged(event, x, y, pointer);
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if (!desktopControls) {
					useCurrentItem();
				}
				endHeldActionAim(pointer);
				clearPreview();
				super.touchUp(event, x, y, pointer, button);
			}

			public void clicked(InputEvent event, float x, float y) {
				if (desktopControls) {
					useCurrentItem();
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
		dashButton.setSize(33, 33);
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
		weaponButton.setSize(40, 40);
		weaponButton.setName("Weapon Button");
		AssetLoader.pickUpItemSound.play(AssetLoader.VOLUME);
		TM.setDisplayMessage("Obtained Knife");
		weaponButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				showPreview(ActionPreviewType.KNIFE);
				beginHeldActionAim(pointer);
				return super.touchDown(event, x, y, pointer, button);
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer) {
				updateHeldActionAim(pointer);
				super.touchDragged(event, x, y, pointer);
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if (!desktopControls) {
					useCurrentWeapon();
				}
				endHeldActionAim(pointer);
				clearPreview();
				super.touchUp(event, x, y, pointer, button);
			}

			public void clicked(InputEvent event, float x, float y) {
				if (desktopControls) {
					useCurrentWeapon();
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
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				showPreview(ActionPreviewType.TRAP);
				beginHeldActionAim(pointer);
				return super.touchDown(event, x, y, pointer, button);
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer) {
				updateHeldActionAim(pointer);
				super.touchDragged(event, x, y, pointer);
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if (!desktopControls) {
					useCurrentItem();
				}
				endHeldActionAim(pointer);
				clearPreview();
				super.touchUp(event, x, y, pointer, button);
			}

			public void clicked(InputEvent event, float x, float y) {
				if (desktopControls) {
					useCurrentItem();
				}
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
		hauntButton.setSize(33, 33);
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
		playerInputController.update(worldViewport,
				gWorld.getPlayer() == null ? null : gWorld.getPlayer().getBody().getPosition());
	}

	public PlayerInputController getPlayerInputController() {
		return playerInputController;
	}

	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		settingsStage.getViewport().update(width, height, true);
		TM.setFrameWidth(PresentationFrame.WIDTH);
		GWTM.setFrameWidth(PresentationFrame.WIDTH);
		layoutHud();
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
		syncDesktopActionPreview();
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

	private void syncDesktopActionPreview() {
		if (!desktopControls) {
			return;
		}
		if (playerInputController.isWeaponControlHeld() && player.getWeapon() != null) {
			if ("Shotgun".equals(player.getWeapon().getName())) {
				showPreview(ActionPreviewType.SHOTGUN);
			} else if ("Knife".equals(player.getWeapon().getName())) {
				showPreview(ActionPreviewType.KNIFE);
			} else {
				showPreview(ActionPreviewType.BAT);
			}
			return;
		}
		if (playerInputController.isItemControlHeld() && player.getItem() != null) {
			if ("Murderer".equals(gWorld.getPlayer().getType())) {
				showPreview(ActionPreviewType.TRAP);
			} else {
				showPreview(ActionPreviewType.DISARM_TRAP);
			}
			return;
		}
		clearPreview();
	}

	private void useCurrentWeapon() {
		System.out.println("Use current weapon");
		clearPreview();
		if (gWorld.getPlayer().getWeapon() == null) {
			return;
		}
		if (gWorld.getPlayer().useWeapon()) {
			WeaponsCD = true;
			if (player.getWeapon() != null && "Shotgun".equals(player.getWeapon().getName())) {
				TM.setDisplayMessage("Shotgun Fired!");
				AssetLoader.shotgunBlastSound.play(AssetLoader.VOLUME);
			} else if (player.getWeapon() != null && "Knife".equals(player.getWeapon().getName())) {
				TM.setDisplayMessage("Knife Thrust!");
				AssetLoader.knifeThrustSound.play(AssetLoader.VOLUME);
			} else if ("Ghost".equals(gWorld.getPlayer().getType())) {
				AssetLoader.pickUpItemSound.play(AssetLoader.VOLUME);
				TM.setDisplayMessage("Placed Bat Down");
			} else {
				TM.setDisplayMessage("Swung Bat");
				AssetLoader.batSwingSound.play(AssetLoader.VOLUME);
			}
			session.updatePlayerUseWeapon();
		}
	}

	private void useCurrentItem() {
		System.out.println("Use current item");
		clearPreview();
		if (gWorld.getPlayer().getItem() == null) {
			return;
		}
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

	/**
	 * Releases the resources held by objects or images loaded.
	 */
	public void hudDispose() {
		stage.dispose();
		settingsStage.dispose();
		batch.dispose();
		previewRenderer.dispose();
		hotkeyFont.dispose();
	}

}
