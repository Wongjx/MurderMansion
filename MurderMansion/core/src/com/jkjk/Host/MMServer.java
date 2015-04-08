package com.jkjk.Host;

import java.io.BufferedReader;
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
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.Gdx;
import com.jkjk.MMHelpers.MultiplayerSeissonInfo;

public class MMServer {
	private static MMServer instance;

	private MultiplayerSeissonInfo info;
	public ServerSocket serverSocket;
	private String serverAddress;
	private int serverPort;

	private ArrayList<Socket> clients;
	private ArrayList<PrintWriter> serverOutput;
	private ArrayList<BufferedReader> serverInput;

	private final int numOfPlayers;
	private final int murdererId;

	private long startTime;
	private long runTime;
	private long nextItemSpawnTime;
	private long nextObstacleRemoveTime;

	private final PlayerStatuses playerStats;


	private final ObjectLocations objectLocations;

	private final ObstaclesHandler obstaclesHandler;
	private float[] obstacleDestroyed; // To transmit position of obstacle destroyed to clients

	private int numInSafeRegion;
	private int numStillAlive;
	private boolean civWin;
	private boolean murWin;

	private MMServer(int numOfPlayers, MultiplayerSeissonInfo info) throws InterruptedException {
		this.numOfPlayers = numOfPlayers;
		this.info = info;

		// System.out.println("Initialize Client list and listeners");
		clients = new ArrayList<Socket>();
		serverOutput = new ArrayList<PrintWriter>();
		serverInput = new ArrayList<BufferedReader>();

		// System.out.println("Initialize fields");
		startTime = System.currentTimeMillis();
		playerStats = new PlayerStatuses(numOfPlayers);
		objectLocations = new ObjectLocations(numOfPlayers,this);


		obstaclesHandler = ObstaclesHandler.getInstance();

		// System.out.println("Assigning murderer");
		murdererId = new Random().nextInt(numOfPlayers);

		nextItemSpawnTime = 10000;
		nextObstacleRemoveTime = 30000;

		initPlayers();

		// Attempt to connect to clients (numOfPlayers)
		System.out.println("Creating server socket");
		initServerSocket(info);
		acceptServerConnections();
	}

	public static MMServer getInstance(int numOfPlayers, MultiplayerSeissonInfo info)
			throws InterruptedException {
		if (instance == null) {
			instance = new MMServer(numOfPlayers, info);
		}
		return instance;
	}

	/**
	 * Start updating only when all clients have successfully synchronized.
	 */
	public void update() {
		runTime = System.currentTimeMillis() - startTime;

		// Item/Weapon/WeaponPart Spawn *NEEDS TO BE BALANCED TO FIT GAMEPLAY
		if (runTime > nextItemSpawnTime) {
			System.out.println("SPAWN!");
			if (!objectLocations.getItemLocations().isFull())
				objectLocations.spawnItems(1);
			if (!objectLocations.getWeaponLocations().isFull())
				objectLocations.spawnWeapons(1);
			if (!objectLocations.getWeaponPartLocations().isFull())
				objectLocations.spawnWeaponParts(1);
			nextItemSpawnTime += (new Random().nextInt(15000) + 10000);
		}

		// Opens random door in mansion *TO BE IMPLEMENTED
		if (runTime > nextObstacleRemoveTime && obstaclesHandler.getObstacles().size() > 0) {
			System.out.println("OBSTACLE DESTROYED!");
			obstacleDestroyed = obstaclesHandler.destroyObstacle().get();
			System.out.println("At x:"+obstacleDestroyed[0]+" y: "+obstacleDestroyed[1]);
			sendToClients("obstacle_"+Float.toString(obstacleDestroyed[0])+"_"+Float.toString(obstacleDestroyed[1]));
			nextObstacleRemoveTime += 30000;
		}

		// Win condition when murderer is dead
		if (playerStats.getPlayerIsAliveValue("Player " + murdererId) == 0)
			civWin = true;

		// Win condition when 1) alive civilians are all in safe region or 2) all civilians are dead
		numStillAlive = 0;
		numInSafeRegion = 0;
		for (int i = 0; i < numOfPlayers; i++) {
			if (i == murdererId)
				continue;
			if (playerStats.getPlayerIsAliveValue("Player " + i) == 1) {
				numStillAlive++;
				if (playerStats.getPlayerIsInSafeRegion("Player " + 1) == 1)
					numInSafeRegion++;
			}
		}
		if (numStillAlive > 0 && numStillAlive == numInSafeRegion)
			civWin = true;
		else if (numStillAlive == 0)
			murWin = true;

	}

