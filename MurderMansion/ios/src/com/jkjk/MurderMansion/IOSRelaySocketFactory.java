package com.jkjk.MurderMansion;

import org.robovm.apple.foundation.NSOperationQueue;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.foundation.NSURLSession;
import org.robovm.apple.foundation.NSURLSessionConfiguration;
import org.robovm.apple.foundation.NSURLSessionWebSocketCloseCode;
import org.robovm.apple.foundation.NSURLSessionWebSocketDelegateAdapter;
import org.robovm.apple.foundation.NSURLSessionWebSocketMessage;
import org.robovm.apple.foundation.NSURLSessionWebSocketTask;

import com.jkjk.Multiplayer.RelaySocketClient;
import com.jkjk.Multiplayer.RelaySocketFactory;
import com.jkjk.Multiplayer.RelaySocketListener;

public class IOSRelaySocketFactory implements RelaySocketFactory {
	@Override
	public RelaySocketClient create() {
		return new IOSRelaySocketClient();
	}

	private static class IOSRelaySocketClient implements RelaySocketClient {
		private NSURLSession session;
		private NSURLSessionWebSocketTask task;
		private RelaySocketListener listener;

		@Override
		public void connect(String url, RelaySocketListener listener) {
			this.listener = listener;
			NSURLSessionConfiguration configuration = NSURLSessionConfiguration.getDefaultSessionConfiguration();
			NSOperationQueue queue = new NSOperationQueue();
			session = new NSURLSession(configuration, new NSURLSessionWebSocketDelegateAdapter() {
				@Override
				public void didOpen(NSURLSession session, NSURLSessionWebSocketTask webSocketTask, String protocol) {
					IOSRelaySocketClient.this.listener.onOpen();
				}

				@Override
				public void didClose(NSURLSession session, NSURLSessionWebSocketTask webSocketTask,
						NSURLSessionWebSocketCloseCode closeCode, org.robovm.apple.foundation.NSData reason) {
					IOSRelaySocketClient.this.listener.onClose(closeCode.name());
				}
			}, queue);
			task = session.newWebSocket(new NSURL(url));
			task.resume();
			startReceiveLoop();
		}

		private void startReceiveLoop() {
			task.receiveMessage((message, error) -> {
				if (error != null) {
					listener.onError(new IllegalStateException(error.getLocalizedDescription()));
					return;
				}
				if (message != null && message.getString() != null) {
					listener.onMessage(message.getString());
				}
				startReceiveLoop();
			});
		}

		@Override
		public void send(String text) {
			task.sendMessage(new NSURLSessionWebSocketMessage(text), error -> {
				if (error != null) {
					listener.onError(new IllegalStateException(error.getLocalizedDescription()));
				}
			});
		}

		@Override
		public void close() {
			if (task != null) {
				task.cancel(NSURLSessionWebSocketCloseCode.NormalClosure, null);
			}
			if (session != null) {
				session.invalidateAndCancel();
			}
		}
	}
}
