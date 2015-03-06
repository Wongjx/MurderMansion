package com.jkjk.MMHelpers;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;

/**
 * @author JunXiang Will handle contact between weapons and characters
 */
public class MMContactListener implements ContactListener {

	private boolean batContact;
	private Fixture fa;
	private Fixture fb;
	private Array<Body> bodiesToRemove;

	public MMContactListener() {
		bodiesToRemove = new Array<Body>();
	}

	// called when two fixtures start to collide
	public void beginContact(Contact c) {
		System.out.println("Begin contact");
		fa = c.getFixtureA();
		fb = c.getFixtureB();

		System.out.println(fa.getUserData() + ", " + fb.getUserData());

		if (fa.getUserData().equals("civilian") && fb.getUserData().equals("item"))
			bodiesToRemove.add(fb.getBody());
		else if (fa.getUserData().equals("murderer") && fb.getUserData().equals("item"))
			bodiesToRemove.add(fb.getBody());
		else if (fa.getUserData().equals("civilian") && fb.getUserData().equals("weapon"))
			bodiesToRemove.add(fb.getBody());
		else if (fa.getUserData().equals("murderer") && fb.getUserData().equals("weapon"))
			bodiesToRemove.add(fb.getBody());

	}

	// called when two fixtures no longer collide
	public void endContact(Contact c) {
		System.out.println("End contact");
		fa = c.getFixtureA();
		fb = c.getFixtureB();
	}

	public Array<Body> getBodies() {
		return bodiesToRemove;
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
