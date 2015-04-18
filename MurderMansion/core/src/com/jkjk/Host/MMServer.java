package com.jkjk.Host;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.badlogic.gdx.Gdx;
import com.jkjk.GameObjects.Duration;
import com.jkjk.Host.Helpers.Location;
import com.jkjk.Host.Helpers.ObstaclesHandler;
import com.jkjk.MMHelpers.MultiplayerSessionInfo;

public class MMServer {

	private MultiplayerSessionInfo info;
	public ServerSocket serverSocket;
	private String serverAddress;
	private int serverPort;
	private final int SERVER_ID = -1;

	private ConcurrentHashMap<String, Socket> clients;
	private ConcurrentHashMap<String, PrintWriter> serverOutput;
	private ConcurrentHashMap<String, BufferedReader> serverInput;
	private ConcurrentHashMap<String, Thread> serverListeners;
	private final ConcurrentHashMap<String, Observer> observers;
	public final ConcurrentHashMap<String, String> clientNames;

	private int numOfPlayers;
	private final int murdererId;
	private AtomicInteger readyCount;
	private Duration gameStartPause;

	private long startTime;
	private long runTime;
	private long nextItemSpawnTime;
	private long nextObstacleRemoveTime;
	private long nextLightningTime;

	private final PlayerStatuses playerStats;
	private final ObjectLocations objectLocations;
	private final ObstaclesHandler obstaclesHandler;
	private float[] obstacleDestroyed; // To transmit position of obstacle destroyed to clients

	private int weaponPartsCollected;
	private GameStatus gameStatus;
	private int numInSafeRegion;
	private int numStillAlive;
	private boolean win;
	private Random random;

	public MMServer(int numOfPlayers, MultiplayerSessionInfo info) throws InterruptedException {
		this.numOfPlayers = numOfPlayers;
		this.info = info;
		int gameStartPauseDuration = 2000;

		// System.out.println("Initialize Client list and listeners");
		clients = new ConcurrentHashMap<String, Socket>();
		serverOutput = new ConcurrentHashMap<String, PrintWriter>();
		serverInput = new ConcurrentHashMap<String, BufferedReader>();
		serverListeners = new ConcurrentHashMap<String, Thread>();
		observers = new ConcurrentHashMap<String, Observer>();
		clientNames = new ConcurrentHashMap<String, String>();

		// System.out.println("Initialize fields");
		playerStats = new PlayerStatuses(numOfPlayers);
		objectLocations = new ObjectLocations(numOfPlayers, this);

		obstaclesHandler = new ObstaclesHandler();
		nextItemSpawnTime = 10000;
		nextObstacleRemoveTime = 30000;
		nextLightningTime = 20000;

		gameStartPause = new Duration(gameStartPauseDuration);
		gameStatus = new GameStatus();
		random = new Random();

		// System.out.println("Assigning murderer");
		murdererId = random.nextInt(numOfPlayers);
		// Set number of players who have loaded and ready to play=0
		readyCount = new AtomicInteger(0);
		initPlayers();

		// Attempt to connect to clients (numOfPlayers)
		System.out.println("Creating server socket");
		initServerSocket(info);
		acceptServerConnections();
	}

	// public static MMServer getInstance(int numOfPlayers, MultiplayerSessionInfo info)
	// throws InterruptedException {
	// if (instance == null) {
	// instance = new MMServer(numOfPlayers, info);
	// System.out.println("new instance of MMServer made");
	// }
	// return instance;
	// }

	/**
	 * Start updating only when all clients have successfully synchronized.
	 */
	public void update() {
		if (gameStatus.getGameStatus() == 1) {
			runTime = System.currentTimeMillis() - startTime;
			handleSpawn();
			checkWin();
			lightningStrike();
		}
		if (gameStartPause.isCountingDown() && gameStatus.getGameStatus() == 0) {
			gameStartPause.update();
			if (!gameStartPause.isCountingDown()) {
				startTime = System.currentTimeMillis();
				gameStatus.begin();
			}
		}
	}

