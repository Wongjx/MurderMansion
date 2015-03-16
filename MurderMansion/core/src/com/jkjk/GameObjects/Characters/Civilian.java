package com.jkjk.GameObjects.Characters;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.jkjk.GameObjects.Items.Item;
import com.jkjk.GameObjects.Weapons.Weapon;
import com.jkjk.GameWorld.GameWorld;


public class Civilian extends GameCharacter {
	
	private FixtureDef fdef;
	private FixtureDef coneFdef;
	private BodyDef coneBdef;
	
	Civilian(int colour, Body body, World world){
		setName("Civilian");
		setColour(colour);
		
		// create player
		Vector2[] vertices = {new Vector2(0,0), new Vector2(-20,-10), new Vector2(-20,10)};
		PolygonShape shape = new PolygonShape();
		fdef = new FixtureDef();
		shape.set(vertices);
		fdef.shape = shape;
		
		body.createFixture(fdef).setUserData("civilian");
		setBody(body);
		
		Vector2[] ConeLightVert = {new Vector2(0,0), new Vector2(100,100), new Vector2(100,-100)};//triangle first for testing
		PolygonShape coneShape = new PolygonShape();
		coneFdef = new FixtureDef();
		coneFdef.isSensor = true;
		coneShape.set(ConeLightVert);
		coneFdef.shape = coneShape;
		coneFdef.filter.maskBits = 1;
		
		coneBdef = new BodyDef();
		coneBdef.type = BodyType.StaticBody;
		coneBdef.position.set(getBody().getPosition());
		coneBdef.angle = getBody().getAngle();
		
		Body coneBody = world.createBody(coneBdef);
		coneBody.createFixture(coneFdef).setUserData("lightBody");
		setLightBody(coneBody);
	}
	

	
//	public void update(){
//		Vector2 v2 = getBody().getPosition();
//		getLightBody().setTransform(v2.x,v2.y,getBody().getAngle());
//	}
}
