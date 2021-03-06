package com.jkjk.Host;

import java.util.ArrayList;

import com.jkjk.Host.Helpers.ItemSpawner;
import com.jkjk.Host.Helpers.Location;
import com.jkjk.Host.Helpers.SpawnBuffer;
import com.jkjk.Host.Helpers.WeaponPartSpawner;
import com.jkjk.Host.Helpers.WeaponSpawner;

public class ObjectLocations implements Subject {
	private final int SERVER_ID = -1;
	private final MMServer server;
	private final int numOfPlayers;
	private ArrayList<Observer> clients = new ArrayList<Observer>();

	private final SpawnBuffer itemLocations;
	private final SpawnBuffer weaponLocations;
	private final SpawnBuffer weaponPartLocations;
	private final SpawnBuffer trapLocations;

	private final ItemSpawner itemSpawner;
	private final WeaponSpawner weaponSpawner;
	private final WeaponPartSpawner weaponPartSpawner;

	private String message;

	public ObjectLocations(int numOfPlayers, MMServer server) {
		this.numOfPlayers = numOfPlayers;
		this.server = server;

		this.itemLocations = new SpawnBuffer(numOfPlayers * 3);
		this.weaponLocations = new SpawnBuffer((int) (numOfPlayers * 1.5));
		this.weaponPartLocations = new SpawnBuffer(numOfPlayers * 2);
		this.trapLocations = new SpawnBuffer(numOfPlayers * 2);

		itemSpawner = new ItemSpawner();
		weaponSpawner = new WeaponSpawner();
		weaponPartSpawner = new WeaponPartSpawner();

		spawnItems(numOfPlayers * 2);
		spawnWeapons(numOfPlayers);
		spawnWeaponParts(numOfPlayers);
	}

	// Visible consume methods for objects
	public void consumeItem(Location location, int origin) throws InterruptedException {
		System.out.println("ObjectLocations: Item Consumed");
		if (weaponLocations.getBuffer().containsKey(location.get()[0] * location.get()[1])) {
			itemLocations.consume(location);
			itemSpawner.restore(location);
		}
		message = "item_" + origin + "_con_" + Float.toString(location.get()[0]) + "_"
				+ Float.toString(location.get()[1]);
		updateAll(origin);
	}

	public void consumeWeapon(Location location, int origin) {
		System.out.println("ObjectLocations: Weapon Consumed");
		if (weaponLocations.getBuffer().containsKey(location.get()[0] * location.get()[1])) {
			weaponLocations.consume(location);
			weaponSpawner.restore(location);
		}
		message = "weapon_" + origin + "_con_" + Float.toString(location.get()[0]) + "_"
				+ Float.toString(location.get()[1]);
		updateAll(origin);
	}

	public void consumeWeaponPart(Location location, int origin) {
		System.out.println("ObjectLocations: WP Consumed");
		weaponPartLocations.consume(location);
		if (origin != server.getMurdererId()) {
			System.out.println("ObjectLocations: weapon part capacity successfully lowered by 1");
			weaponPartLocations.setCapacity(weaponPartLocations.getCapacity() - 1);
		}
		weaponPartSpawner.restore(location);
		message = "weaponpart_" + origin + "_con_" + Float.toString(location.get()[0]) + "_"
				+ Float.toString(location.get()[1]);
		updateAll(origin);
	}

	public void consumeTrap(Location location, int origin) throws InterruptedException {
		trapLocations.consume(location);
		message = "trap_" + origin + "_con_" + Float.toString(location.get()[0]) + "_"
				+ Float.toString(location.get()[1]);
		updateAll(origin);
	}

	// Visible produce methods for each object
	public void spawnItems(int numOfItems) {
		for (int i = 0; i < numOfItems; i++) {
			produceItem(itemSpawner.spawn());
		}
	}

	public void spawnWeapons(int numOfItems) {
		for (int i = 0; i < numOfItems; i++) {
			produceWeapon(weaponSpawner.spawn());
		}
	}

	public void spawnWeaponParts(int numOfItems) {
		for (int i = 0; i < numOfItems; i++) {
			produceWeaponPart(weaponPartSpawner.spawn());
		}
	}

	// Getters for direct access to item buffers
	public SpawnBuffer getItemLocations() {
		return itemLocations;
	}

	public SpawnBuffer getWeaponLocations() {
		return weaponLocations;
	}

	public SpawnBuffer getWeaponPartLocations() {
		return weaponPartLocations;
	}

	public SpawnBuffer getTrapLocations() {
		return trapLocations;
	}

	// Private produce methods
	private void produceItem(Location location) {
		itemLocations.produce(location);
		message = "item_" + SERVER_ID + "_pro_" + Float.toString(location.get()[0]) + "_"
				+ Float.toString(location.get()[1]);
		updateAll(SERVER_ID);
	}

	// Public produce method to be used for GHOST
	public void produceItemGhost(Location location, int origin) {
		message = "item_" + SERVER_ID + "_pro_" + Float.toString(location.get()[0]) + "_"
				+ Float.toString(location.get()[1]);
		updateAll(origin);
	}

	private void produceWeapon(Location location) {
		weaponLocations.produce(location);
		message = "weapon_" + SERVER_ID + "_pro_" + Float.toString(location.get()[0]) + "_"
				+ Float.toString(location.get()[1]);
		updateAll(SERVER_ID);
	}

	// Public produce method to be used for GHOST
	public void produceWeaponGhost(Location location, int origin) {
		message = "weapon_" + SERVER_ID + "_pro_" + Float.toString(location.get()[0]) + "_"
				+ Float.toString(location.get()[1]);
		updateAll(origin);
	}

	private void produceWeaponPart(Location location) {
		weaponPartLocations.produce(location);
		message = "weaponpart_" + SERVER_ID + "_pro_" + Float.toString(location.get()[0]) + "_"
				+ Float.toString(location.get()[1]);
		updateAll(SERVER_ID);
	}

	public void produceTrap(Location location, int origin) throws InterruptedException {
		trapLocations.produce(location);
		message = "trap_" + origin + "_pro_" + Float.toString(location.get()[0]) + "_"
				+ Float.toString(location.get()[1]);
		updateAll(origin);
	}

	@Override
	public void register(Observer obs) {
		this.clients.add(obs);
	}

	@Override
	public void unregister(Observer obs) {
		this.clients.remove(obs);
	}

	@Override
	public void updateAll(int origin) {
		for (int i = 0; i < clients.size(); i++) {
			if (i == origin) {
				continue;
			}
			clients.get(i).update(message);
		}
	}
}
