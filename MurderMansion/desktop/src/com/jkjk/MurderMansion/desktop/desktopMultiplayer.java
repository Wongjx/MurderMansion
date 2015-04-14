package com.jkjk.MurderMansion.desktop;

import java.util.ArrayList;

import com.jkjk.GameWorld.MMClient;
import com.jkjk.Host.MMServer;
import com.jkjk.MMHelpers.MultiplayerSessionInfo;

public class desktopMultiplayer extends MultiplayerSessionInfo{
	
	public String mId="0";
	public String mIncomingInvitationId="0";
	public String mRoomId="0";
	public ArrayList mParticipants;
//	public Object mMyId; 
	public int mState=1000;

	public boolean isServer=true;
	public String serverAddress="0";
	public int serverPort=0;
	
	private MMServer server;
	private MMClient client;
	
	public final int ROOM_NULL=1000;
	public final int ROOM_WAIT=1001;
	public final int ROOM_PLAY=1002;
	public final int ROOM_MENU=1003;
	
	public desktopMultiplayer(){
		
	}

	public MMServer getServer() {
		return server;
	}

	public void setServer(MMServer server) {
		this.server = server;
	}

	public MMClient getClient() {
		return client;
	}

	public void setClient(MMClient client) {
		this.client = client;
	}
}
