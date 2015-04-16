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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Random;
import com.badlogic.gdx.Gdx;
import com.jkjk.Host.Helpers.Location;
import com.jkjk.Host.Helpers.ObstaclesHandler;
import com.jkjk.MMHelpers.MultiplayerSessionInfo;

public class MMServer {
	private static MMServer instance;

	private MultiplayerSessionInfo info;
	public ServerSocket serverSocket;
	private String serverAddress;
	private int serverPort;

	private ArrayList<Socket> clients;
	private ArrayList<PrintWriter> serverOutput;
	private ArrayList<BufferedReader> serverInput;
	private ArrayList<Thread> serverListeners;

	private final int numOfPlayers;
	private final int murdererId;
	private int readyCount;

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

	private MMServer(int numOfPlayers, MultiplayerSessionInfo info) throws InterruptedException {
		this.numOfPlayers = numOfPlayers;
		this.info = info;

		// System.out.println("Initialize Client list and listeners");
		clients = new ArrayList<Socket>();
		serverOutput = new ArrayList<PrintWriter>();
		serverInput = new ArrayList<BufferedReader>();
		serverListeners=new ArrayList<Thread>();

		// System.out.println("Initialize fields");
		startTime = System.currentTimeMillis();
		playerStats = new PlayerStatuses(numOfPlayers);
		objectLocations = new ObjectLocations(numOfPlayers, this);

		obstaclesHandler = ObstaclesHandler.getInstance();
		nextItemSpawnTime = 10000;
		nextObstacleRemoveTime = 30000;
		nextLightningTime = 30000;

		gameStatus = new GameStatus();
		random = new Random();

		// System.out.println("Assigning murderer");
		murdererId = random.nextInt(numOfPlayers);
		//Set number of players who have loaded and ready to play=0
		readyCount=0;
		initPlayers();

		// Attempt to connect to clients (numOfPlayers)
		System.out.println("Creating server socket");
		initServerSocket(info);
		acceptServerConnections();
	}

	public static MMServer getInstance(int numOfPlayers, MultiplayerSessionInfo info)
			throws InterruptedException {
		if (instance == null) {
			instance = new MMServer(numOfPlayers, info);
			System.out.println("new instance of MMServer made");
		}
		return instance;
	}

	/**
	 * Start updating only when all clients have successfully synchronized.
	 */
	public void update() {
		runTime = System.currentTimeMillis() - startTime;
		handleSpawn();
		checkWin();
		lightningStrike();
	}

	private void handleSpawn() {
		// Item/Weapon/WeaponPart Spawn
		if (runTime > nextItemSpawnTime) {
			System.out.println("SPAWN!");
			if (!objectLocations.getItemLocations().isFull())
				objectLocations.spawnItems(1);
			if (!objectLocations.getWeaponLocations().isFull())
				objectLocations.spawnWeapons(1);
			if (!objectLocations.getWeaponPartLocations().isFull())
				objectLocations.spawnWeaponParts(1);
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
					if (playerStats.getPlayerIsInSafeRegion("Player " + 1) == 1) {
						numInSafeRegion++;
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

	public ArrayList<Socket> getClients() {
		synchronized (clients) {
			return new ArrayList<Socket>(clients);
		}
	}

	public void setClients(ArrayList<Socket> clients) {
		synchronized (clients) {
			this.clients = new ArrayList<Socket>(clients);
		}
	}

	public ArrayList<PrintWriter> getServerOutput() {
		synchronized (serverOutput) {
			return new ArrayList<PrintWriter>(serverOutput);
		}
	}

	public void setServerOutput(ArrayList<PrintWriter> serverOutput) {
		synchronized (serverOutput) {
			this.serverOutput = new ArrayList<PrintWriter>(serverOutput);
		}
	}

	public ArrayList<BufferedReader> getServerInput() {
		synchronized (serverInput) {
			ArrayList<BufferedReader> ret = new ArrayList<BufferedReader>(serverInput);
			return ret;
		}
	}

	public void setServerListeners(ArrayList<Thread> serverListeners) {
		synchronized (serverListeners) {
			this.serverListeners = new ArrayList<Thread>(serverListeners);
		}
	}
	
	public ArrayList<Thread> getServerListeners() {
		synchronized (serverListeners) {
			ArrayList<Thread> ret = new ArrayList<Thread>(serverListeners);
			return ret;
		}
	}

	public void setServerInput(ArrayList<BufferedReader> serverInput) {
		synchronized (serverInput) {
			this.serverInput = new ArrayList<BufferedReader>(serverInput);
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
		for (PrintWriter write : this.serverOutput) {
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
		//If client ready message
		if(msg[0].equals("ready")){
			readyCount++;
			if(readyCount>=numOfPlayers){
				sendToClients("startgame");
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
			System.out.println("Player " + msg[1] + " is safe.");
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
	
	public void endSession() throws IOException{
		MMServer.instance= null;
		for(Thread t:serverListeners){
			t.interrupt();
		}
		for(Socket s: clients){
			s.getOutputStream().flush();
			s.close();
		}
		System.out.println("MMServer seisson ended.");
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
				// Add in client socket
				ArrayList<Socket> temp = server.getClients();
				temp.add(socket);
				server.setClients(temp);
				// Add input stream
				ArrayList<BufferedReader> tempInput = server.getServerInput();
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				tempInput.add(reader);
				server.setServerInput(tempInput);
				// Add output stream
				ArrayList<PrintWriter> tempOutput = server.getServerOutput();
				PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
				tempOutput.add(writer);
				server.setServerOutput(tempOutput);

				// Register client to playerStatus subject
				server.getPlayerStats().register(new Observer(writer));
				server.getObjectLocations().register(new Observer(writer));
				server.getGameStatus().register(new Observer(writer));

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

				// Increase id count
				idCount++;

			} catch (Exception e) {
				Gdx.app.log(TAG, "Error creating server socket: " + e.getMessage());
			}
		}
		// Start a listener thread for each client socket connected
		for (BufferedReader read : server.getServerInput()) {
			Thread thread = new serverListener(read, server);
			server.getServerListeners().add(thread);
			thread.start();
		}
	}
}

class serverListener extends Thread {
	private MMServer server;
	private BufferedReader input;
	private String msg;

	public serverListener(BufferedReader inputStream, MMServer server) {
		this.server = server;
		this.input = inputStream;
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			try {
				if ((msg = input.readLine()) != null) {
					// System.out.println("MMServer Message received: "+msg);
					// String message = new String(msg);
					// Do something with message
					server.handleMessage(msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error while reading: " + e.getMessage());
			}

		}
	}
}