	private void initPlayers() {
		for (int i = 0; i < numOfPlayers; i++) {
			playerStats.getPlayerIsAlive().put("Player " + i, 1);
			playerStats.getPlayerIsStun().put("Player " + i, 0);
			playerStats.getPlayerUseItem().put("Player " + i, 0);
			playerStats.getPlayerUseWeapon().put("Player " + i, 0);
			playerStats.getPlayerIsInSafeRegion().put("Player " + i, 0);
			if (i == murdererId) {
				playerStats.getPlayerType().put("Player " + i, 0);
			} else {
				playerStats.getPlayerType().put("Player " + i, 1);
			}

			playerStats.getPlayerPosition().put("Player " + i, new float[] { 850 - ((i + 1) * 40), 515 });
			playerStats.getPlayerAngle().put("Player " + i, 3.1427f);

		}
	}

	public int getNumOfPlayers() {
		return numOfPlayers;
	}

	public int getMurdererId() {
		return murdererId;
	}

	public PlayerStatuses getPlayerStats() {
		return playerStats;
	}

	public ObjectLocations getObjectLocations() {
		return objectLocations;
	}
	
	public ObstaclesHandler getObstaclesHandler(){
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

	public void setServerInput(ArrayList<BufferedReader> serverInput) {
		synchronized (serverInput) {
			this.serverInput = new ArrayList<BufferedReader>(serverInput);
		}
	}

	// Initialize server socket
	public void initServerSocket(MultiplayerSeissonInfo info) {
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
	 * Send a string message out to all other clients
	 * 
	 * @param Message
	 *            Message to send out
	 * @param id
	 *            Client to skip
	 */
	public void updateClients(String Message, int id) {
		PrintWriter writer = null;
		for (int i = 0; i < serverOutput.size(); i++) {
			if (i == id) {
				continue;
			}
			writer = serverOutput.get(i);
			writer.println(Message);
			writer.flush();
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
		
		// If player position update message
		if (msg[0].equals("loc")) {
			float[] position = { Float.parseFloat(msg[2]), Float.parseFloat(msg[3]) };
			float angle = Float.parseFloat(msg[4]);
			playerStats.updatePositionAndAngle(Integer.parseInt(msg[1]), position, angle);
		} else if (msg[0].equals("pos")) {
			float[] position = { Float.parseFloat(msg[2]), Float.parseFloat(msg[3]) };
			playerStats.updatePosition(Integer.parseInt(msg[1]), position);
		} else if (msg[0].equals("ang")) {
			float angle = Float.parseFloat(msg[2]);
			playerStats.updateAngle(Integer.parseInt(msg[1]), angle);
		}  else if (msg[0].equals("safe")) {
			System.out.println("Player "+msg[1]+"is safe.");
			playerStats.updateIsInSafeRegion(Integer.parseInt(msg[1]), Integer.parseInt(msg[2]));
		}	else if (msg[0].equals("useItem")) {
			System.out.println("Player "+msg[1]+"using item.");
			playerStats.updateUseItem(Integer.parseInt(msg[1]), Integer.parseInt(msg[2]));
		}	else if (msg[0].equals("useWeapon")) {
			System.out.println("Player "+msg[1]+"using weapon.");
			playerStats.updateUseWeapon(Integer.parseInt(msg[1]), Integer.parseInt(msg[2]));
		}
		
		// If item consumption or production message
		else if (msg[0].equals("item")) {
			if (msg[2].equals("con")) {
				objectLocations.consumeItem(new Location(new float[] { Float.parseFloat(msg[3]), Float.parseFloat(msg[4]) }),Integer.parseInt(msg[1]));
			} else if (msg[2].equals("pro")) {
				objectLocations.produceItemGhost(new Location(new float[] { Float.parseFloat(msg[3]), Float.parseFloat(msg[4]) }),Integer.parseInt(msg[1]));
			}
		} else if (msg[0].equals("weapon")) {
			if (msg[2].equals("con")) {
				objectLocations.consumeWeapon(new Location(new float[] { Float.parseFloat(msg[3]), Float.parseFloat(msg[4]) }),Integer.parseInt(msg[1]));
			} else if (msg[2].equals("pro")) {
				objectLocations.produceWeaponGhost(new Location(new float[] { Float.parseFloat(msg[3]), Float.parseFloat(msg[4]) }),Integer.parseInt(msg[1]));
			}
		} else if (msg[0].equals("weaponpart")) {
			if (msg[2].equals("con")) {
				objectLocations.consumeWeaponPart(new Location(new float[] { Float.parseFloat(msg[3]), Float.parseFloat(msg[4]) }),Integer.parseInt(msg[1]));
			}
		} else if (msg[0].equals("trap")) {
			if (msg[2].equals("con")) {
				objectLocations.consumeTrap(new Location(new float[] { Float.parseFloat(msg[3]), Float.parseFloat(msg[4]) }),Integer.parseInt(msg[1]));
			} else if (msg[2].equals("pro")) {
				objectLocations.produceTrap(new Location(new float[] { Float.parseFloat(msg[3]), Float.parseFloat(msg[4]) }),Integer.parseInt(msg[1]));
			}
		}
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
				System.out.println("Error while reading: " + e.getMessage());
			}

		}
	}
}
