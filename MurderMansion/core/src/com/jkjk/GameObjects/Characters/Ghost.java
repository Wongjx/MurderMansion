package com.jkjk.GameObjects.Characters;

import com.badlogic.gdx.physics.box2d.Body;
import com.jkjk.GameObjects.Items.Item;


public class Ghost extends GameCharacter {

	private Item item;
	private Body body;
	
	public Ghost(Body body) {
		this.body = body;
	}

}
