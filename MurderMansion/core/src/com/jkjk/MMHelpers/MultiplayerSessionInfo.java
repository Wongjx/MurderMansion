package com.jkjk.MMHelpers;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.jkjk.GameWorld.MMClient;
import com.jkjk.Host.MMServer;


/**
 * Storage class to store all information needed for google play services in
 * @author Wong
 *
 */
public class MultiplayerSessionInfo {
	
	public String mId;
	public String mIncomingInvitationId;
	public String mRoomId;
	public ArrayList mParticipants;
	public String mName;
	public int mState=1000;

	public boolean isServer;
	public String serverAddress;
	public int serverPort=0;
	
	private MMServer server;
	private MMClient client;
	
	public final int ROOM_NULL=1000;
	public final int ROOM_WAIT=1001;
	public final int ROOM_PLAY=1002;
	public final int ROOM_MENU=1003;
	
	public MultiplayerSessionInfo(){
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
	
	public void endSession(){
		mId=null;
		mIncomingInvitationId=null;
		mRoomId=null;
		mParticipants=null;
		mState=ROOM_MENU;
		mName = null;

		isServer=false;
		serverAddress=null;
		serverPort=0;
		
		server=null;
		client=null;

	}


}
