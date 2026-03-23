package com.jkjk.MMHelpers;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jkjk.GameObjects.Duration;

public class ToastMessage {
	
	private boolean show;
	private String message;
	private Duration ttl;
	private BitmapFont font;
	private float x;
	private float y;
	private float frameWidth;
	
	public ToastMessage(float y, int duration){
		this(PresentationFrame.WIDTH, y, duration);
	}

	public ToastMessage(float frameWidth, float y, int duration) {
		show = false;
		message = "";
		ttl = new Duration(duration);
		font = AssetLoader.basker32Message;
		font.getData().setScale(.5f, .5f);
		x = 0f;
		this.frameWidth = frameWidth;
		this.y = y;
	}
	
	public void setDisplayMessage(String s){
		GlyphLayout layout = new GlyphLayout(font, s);
		x = (frameWidth - layout.width) / 2f;
		ttl.startCountdown();
		this.message = s;
		show=true;
	}

	public void setFrameWidth(float frameWidth) {
		this.frameWidth = frameWidth;
	}
	
	public void render(SpriteBatch batch){
		if(show==true){
			font.draw(batch, message, x, y);
			ttl.update();
		}
		if(!ttl.isCountingDown()){
			show = false;
		}
		
	}
	
}
