package com.jkjk.MMHelpers;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jkjk.GameObjects.Duration;

public class ToastMessage {
	
	private boolean show;
	private String message;
	private Duration ttl;
	private BitmapFont font;
	private float x;
	private float y;
	
	public ToastMessage(float y, int duration){
		show = false;
		message = "";
		ttl = new Duration(duration);
		font = AssetLoader.basker32Message;
		font.setScale(.5f,.5f);
		x = 0f;
		this.y = y;
	}
	
	public void setDisplayMessage(String s){
		x = 320-(font.getBounds(s).width/2);
		ttl.startCountdown();
		this.message = s;
		show=true;
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
