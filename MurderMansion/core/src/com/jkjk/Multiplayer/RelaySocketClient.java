package com.jkjk.Multiplayer;

public interface RelaySocketClient {
	void connect(String url, RelaySocketListener listener) throws Exception;

	void send(String text) throws Exception;

	void close();
}