	private void handleSpawn() {
		// Item/Weapon/WeaponPart Spawn
		if (runTime > nextItemSpawnTime) {
			System.out.println("SPAWN!");
			if (!objectLocations.getItemLocations().isFull()) {
				objectLocations.spawnItems(1);
				System.out.println("Item spawned with size: "
						+ objectLocations.getItemLocations().getBuffer().size());
			}
			if (!objectLocations.getWeaponLocations().isFull()) {
				objectLocations.spawnWeapons(1);
				System.out.println("Weapon spawned with size: "
						+ objectLocations.getWeaponLocations().getBuffer().size());
			}
			if (!objectLocations.getWeaponPartLocations().isFull()) {
				objectLocations.spawnWeaponParts(1);
				System.out.println("Weapon Part spawned with size: "
						+ objectLocations.getWeaponPartLocations().getBuffer().size());
			}
			nextItemSpawnTime += (random.nextInt(15000) + 10000);
		}

		// Opens random door in mansion
		if (runTime > nextObstacleRemoveTime && obstaclesHandler.getObstacles().size() > 0) {
			System.out.println("OBSTACLE DESTROYED!");
			obstacleDestroyed = obstaclesHandler.destroyObstacle().get();
			System.out.println("At x:" + obstacleDestroyed[0] + " y: " + obstacleDestroyed[1]);
			sendToClients("obstacle_" + Float.toString(obstacleDestroyed[0]) + "_"
					+ Float.toString(obstacleDestroyed[1]));
			nextObstacleRemoveTime += 30000;
		}
	}

	private void checkWin() {
		// Civilian Win condition when murderer is dead
		if (!win) {
			if (playerStats.getPlayerIsAliveValue("Player " + murdererId) == 0) {
				gameStatus.win(1);
				win = true;
			}

			// Win condition when
			// (Civilian) 1) alive civilians are all in safe region or
			// (Murderer) 2) all civilians are dead
			numStillAlive = 0;
			numInSafeRegion = 0;
			for (int i = 0; i < numOfPlayers; i++) {
				if (i == murdererId)
					continue;
				if (playerStats.getPlayerIsAliveValue("Player " + i) == 1) {
					numStillAlive++;
					if (playerStats.getPlayerIsInSafeRegion("Player " + i) == 1) {
						numInSafeRegion++;
						System.out.println("ENTERED SAFE REGION!");
					}
				}
			}
			if (numStillAlive > 0 && numStillAlive == numInSafeRegion) {
				System.out.println("WHATTTT");
				gameStatus.win(1);
				win = true;
			} else if (numStillAlive == 0) {
				gameStatus.win(0);
				win = true;
			}
		}
	}

	private void lightningStrike() {
		if (runTime > nextLightningTime) {
			sendToClients("lightning");
			nextLightningTime += (random.nextInt(15000) + 20000);
		}
	}

	private void initPlayers() {
		for (int i = 0; i < numOfPlayers; i++) {
			playerStats.getPlayerIsAlive().put("Player " + i, 1);
			playerStats.getPlayerIsStun().put("Player " + i, 0);
			playerStats.getPlayerIsInSafeRegion().put("Player " + i, 0);
			if (i == murdererId) {
				playerStats.getPlayerType().put("Player " + i, 0);
			} else {
				playerStats.getPlayerType().put("Player " + i, 1);
			}

			playerStats.getPlayerPosition().put("Player " + i, new float[] { 880, 580 - ((i + 1) * 30) });
			playerStats.getPlayerAngle().put("Player " + i, 3.1427f);

		}
	}

	public int getNumOfPlayers() {
		return numOfPlayers;
	}

	public int getMurdererId() {
		return murdererId;
	}

	public GameStatus getGameStatus() {
		return gameStatus;
	}

	public PlayerStatuses getPlayerStats() {
		return playerStats;
	}

	public ObjectLocations getObjectLocations() {
		return objectLocations;
	}

	public ObstaclesHandler getObstaclesHandler() {
		return obstaclesHandler;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String socketAddress) {
		System.out.println("Setting server adddress " + socketAddress);
		this.serverAddress = socketAddress;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int socketPort) {
		this.serverPort = socketPort;
	}

	public ConcurrentHashMap<String, Socket> getClients() {
		synchronized (clients) {
			return clients;
		}
	}

	public void setClients(ConcurrentHashMap<String, Socket> clients) {
		synchronized (clients) {
			this.clients = clients;
		}
	}

	public ConcurrentHashMap<String, PrintWriter> getServerOutput() {
		synchronized (serverOutput) {
			return serverOutput;
		}
	}

	public ConcurrentHashMap<String, Observer> getObservers() {
		synchronized (observers) {
			return observers;
		}
	}

	public void setServerOutput(ConcurrentHashMap<String, PrintWriter> serverOutput) {
		synchronized (serverOutput) {
			this.serverOutput = serverOutput;
		}
	}

	public ConcurrentHashMap<String, BufferedReader> getServerInput() {
		synchronized (serverInput) {
			return serverInput;
		}
	}

	public void setServerListeners(ConcurrentHashMap<String, Thread> serverListeners) {
		synchronized (serverListeners) {
			this.serverListeners = serverListeners;
		}
	}

