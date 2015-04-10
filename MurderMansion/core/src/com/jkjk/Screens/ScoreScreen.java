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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jkjk.MMHelpers.AssetLoader;
import com.jkjk.MurderMansion.MurderMansion;

/**
 * @author LeeJunXiang
 *
 */
public class ScoreScreen implements Screen {
	
	private MurderMansion game;
	
	private Stage stage;
	private Table table;
	
	private float gameWidth;
    private float gameHeight;
    private float TITLE_PAD;
    private float BUTTON_WIDTH;
    private float BUTTON_HEIGHT;
    private float BUTTON_PAD;
    
    private SpriteBatch batch;
    private Texture background;
    private Sprite sprite;

    private TextButtonStyle normal = AssetLoader.normal;

    private TextButton buttonMenu = new TextButton("Main Menu",normal);
	
	private boolean murWin;
	
	/**
	 * Score screen shows score board
	 * 
	 * @param murWin who won the game? murderer or civilian?
	 */
	public ScoreScreen(MurderMansion game, float gameWidth, float gameHeight, boolean murWin){
		this.murWin = murWin;
		initAssets(gameWidth, gameHeight);
		
		// Create a Stage and add TouchPad
		stage = new Stage();
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

	}
	
	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#show()
	 */
	@Override
	public void show() {
    	
    	batch = new SpriteBatch();
    	background = AssetLoader.menuBackground;
    	background.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    	sprite = new Sprite(background);
    	sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    	
        
        buttonMenu.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	((Game)Gdx.app.getApplicationListener()).setScreen(new MenuScreen(game, gameWidth, gameHeight));
            }
        });
        table.add(buttonMenu).size(this.BUTTON_WIDTH,this.BUTTON_HEIGHT).padBottom(this.BUTTON_PAD).row();

        table.setFillParent(true);
        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#render(float)
	 */
	@Override
	public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
		stage.draw();
		stage.act(Gdx.graphics.getDeltaTime()); // Acts stage at deltatime
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#resize(int, int)
	 */
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#pause()
	 */
	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#resume()
	 */
	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#hide()
	 */
	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
