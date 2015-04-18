package com.jkjk.Host;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class GameStatus implements Subject {
	private ArrayList<Observer> clients;
	private int gameStatus;
	private int WAITING = 0;
	private int PLAYING = 1;
	private int GAMEOVER = 2;

	private String message;

	public GameStatus() {
		this.clients = new ArrayList<Observer>();
		gameStatus = WAITING;
		message = "";
	}

	/**
	 * Signals all clients to begin the game
	 */
	public void begin() {
		message = "startgame";
		gameStatus = PLAYING;
		updateAll(-1);
	}

	/**
	 * Sends message when someone wins the game
	 * 
	 * @param winnerType
	 *            0 for murderer win, 1 for civilian win
	 */
	public void win(int winnerType) {
		if (winnerType == 0) {
			message = "win_murderer";
		} else if (winnerType == 1) {
			message = "win_civilian";
		}
		gameStatus = GAMEOVER;
		updateAll(-1);
	}

	/**
	 * Returns game status: 0 for waiting/ready. 1 for playing. 2 for game over.
	 * 
	 * @return Status of game.
	 */
	public int getGameStatus() {
		return gameStatus;
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