	public ConcurrentHashMap<String, Thread> getServerListeners() {
		synchronized (serverListeners) {
			return serverListeners;
		}
	}

	public void setServerInput(ConcurrentHashMap<String, BufferedReader> serverInput) {
		synchronized (serverInput) {
			this.serverInput = serverInput;
		}
	}

	// Initialize server socket
	public void initServerSocket(MultiplayerSessionInfo info) {
		try {
			// Randomly assign server to an open port
			ServerSocket sock = new ServerSocket(0);
			this.serverSocket = sock;
			info.setServer(this);

			// Get ip address and port number
			setServerAddress(getLocalIpAddress());
			setServerPort(sock.getLocalPort());
			// Set info into multiplayerseissoninfo for local client to read
			info.serverAddress = getServerAddress();
			info.serverPort = getServerPort();

			System.out.println("Server Socket created. Port: " + serverPort + " address: " + serverAddress);

		} catch (Exception e) {
			System.out.println("Error creating server socket: " + e.getMessage());
		}
	}

	// Start accepting client connections
	public void acceptServerConnections() {
		if (serverSocket != null) {
			System.out.println("Server accepting client connections");
			Thread thread = new serverAcceptThread(this);
			thread.start();
		} else {
			System.out.println("Server not instantiated yet.");
		}
	}

	/**
	 * Send a string message out to all connected clients
	 * 
	 * @param Message
	 *            Message to send out
	 */
	public void sendToClients(String Message) {
		for (PrintWriter write : this.serverOutput.values()) {
			write.println(Message);
			write.flush();
		}
	}

	/**
	 * Get local ip address in IPV4 format
	 * 
	 * @return IPV4 of device in string
	 */
	private String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
					.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
						.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
						return inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Called to handle message sent by clients
	 * 
	 * @param message
	 * @throws InterruptedException
	 * @throws NumberFormatException
	 */
	public void handleMessage(String message) throws NumberFormatException, InterruptedException {
		String[] msg = message.split("_");
		// If client ready message
		if (msg[0].equals("ready")) {
			readyCount.getAndIncrement();
			if (readyCount.get() >= numOfPlayers) {
				gameStartPause.startCountdown();
			}
		}

		// If player position update message
		else if (msg[0].equals("loc")) {
			float[] position = { Float.parseFloat(msg[2]), Float.parseFloat(msg[3]) };
			float angle = Float.parseFloat(msg[4]);
			float velocity = Float.parseFloat(msg[5]);
			playerStats.updatePositionAndAngle(Integer.parseInt(msg[1]), position, angle, velocity);
		} else if (msg[0].equals("pos")) {
			float[] position = { Float.parseFloat(msg[2]), Float.parseFloat(msg[3]) };
			playerStats.updatePosition(Integer.parseInt(msg[1]), position);
		} else if (msg[0].equals("ang")) {
			float angle = Float.parseFloat(msg[2]);
			playerStats.updateAngle(Integer.parseInt(msg[1]), angle);
		} else if (msg[0].equals("vel")) {
			float velocity = Float.parseFloat(msg[2]);
			playerStats.updateVelocity(Integer.parseInt(msg[1]), velocity);
		} else if (msg[0].equals("type")) {
			playerStats.updateType(Integer.parseInt(msg[1]), Integer.parseInt(msg[2]),
					Integer.parseInt(msg[3]));
		} else if (msg[0].equals("alive")) {
			playerStats.updateIsAlive(Integer.parseInt(msg[1]), Integer.parseInt(msg[2]),
					Integer.parseInt(msg[3]));
		} else if (msg[0].equals("stun")) {
			playerStats.updateIsStun(Integer.parseInt(msg[1]), Integer.parseInt(msg[2]),
					Integer.parseInt(msg[3]));
		} else if (msg[0].equals("safe")) {
			System.out.println("Player " + msg[1] + " in safe region?: " + msg[2]);
			playerStats.updateIsInSafeRegion(Integer.parseInt(msg[1]), Integer.parseInt(msg[2]));
		} else if (msg[0].equals("useItem")) {
			System.out.println("Player " + msg[1] + " using item.");
			playerStats.updateUseItem(Integer.parseInt(msg[1]));
		} else if (msg[0].equals("useWeapon")) {
			System.out.println("Player " + msg[1] + " using weapon.");
			playerStats.updateUseWeapon(Integer.parseInt(msg[1]));
		} else if (msg[0].equals("useAbility")) {
			System.out.println("Player " + msg[1] + " using ability.");
			playerStats.updateUseAbility(Integer.parseInt(msg[1]));
		} else if (msg[0].equals("addWeaponPart")) {
			System.out.println("Add weapon part");
			weaponPartsCollected++;
			playerStats.updateWeaponPartsCollected();
			if (weaponPartsCollected == numOfPlayers * 2) {
				playerStats.updateShotgunCreated();
			}
		}

		// If item consumption or production message
		else if (msg[0].equals("item")) {
			if (msg[2].equals("con")) {
				System.out.println("Server: Consume item");
				objectLocations.consumeItem(
						new Location(new float[] { Float.parseFloat(msg[3]), Float.parseFloat(msg[4]) }),
						Integer.parseInt(msg[1]));
			} else if (msg[2].equals("pro")) {
				objectLocations.produceItemGhost(
						new Location(new float[] { Float.parseFloat(msg[3]), Float.parseFloat(msg[4]) }),
						Integer.parseInt(msg[1]));
			}
		} else if (msg[0].equals("weapon")) {
			if (msg[2].equals("con")) {
				System.out.println("Server: Consume weapon");
				objectLocations.consumeWeapon(
						new Location(new float[] { Float.parseFloat(msg[3]), Float.parseFloat(msg[4]) }),
						Integer.parseInt(msg[1]));
			} else if (msg[2].equals("pro")) {
				objectLocations.produceWeaponGhost(
						new Location(new float[] { Float.parseFloat(msg[3]), Float.parseFloat(msg[4]) }),
						Integer.parseInt(msg[1]));
			}
		} else if (msg[0].equals("weaponpart")) {
			if (msg[2].equals("con")) {
				System.out.println("Server: Consume WP");
				objectLocations.consumeWeaponPart(
						new Location(new float[] { Float.parseFloat(msg[3]), Float.parseFloat(msg[4]) }),
						Integer.parseInt(msg[1]));
			}
		} else if (msg[0].equals("trap")) {
			if (msg[2].equals("con")) {
				objectLocations.consumeTrap(
						new Location(new float[] { Float.parseFloat(msg[3]), Float.parseFloat(msg[4]) }),
						Integer.parseInt(msg[1]));
			} else if (msg[2].equals("pro")) {
				objectLocations.produceTrap(
						new Location(new float[] { Float.parseFloat(msg[3]), Float.parseFloat(msg[4]) }),
						Integer.parseInt(msg[1]));
			}
		}
	}

