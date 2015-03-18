package com.jkjk.MMHelpers;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;
import com.jkjk.GameWorld.GameWorld;

/**
 * @author JunXiang Will handle contact between weapons and characters
 */
public class MMContactListener implements ContactListener {

	private Fixture fa;
	private Fixture fb;
	private Object faUD;
	private Object fbUD;
	private Array<Body> itemsToRemove;
	private Array<Body> weaponsToRemove;
	private GameWorld gWorld;
	private boolean atStairs;
	private String stairsName;

	public MMContactListener(GameWorld gWorld) {
		itemsToRemove = new Array<Body>();
		weaponsToRemove = new Array<Body>();
		this.gWorld = gWorld;
		atStairs = false;
		stairsName = null;
	}

	// called when two fixtures start to collide
	public void beginContact(Contact c) {
		fa = c.getFixtureA();
		fb = c.getFixtureB();
		faUD = fa.getUserData();
		fbUD = fb.getUserData();

		System.out.println("Begin contact: fa: " + faUD + ", fb: " + fbUD);
		if (faUD != null && fbUD != null) {
			if (faUD.equals("player") || fbUD.equals("player")) {
				if (faUD.equals("item") && gWorld.getPlayer().getItem() == null) {
					itemsToRemove.add(fa.getBody());
				} else if (fbUD.equals("item") && gWorld.getPlayer().getItem() == null) {
					itemsToRemove.add(fb.getBody());
				} else if (fbUD.equals("weapon") && gWorld.getPlayer().getWeapon() == null) {
					weaponsToRemove.add(fb.getBody());
				} else if (faUD.equals("weapon") && gWorld.getPlayer().getWeapon() == null) {
					weaponsToRemove.add(fa.getBody());
				} else if (faUD.equals("L1S1") || fbUD.equals("L1S1")) {
					atStairs = true;
					stairsName = "L1S1";
				} else if (faUD.equals("L1S2") || fbUD.equals("L1S2")) {
					atStairs = true;
					stairsName = "L1S2";
				} else if (faUD.equals("L1S3") || fbUD.equals("L1S3")) {
					atStairs = true;
					stairsName = "L1S3";
				} else if (faUD.equals("L1S4") || fbUD.equals("L1S4")) {
					atStairs = true;
					stairsName = "L1S4";
				} else if (faUD.equals("L2S1") || fbUD.equals("L2S1")) {
					atStairs = true;
					stairsName = "L2S1";
				} else if (faUD.equals("L2S2") || fbUD.equals("L2S2")) {
					atStairs = true;
					stairsName = "L2S2";
				} else if (faUD.equals("L2S3") || fbUD.equals("L2S3")) {
					atStairs = true;
					stairsName = "L2S3";
				} else if (faUD.equals("L2S4") || fbUD.equals("L2S4")) {
					atStairs = true;
					stairsName = "L2S4";
				}

				if (fbUD.equals("lightBody")) {
					System.out.println("draw sprite");
				}

				if (fbUD.equals("lightBody") && !faUD.equals("lightBody")) {// not into contact with another
																			// light body
					System.out.println("draw sprite");
				}
			}
		}

	}

	public void notAtStairs() {
		atStairs = false;
	}

	public boolean getAtStairs() {
		return atStairs;
	}

	public String getStairsName() {
		return stairsName;
	}

	// called when two fixtures no longer collide
	public void endContact(Contact c) {
	}

	public Array<Body> getItemsToRemove() {
		return itemsToRemove;
	}

	public Array<Body> getWeaponsToRemove() {
		return weaponsToRemove;
	}

	// collision detection - when two objects collide
	// presolve
	// collision handling - what happens when it happens
	// postsolve
	public void preSolve(Contact c, Manifold m) {
	}

	public void postSolve(Contact c, ContactImpulse ci) {
	}

}
