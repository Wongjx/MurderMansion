package com.jkjk.Multiplayer;

public interface RelaySocketListener {
	void onOpen();

	void onMessage(String text);

	void onClose(String reason);

	void onError(Throwable throwable);
}