	public void endSession() throws IOException {
		// instance= null;
		for (Thread t : serverListeners.values()) {
			t.interrupt();
		}
		for (Socket s : clients.values()) {
			s.getOutputStream().flush();
			s.close();
		}
		System.out.println("MMServer seisson ended.");
	}

	public void removePlayer(int playerId) {
		Observer toRemove = observers.get("Player " + playerId);
		unregisterFromSubjects(toRemove); // PlayerStats, Object locations
		killPlayer(playerId);
		removeFromPlayerLists(playerId); // clients, serverOuput, serverInput, serverListeners, observers
		decreaseNumOfPlayers();
	}

	private void unregisterFromSubjects(Observer obs) {
		this.playerStats.unregister(obs);
		this.objectLocations.unregister(obs);
	}

	private void removeFromPlayerLists(int playerId) {
		this.clients.remove("Player " + playerId);
		this.observers.remove("Player " + playerId);
		this.serverOutput.remove("Player " + playerId);
		this.serverInput.remove("Player " + playerId);
		this.serverListeners.remove("Player " + playerId);
	}

	private void decreaseNumOfPlayers() {
		this.numOfPlayers--;
	}

	private void killPlayer(int playerId) {
		this.playerStats.updateIsAlive(SERVER_ID, playerId, 0);
	}

}

/**
 * Sub Thread to for server to accept connections, stops when all players in gps room connects successfully
 * Precondition: MultiPlayerSeissonInfo.server != null;
 * 
 * @author Wong
 * 
 */
class serverAcceptThread extends Thread {
	private MMServer server;
	private String TAG = "ServerAcceptThread";

	public serverAcceptThread(MMServer server) {
		this.server = server;
	}

