package com.jkjk.MMHelpers;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.GameObjects.Items.DisarmTrap;

/**
 * @author JunXiang Will handle contact between weapons and characters
 */
public class MMContactListener implements ContactListener {

	private Fixture fa;
	private Fixture fb;
	private Object faUD;
	private Object fbUD;
	private ConcurrentLinkedQueue<Body> itemsToRemove;
	private ConcurrentLinkedQueue<Body> weaponsToRemove;
	private ConcurrentLinkedQueue<Body> trapToRemove;
	private ConcurrentLinkedQueue<Body> weaponPartsToRemove;

	private GameWorld gWorld;
	private boolean atStairs;
	private String stairsName;

	public MMContactListener(GameWorld gWorld) {
		itemsToRemove = new ConcurrentLinkedQueue<Body>();
		weaponsToRemove = new ConcurrentLinkedQueue<Body>();
		weaponPartsToRemove = new ConcurrentLinkedQueue<Body>();
		trapToRemove = new ConcurrentLinkedQueue<Body>();
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

		if (faUD != null && fbUD != null) {
			System.out.println("Begin contact: fa: " + faUD + ", fb: " + fbUD);
			if (faUD.equals("player") || fbUD.equals("player")) {
				if (faUD.equals("item") && gWorld.getPlayer().getItem() == null) {
					if (!gWorld.getPlayer().getType().equals("Ghost")) {
						itemsToRemove.offer(fa.getBody());
					}
				} else if (fbUD.equals("item")
						&& gWorld.getPlayer().getItem() == null) {
					if (!gWorld.getPlayer().getType().equals("Ghost")) {
						itemsToRemove.offer(fb.getBody());
					}
				} else if (fbUD.equals("weapon")
						&& gWorld.getPlayer().getWeapon() == null) {
					if (!gWorld.getPlayer().getType().equals("Ghost")) {
						weaponsToRemove.offer(fb.getBody());
					}
				} else if (faUD.equals("weapon")
						&& gWorld.getPlayer().getWeapon() == null) {
					if (!gWorld.getPlayer().getType().equals("Ghost")) {
						weaponsToRemove.offer(fa.getBody());
					}
				} else if (faUD.equals("weapon part")) {
					if (!gWorld.getPlayer().getType().equals("Ghost")) {
						weaponPartsToRemove.offer(fa.getBody());
						AssetLoader.pickUpItemSound.play(AssetLoader.VOLUME);
					}
				} else if (fbUD.equals("weapon part")) {
					if (!gWorld.getPlayer().getType().equals("Ghost")) {
						weaponPartsToRemove.offer(fb.getBody());
						AssetLoader.pickUpItemSound.play(AssetLoader.VOLUME);
					}
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
				} else if (faUD.equals("L1S5") || fbUD.equals("L1S5")) {
					atStairs = true;
					stairsName = "L1S5";
				} else if (faUD.equals("LbS1") || fbUD.equals("LbS1")) {
					atStairs = true;
					stairsName = "LbS1";
				} else if (faUD.equals("bat") || fbUD.equals("bat")) {
					if (!gWorld.getPlayer().getType().equals("Ghost")) {
						gWorld.getPlayer().stun();
						AssetLoader.batHitSound.play(AssetLoader.VOLUME);
					}
				} else if (faUD.equals("trap")) {
					if (gWorld.getPlayer().getType().equals("Civilian")) {
						gWorld.getPlayer().die();
						trapToRemove.offer(fa.getBody());
						AssetLoader.trappedSound.play(AssetLoader.VOLUME);
					}
				} else if (fbUD.equals("trap")) {
					if (gWorld.getPlayer().getType().equals("Civilian")) {
						gWorld.getPlayer().die();
						trapToRemove.offer(fb.getBody());
						AssetLoader.trappedSound.play(AssetLoader.VOLUME);
					}
				} else if (faUD.equals("knife") || fbUD.equals("knife")) {
					if (gWorld.getPlayer().getType().equals("Civilian")) {
						gWorld.getPlayer().die();
						AssetLoader.knifeStabSound.play(AssetLoader.VOLUME);
					}
				} else if (faUD.equals("shotgun") || fbUD.equals("shotgun")) {
					if (!gWorld.getPlayer().getType().equals("Ghost")) {
						gWorld.getPlayer().die();
						AssetLoader.shotgunBlastSound.play(AssetLoader.VOLUME);
					}
				} else if (faUD.equals("haunt") || fbUD.equals("haunt")) {
					if (!gWorld.getPlayer().getType().equals("Ghost")) {
						gWorld.getPlayer().haunt(true);
					}
				} else if (faUD.equals("saferegion")
						|| fbUD.equals("saferegion")) {
					gWorld.setInSafeArea(true);
				}
			} else if (faUD.equals("dummy") || fbUD.equals("dummy")) {
				if (faUD.equals("bat") || fbUD.equals("bat")) {
					if (!gWorld.getDummy().getType().equals("Ghost")) {
						gWorld.getDummy().stun();
						AssetLoader.batHitSound.play(AssetLoader.VOLUME);
					}
				} else if (faUD.equals("trap")) {
					if (gWorld.getDummy().getType().equals("Civilian")) {
						gWorld.getDummy().die();
						trapToRemove.offer(fa.getBody());
						AssetLoader.trappedSound.play(AssetLoader.VOLUME);
					}
				} else if (fbUD.equals("trap")) {
					if (gWorld.getDummy().getType().equals("Civilian")) {
						gWorld.getDummy().die();
						trapToRemove.offer(fb.getBody());
						AssetLoader.trappedSound.play(AssetLoader.VOLUME);
					}
				} else if (faUD.equals("knife") || fbUD.equals("knife")) {
					if (!gWorld.getDummy().getType().equals("Ghost")) {
						gWorld.getDummy().die();
						AssetLoader.knifeStabSound.play(AssetLoader.VOLUME);
					}
				} else if (faUD.equals("shotgun") || fbUD.equals("shotgun")) {
					if (!gWorld.getDummy().getType().equals("Ghost")) {
						gWorld.getDummy().die();
						AssetLoader.shotgunBlastSound.play(AssetLoader.VOLUME);
					}
				} else if (faUD.equals("haunt") || fbUD.equals("haunt")) {
					if (!gWorld.getDummy().getType().equals("Ghost")) {
						gWorld.getDummy().haunt(true);
					}
				}
			} else {

				if (faUD.equals("pre disarm trap")
						|| fbUD.equals("pre disarm trap")) {
					AssetLoader.disarmTrapSound.play(AssetLoader.VOLUME);
					if (faUD.equals("trap") || fbUD.equals("trap")) {
						((DisarmTrap) gWorld.getPlayer().getItem()).foundTrap();
					}
				}

				if (faUD.equals("post disarm trap")
						|| fbUD.equals("post disarm trap")) {
					AssetLoader.trapDisarmedSound.play(AssetLoader.VOLUME);
					if (faUD.equals("trap")) {
						trapToRemove.offer(fa.getBody());
					} else if (fbUD.equals("trap")) {
						trapToRemove.offer(fb.getBody());
					}
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
		fa = c.getFixtureA();
		fb = c.getFixtureB();
		faUD = fa.getUserData();
		fbUD = fb.getUserData();

		if (faUD != null && fbUD != null) {
			if (faUD.equals("player") || fbUD.equals("player")) {
				if (faUD.equals("saferegion") || fbUD.equals("saferegion")) {
					gWorld.setInSafeArea(false);
				}
			}
		}
	}

	public ConcurrentLinkedQueue<Body> getItemsToRemove() {
		return itemsToRemove;
	}

	public ConcurrentLinkedQueue<Body> getWeaponsToRemove() {
		return weaponsToRemove;
	}

	public ConcurrentLinkedQueue<Body> getWeaponPartsToRemove() {
		return weaponPartsToRemove;
	}

	public ConcurrentLinkedQueue<Body> getTrapToRemove() {
		return trapToRemove;
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
