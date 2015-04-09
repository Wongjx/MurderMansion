package com.jkjk.Host;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class GameStatus implements Subject {
	private ArrayList<Observer> clients;

	private String message;

	public GameStatus() {
		this.clients = new ArrayList<Observer>();
		message = "";
	}

	/**Sends message when someone wins the game
	 * @param winnerType
	 *            0 for murderer win, 1 for civilian win
	 */
	public void win(int winnerType) {
		if (winnerType == 0) {
			message = "win_murderer";
		} else if (winnerType == 1){
			message = "win_civilian";
		}
		updateAll(-1);
	}

	@Override
	public void updateAll(int origin) {
		for (int i = 0; i < clients.size(); i++) {
			if (i==origin){
				continue;
			}
			clients.get(i).update(message);
		}
	}

	@Override
	public void register(Observer obs) {
		this.clients.add(obs);
	}

	@Override
	public void unregister(Observer obs) {
		this.clients.remove(obs);
	}

	public ArrayList<Observer> getClients() {
		return clients;
	}
}
