package com.jkjk.GameObjects.Characters;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.jkjk.GameObjects.Items.Item;
import com.jkjk.GameObjects.Weapons.Weapon;


public class Murderer extends GameCharacter {

	private boolean disguised;
	private FixtureDef fdef;
	
	public Murderer(Body body) {
		setName("Murderer");
		
		// create player
		Vector2[] vertices = {new Vector2(0,0), new Vector2(-20,-10), new Vector2(-20,10)};
		PolygonShape shape = new PolygonShape();
		fdef = new FixtureDef();
		shape.set(vertices);
		fdef.shape = shape;
		body.createFixture(fdef).setUserData("murderer");
		setBody(body);
	}
	
	public boolean isDisguised(){
		return disguised;
	}

}
