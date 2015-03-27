package com.jkjk.MMHelpers;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


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
	
	public boolean isServer;
	public InetAddress socketAddress;
	public int port;
	public Condition isMessaged;
	public ReentrantLock lock;
	
	public ServerSocket serverSocket;
	public ArrayList<Socket> clients;
	public ArrayList<PrintWriter> serverOutput;
	public ArrayList<BufferedReader> serverInput;
	
	public Socket clientSocket;
	public BufferedReader clientInput;
	public PrintWriter clientOuput;
	
	public final int ROOM_NULL=1000;
	public final int ROOM_WAIT=1001;
	public final int ROOM_PLAY=1002;
	public final int ROOM_SOCKET=1003;
	public final int ROOM_MENU=1004;
	
	
	public MultiplayerSeissonInfo(){
		this.lock=new ReentrantLock();
		this.isMessaged=this.lock.newCondition();
	}
}
