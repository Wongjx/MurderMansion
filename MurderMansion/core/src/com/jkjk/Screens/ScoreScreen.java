/**
 * 
 */
package com.jkjk.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.jkjk.GameWorld.MMClient;
import com.jkjk.MMHelpers.AssetLoader;
import com.jkjk.MurderMansion.MurderMansion;

/**
 * @author LeeJunXiang
 * 
 */
public class ScoreScreen implements Screen {

	private MurderMansion game;

	private Stage stage;

	private float gameWidth;
	private float gameHeight;
	private float BUTTON_WIDTH;
	private float BUTTON_HEIGHT;

	private SpriteBatch batch;
	private Texture background;
	private Sprite sprite;

	private ImageButtonStyle normal1;
	private ImageButton nextButton;
	
	private Table table;
	private Texture rip;
	private Image rip_image;

	private boolean murWin;

	/**
	 * Score screen shows score board
	 * 
	 * @param murWin
	 *            who won the game? murderer or civilian?
	 */
	public ScoreScreen(MurderMansion game, float gameWidth, float gameHeight, boolean murWin) {
		this.murWin = murWin;
		this.gameHeight=gameHeight;
		this.gameWidth=gameWidth;
		this.game=game;
		initAssets(gameWidth, gameHeight);

		// Create a Stage and add TouchPad
		stage = new Stage(new ExtendViewport(gameWidth, gameHeight));

		BUTTON_WIDTH = 60;
		BUTTON_HEIGHT = 60;

		nextButton = new ImageButton(normal1);
		
		table = new Table();
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

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.Screen#show()
	 */
	@Override
	public void show() {    	
    	batch = new SpriteBatch();
    	background = AssetLoader.scoreBackground;
    	background.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    	sprite = new Sprite(background);
    	sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    	
    	rip_image = new Image(rip);
    	table.add(rip_image);
    	
        nextButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
            	try{
            		if(game.mMultiplayerSession.isServer){
                		game.mMultiplayerSession.getServer().endSession();
//                		System.out.println("Ended server session.");
                	}

            		if (game.mMultiplayerSession.getClient()!=null){
            			game.mMultiplayerSession.getClient().endSession();
            		}else{
            			//TODO HALP HALP HALP CLIENT NOT SUPPOSED TO BE NULL
            			System.out.println("CLIENT IS NULL?!!!?");
            		}
            		
            		game.actionResolver.leaveRoom();
            		
//            		System.out.println("End mMultiplayer session");
                	game.mMultiplayerSession.endSession();
            	}catch(Exception e){
            		System.out.println("Error on button press: "+e.getMessage());
            	}
            	((Game)Gdx.app.getApplicationListener()).setScreen(new MenuScreen(game, gameWidth, gameHeight));
            }
        });
        
        nextButton.setSize(this.BUTTON_WIDTH,this.BUTTON_HEIGHT);
        nextButton.setPosition(560, 20);
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
		batch.end();
		
		stage.draw();
		stage.act(Gdx.graphics.getDeltaTime()); // Acts stage at deltatime
		
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
	}

}
