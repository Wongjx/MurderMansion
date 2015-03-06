package com.jkjk.GameWorld;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.jkjk.GameObjects.Characters.Civilian;
import com.jkjk.GameObjects.Characters.GameCharacter;
import com.jkjk.GameObjects.Characters.GameCharacterFactory;
import com.jkjk.GameObjects.Characters.Murderer;
import com.jkjk.MMHelpers.MMContactListener;

public class GameWorld {
	GameCharacterFactory gameCharFac;
	private Civilian civilian;
	private Murderer murderer;
	private ArrayList<GameCharacter> playerList;

	private GameRenderer renderer;
	private World world;
	private MMContactListener cl;
	private BodyDef bdef;
	private Body body;
	private ShapeRenderer shapeRenderer;

	private float screenWidth;
	private float screenHeight;
	private int numOfPlayers;

	public GameWorld(float screenWidth, float screenHeight) {
		gameCharFac = new GameCharacterFactory();
		// create WORLD
		cl = new MMContactListener();
		world = new World(new Vector2(0, 0), true);
		world.setContactListener(cl);
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		playerList = new ArrayList<GameCharacter>();
		numOfPlayers = 3;

		// create BODY for WORLD
		bdef = new BodyDef();
		bdef.position.set(150, 150);
		// static body - don't move, unaffected by forces *eg. ground
		// kinematic body - don't get affected by forces, but can move *eg. moving platform
		// dynamic body - always get affected by forces *eg. player
		bdef.type = BodyType.StaticBody;
		body = world.createBody(bdef);

		// create FIXTURE for BODY for WORLD
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(100, 10);
		FixtureDef fdef = new FixtureDef();
		fdef.shape = shape;
		body.createFixture(fdef).setUserData("wall");

		createPlayer();
		for (int i = 0;i<numOfPlayers;i++){
			createOpponents(i);
		}
	}
	
	private void createPlayer(){
		bdef.type = BodyType.DynamicBody;
		bdef.position.set(100, 100); // Spawn position
		body = world.createBody(bdef);
		civilian = (Civilian) gameCharFac.createCharacter("Civilian", 0, body);
		civilian.spawn();
	}
	
	private void createOpponents(int i){
		if (i==numOfPlayers){
			bdef.type = BodyType.KinematicBody;
			bdef.position.set(100-((i+1)*40), 100); // Spawn position
			body = world.createBody(bdef);
			playerList.add((Murderer) gameCharFac.createCharacter("Murderer", body));
			playerList.get(i).spawn();
		} else {
			bdef.type = BodyType.KinematicBody;
			bdef.position.set(100-((i+1)*40), 100); // Spawn position
			body = world.createBody(bdef);
			playerList.add((Civilian) gameCharFac.createCharacter("Civilian", i+1, body));
			playerList.get(i).spawn();
		}
	}
	
	public World getWorld(){
		return world;
	}

	public int getNumOfPlayers() {
		return numOfPlayers;
	}

	public void setNumOfPlayers(int i) {
		numOfPlayers = i;
	}

	public GameCharacter getPlayer() {
		return civilian;
	}

	public void checkBat() {
		if (cl.isBatContact())
			System.out.println(cl.isBatContact());
	}

	public void update(float delta) {
		checkBat();
		world.step(delta, 6, 2); // Step size|Steps for each body to check collision|Accuracy of body position
									// after collision
	}

	public void setRenderer(GameRenderer renderer) {
		this.renderer = renderer;
	}
}
