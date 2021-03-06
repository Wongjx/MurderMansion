/**
 * 
 */
package com.jkjk.Screens;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.GameWorld.MMClient;
import com.jkjk.MMHelpers.AssetLoader;
import com.jkjk.MurderMansion.MurderMansion;

/**
 * @author LeeJunXiang
 * 
 */
public class ScoreScreen implements Screen {

	private MurderMansion game;
	private MMClient client;

	private String[] names;
	private int numOfNames;
	private final ConcurrentHashMap<String, Integer> playerIsAlive;
	private final ConcurrentHashMap<String, Integer> playerType;

	private Stage stage;
	private float animationRunTime;
	private Animation score_animation;
	private Texture score_texture;

	private float gameWidth;
	private float gameHeight;
	private float BUTTON_WIDTH;
	private float BUTTON_HEIGHT;

	private SpriteBatch batch;
	private Sprite sprite;

	private ImageButtonStyle normal1;
	private ImageButton nextButton;

	private Table table;

	private Label[] label_array;
	private LabelStyle scoreLabelStyle;

	private Image[] image_array;
	private Texture rip;
	private Texture civ_char0;
	private Texture civ_char1;
	private Texture civ_char2;
	private Texture civ_char3;
	private Texture mur_char;

	private Integer status;
	private Integer type;

	// for testing only
	// private Label scoreLabel;
	// private Label scoreLabel1;
	// private Label scoreLabel2;
	// private Label scoreLabel3;
	// private Label scoreLabel4;
	// private Label scoreLabel5;
	// private Label scoreLabel6;
	// private Image rip_image;
	// private Image rip_image1;
	// private Image rip_image2;
	// private Image rip_image3;
	// private Image civ_char_image;
	// private Image mur_char_image;

