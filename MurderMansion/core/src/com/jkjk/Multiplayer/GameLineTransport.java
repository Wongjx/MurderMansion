package com.jkjk.Multiplayer;

import java.io.IOException;
import java.net.SocketTimeoutException;

public interface GameLineTransport {
	String readLine() throws IOException;

	String readLine(long timeoutMillis) throws IOException, SocketTimeoutException;

	void sendLine(String line) throws IOException;

	void close() throws IOException;

	boolean isClosed();
}
