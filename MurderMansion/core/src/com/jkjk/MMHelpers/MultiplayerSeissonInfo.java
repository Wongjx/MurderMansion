package com.jkjk.MMHelpers;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;


/**
 * Storage class to store all information needed for google play services in
 * @author Wong
 *
 */
public class MultiplayerSeissonInfo {
	public volatile String mIncomingInvitationId;
	public volatile String mRoomId;
	public volatile ArrayList mParticipants;
	public volatile Object mMyId; 
	public volatile int mState=1000;
	
	public Socket sock;
	public ServerSocket server;
	public SocketAddress socketAddress;
	public ArrayList<Socket> clients;
	
	public final int ROOM_NULL=1000;
	public final int ROOM_WAIT=1001;
	public final int ROOM_PLAY=1002;
	public final int ROOM_MENU=1003;
	
	
	public MultiplayerSeissonInfo(){
		
	}
}