	/**
	 * Score screen
	 * 
	 * @param murWin
	 *            who won the game? murderer or civilian?
	 */
	public ScoreScreen(MurderMansion game, float gameWidth, float gameHeight, MMClient client) {
		this.client = client;
		this.gameHeight = gameHeight;
		this.gameWidth = gameWidth;
		this.game = game;
		initAssets(gameWidth, gameHeight);

		// public ScoreScreen(MurderMansion game, float gameWidth, float gameHeight) {
		// this.gameHeight=gameHeight;
		// this.gameWidth=gameWidth;
		// this.game=game;
		// initAssets(gameWidth, gameHeight);

		// names = new String[]{"wong","jx","enyan","kat"};

		names = client.getParticipantNames();
		//
		numOfNames = names.length;
		playerIsAlive = client.get_playerIsAlive();
		playerType = client.get_playerType();
		status = 1; // default test = alive
		type = 0; // default test = murderer

		// Create a Stage and add TouchPad
		stage = new Stage(new ExtendViewport(gameWidth, gameHeight));
		batch = new SpriteBatch();
		sprite = new Sprite(score_texture);
		sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		BUTTON_WIDTH = 60;
		BUTTON_HEIGHT = 60;

		nextButton = new ImageButton(normal1);
		table = new Table();

		label_array = new Label[numOfNames];
		image_array = new Image[numOfNames];
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
		normal1 = AssetLoader.normal1;
		rip = AssetLoader.rip;
		civ_char0 = AssetLoader.civ_char0;
		civ_char1 = AssetLoader.civ_char1;
		civ_char2 = AssetLoader.civ_char2;
		civ_char3 = AssetLoader.civ_char3;
		mur_char = AssetLoader.mur_char;
		scoreLabelStyle = AssetLoader.scoreLabelStyle;
		score_animation = AssetLoader.score_background_animation;
		score_texture = AssetLoader.score_texture;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.Screen#show()
	 */
	@Override
	public void show() {
		//Unlock An AMAZING GAME
		if(game.actionResolver.getSignedInGPGS()){
			game.actionResolver.unlockAchievementGPGS(game.actionResolver.ACHEIVEMENT_1);
		}
		
		
		table.padTop(120);

		// FIRST ROW: NAME OF PLAYERS
		for (int i = 0; i < numOfNames; i++) {
			System.out.println("Name of player: " + names[i]);
			label_array[i] = new Label(names[i], scoreLabelStyle);
			label_array[i].setAlignment(Align.center);
			label_array[i].setWrap(true);
			label_array[i].setWidth(BUTTON_WIDTH);
		}
		for (int i = 0; i < numOfNames; i++) {
			table.add(label_array[i]).size(82, 48).spaceRight(10);
		}

		table.row();
		// SECOND ROW: STATUS & TYPES OF PLAYERS
		for (int i = 0; i < numOfNames; i++) {

			// status = 1 = alive
			// if character is dead
			if (status != playerIsAlive.get("Player " + i)) {
				image_array[i] = new Image(rip);
			}
			// if character is alive
			else {
				// type = 0 = murderer
				// if character is civilian
				// TODO: ASSIGN DIFFERENT CHARACTER FOR DIFFERENT IDs
				if (type != playerType.get("Player " + i)) {
					image_array[i] = new Image(civ_char0);
				}
				// if character is murderer
				else {
					image_array[i] = new Image(mur_char);
				}
			}
		}

		for (int i = 0; i < numOfNames; i++) {
			table.add(image_array[i]).size(82, 127).spaceRight(10);
		}

		// the following works for testing
		// scoreLabel1 = new Label("Katherine",scoreLabelStyle);
		// scoreLabel1.setAlignment(Align.center);
		// scoreLabel2 = new Label("Enyan",scoreLabelStyle);
		// scoreLabel2.setAlignment(Align.center);
		// scoreLabel3 = new Label("JX",scoreLabelStyle);
		// scoreLabel3.setAlignment(Align.center);
		// scoreLabel4 = new Label("Wong",scoreLabelStyle);
		// scoreLabel4.setAlignment(Align.center);
		// scoreLabel5 = new Label("Enyan",scoreLabelStyle);
		// scoreLabel5.setAlignment(Align.center);
		// scoreLabel6 = new Label("Katherine",scoreLabelStyle);
		// scoreLabel6.setAlignment(Align.center);

		// rip_image = new Image(rip);
		// rip_image1 = new Image(rip);
		// rip_image2 = new Image(rip);
		// rip_image3 = new Image(rip);
		// civ_char_image = new Image(civ_char0);
		// mur_char_image = new Image(mur_char);

		// table.add(scoreLabel1).size(82, 48).spaceRight(10);
		// table.add(scoreLabel2).size(82, 48).spaceRight(10);
		// table.add(scoreLabel3).size(82, 48).spaceRight(10);
		// table.add(scoreLabel4).size(82, 48).spaceRight(10);
		// table.add(scoreLabel5).size(82, 48).spaceRight(10);
		// table.add(scoreLabel6).size(82, 48).spaceRight(10);
		// table.row();
		// table.add(rip_image).size(80, 150).spaceRight(22);
		// table.add(civ_char_image).size(80, 150).spaceRight(22);
		// table.add(mur_char_image).size(80, 150).spaceRight(22);
		// table.add(rip_image1).size(80, 150).spaceRight(22);
		// table.add(rip_image2).size(80, 150).spaceRight(22);
		// table.add(rip_image3).size(80, 150).spaceRight(22);

		nextButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
				try {
					if (game.mMultiplayerSession.isServer) {
						game.mMultiplayerSession.getServer().endSession();
						// System.out.println("Ended server session.");
					}
					game.mMultiplayerSession.getClient().endSession();

					game.actionResolver.leaveRoom();

					// System.out.println("End mMultiplayer session");
					game.mMultiplayerSession.endSession();
				} catch (Exception e) {
					System.out.println("Error on button press: " + e.getMessage());
				}
				if (game.mMultiplayerSession.mState == game.mMultiplayerSession.ROOM_MENU) {
					((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen(game, gameWidth,
							gameHeight));
				}
			}
		});

		nextButton.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
		nextButton.setPosition(560, 10);
		table.setFillParent(true);
		stage.addActor(nextButton);
		stage.addActor(table);

		Gdx.input.setInputProcessor(stage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.Screen#render(float)
	 */
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		sprite.draw(batch);
		// TODO: ANIMATION NOT WORKING :(
		// animationRunTime += Gdx.graphics.getRawDeltaTime();
		// batch.draw(score_animation.getKeyFrame(animationRunTime), 0, 0, 640, 360);
		batch.end();
		stage.draw();
		stage.act(delta); // Acts stage at deltatime
		// table.debugAll();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.Screen#resize(int, int)
	 */
	@Override
	public void resize(int width, int height) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.Screen#pause()
	 */
	@Override
	public void pause() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.Screen#resume()
	 */
	@Override
	public void resume() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.Screen#hide()
	 */
	@Override
	public void hide() {
		dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.Screen#dispose()
	 */
	@Override
	public void dispose() {
		stage.dispose();
		client.getgWorld().getWorld().dispose();
	}

}
