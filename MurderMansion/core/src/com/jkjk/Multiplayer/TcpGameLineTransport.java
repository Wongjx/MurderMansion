package com.jkjk.Multiplayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class TcpGameLineTransport implements GameLineTransport {
	private final Socket socket;
	private final BufferedReader input;
	private final PrintWriter output;

	public TcpGameLineTransport(String address, int port) throws IOException {
		socket = new Socket();
		socket.setSoTimeout(10000);
		InetAddress addr = InetAddress.getByName(address);
		InetSocketAddress iAddress = new InetSocketAddress(addr, port);
		socket.connect(iAddress);
		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		output = new PrintWriter(socket.getOutputStream(), true);
	}

	@Override
	public String readLine() throws IOException {
		return input.readLine();
	}

	@Override
	public String readLine(long timeoutMillis) throws IOException, SocketTimeoutException {
		int originalTimeout = socket.getSoTimeout();
		try {
			socket.setSoTimeout((int) timeoutMillis);
			return input.readLine();
		} finally {
			socket.setSoTimeout(originalTimeout);
		}
	}

	@Override
	public void sendLine(String line) {
		output.println(line);
		output.flush();
	}

	@Override
	public void close() throws IOException {
		output.close();
		input.close();
		socket.close();
	}

	@Override
	public boolean isClosed() {
		return socket.isClosed();
	}
}