	@Override
	public void run() {
		int idCount = 0;
		while (server.getClients().size() < server.getNumOfPlayers()) {
			try {
				Socket socket = server.serverSocket.accept();

				// Set socket timeout as 30 seconds
				socket.setSoTimeout(90000);

				// Add in client socket
				server.getClients().put("Player " + idCount, socket);

				// Add input stream
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				server.getServerInput().put("Player " + idCount, reader);
				// Add output stream
				PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
				server.getServerOutput().put("Player " + idCount, writer);

				// Register client to playerStatus subject
				Observer player = new Observer(writer);
				server.getPlayerStats().register(player);
				server.getObjectLocations().register(player);
				server.getGameStatus().register(player);
				server.getObservers().put("Player " + idCount, player);

				// Get google play participant id of client
				String clientName = reader.readLine();
				server.clientNames.put("Player " + idCount, clientName);

				// Send out initializing information to client
				writer.println(server.getNumOfPlayers());
				writer.println(idCount);
				writer.println(server.getMurdererId());

				String message = "";
				float[] position = null;
				Collection<Location> locations = null;

				// Send item spawn locations
				writer.println("itemLocations");
				locations = server.getObjectLocations().getItemLocations().getBuffer().values();
				message = "";
				for (Location location : locations) {
					position = location.get();
					for (float coordinate : position) {
						message += String.valueOf(coordinate) + ",";
					}
					message = message.substring(0, message.length() - 1);
					message += "_";
				}
				message = message.substring(0, message.length() - 1);
				System.out.println(message);
				writer.println(message);
				writer.println("end");
				// Send weapon spawn locations
				writer.println("weaponLocations");
				locations = server.getObjectLocations().getWeaponLocations().getBuffer().values();
				message = "";
				for (Location location : locations) {
					position = location.get();
					for (float coordinate : position) {
						message += String.valueOf(coordinate) + ",";
					}
					message = message.substring(0, message.length() - 1);
					message += "_";
				}
				message = message.substring(0, message.length() - 1);
				System.out.println(message);
				writer.println(message);
				writer.println("end");
				// Send weapon parts spawn locations
				writer.println("weaponPartLocations");
				locations = server.getObjectLocations().getWeaponPartLocations().getBuffer().values();
				message = "";
				for (Location location : locations) {
					position = location.get();
					for (float coordinate : position) {
						message += String.valueOf(coordinate) + ",";
					}
					message = message.substring(0, message.length() - 1);
					message += "_";
				}
				message = message.substring(0, message.length() - 1);
				System.out.println(message);
				writer.println(message);
				writer.println("end");

				// Send spawn locations and angle
				writer.println("spawnPositions");
				message = "";
				for (int i = 0; i < server.getNumOfPlayers(); i++) {
					// Get float[] of position x and y from concurrent hashmap
					position = server.getPlayerStats().getPlayerPositionValue("Player " + i);
					for (float coordinate : position) {
						message += String.valueOf(coordinate) + ",";
					}
					message = message.substring(0, message.length() - 1);
					message += "_";
				}
				message = message.substring(0, message.length() - 1);
				System.out.println(message);
				writer.println(message);
				writer.println("end");

				// Send spawn angles
				writer.println("spawnAngles");
				message = "";
				for (int i = 0; i < server.getNumOfPlayers(); i++) {
					// Get float[] of position x and y from concurrent hashmap
					float angle = server.getPlayerStats().getPlayerAngleValue("Player " + i);
					message += String.valueOf(angle) + ",";
				}
				message = message.substring(0, message.length() - 1);
				System.out.println(message);
				writer.println(message);
				writer.println("end");

				// Start a listener thread for each client socket connected
				Thread thread = new serverListener(reader, server, idCount);
				server.getServerListeners().put("Player " + idCount, thread);
				thread.start();

				// Increase id count
				idCount++;

			} catch (Exception e) {
				Gdx.app.log(TAG, "Error creating server socket: " + e.getMessage());
			}
		}

		// Send collated list of participant ids to all clients
		String ret = "";
		for (int i = 0; i < server.getNumOfPlayers(); i++) {
			ret += server.clientNames.get("Player " + i) + "_";
		}
		ret = ret.substring(0, ret.length() - 1);
		server.sendToClients("clientNames");
		server.sendToClients(ret);
		server.sendToClients("end");

		System.out.println("End of client intialization");

	}
}

class serverListener extends Thread {
	private MMServer server;
	private BufferedReader input;
	private final int playerId;
	private String msg;

	public serverListener(BufferedReader inputStream, MMServer server, int playerId) {
		this.server = server;
		this.input = inputStream;
		this.playerId = playerId;
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			try {
				if ((msg = input.readLine()) != null) {
					// System.out.println("MMServer Message received: "+msg);
					// Do something with message
					server.handleMessage(msg);
				}
			} catch (SocketException e) {
				System.out.println("Server socket error while reading: " + e.getMessage());
				e.printStackTrace();
				break;
			} catch (Exception e) {
				System.out.println("Server error while reading: " + e.getMessage());
				e.printStackTrace();
			}
		}

		System.out.println("Connection with client has been terminated.");
		server.removePlayer(playerId);

	}
}
