package com.jkjk.GameObjects.Characters;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
<<<<<<< HEAD
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
=======
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
>>>>>>> 7a9044a096b7b23eb8ae3af3c2530f98358cea16
import com.jkjk.GameObjects.Items.Item;
import com.jkjk.GameObjects.Weapons.Weapon;
import com.jkjk.GameWorld.GameWorld;

public class Civilian extends GameCharacter {

	private FixtureDef fdef;
<<<<<<< HEAD
	private BodyDef bdef;
	private Body body;
	private World world;

	Civilian(int colour, World world) {
		this.world = world;
		fdef = new FixtureDef();
		bdef = new BodyDef();
=======
	private FixtureDef coneFdef;
	private BodyDef coneBdef;
	
	Civilian(int colour, Body body, World world){
>>>>>>> 7a9044a096b7b23eb8ae3af3c2530f98358cea16
		setName("Civilian");
		setColour(colour);

		bdef.type = BodyType.DynamicBody;
		body = world.createBody(bdef);

		Vector2[] vertices = { new Vector2(0, 0), new Vector2(-20, -10), new Vector2(-20, 10) };
		PolygonShape shape = new PolygonShape();
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
<<<<<<< HEAD

	@Override
	public void spawn(float x, float y, float angle) {
		alive = true;
		body.setTransform(x, y, angle); // Spawn position
	}

	public void die() {
		alive = false;
	}
=======
	

	
//	public void update(){
//		Vector2 v2 = getBody().getPosition();
//		getLightBody().setTransform(v2.x,v2.y,getBody().getAngle());
//	}
>>>>>>> 7a9044a096b7b23eb8ae3af3c2530f98358cea16
}
