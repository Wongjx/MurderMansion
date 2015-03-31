package com.jkjk.MurderMansion.android;

import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.Arrays;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.jkjk.Host.MMServer;
import com.jkjk.MMHelpers.MultiplayerSeissonInfo;

public class RealTimeCommunication implements RealTimeMessageReceivedListener{
	private String TAG = "MurderMansion RealTime Communications";
	private GoogleApiClient mGoogleApiClient;
	private MultiplayerSeissonInfo info;

    
    public RealTimeCommunication(GoogleApiClient api, MultiplayerSeissonInfo info){
//    	this.activity=activity;
    	this.mGoogleApiClient=api;
    	this.info=info;
    }

    // Called when we receive a real-time message from the network.
    // Messages in our game are made up of 2 bytes: the first one is 'F' or 'U'
    // indicating
    // whether it's a final or interim score. The second byte is the score.
    // There is also the
    // 'S' message, which indicates that the game should start.
    
    @Override
    public void onRealTimeMessageReceived(RealTimeMessage rtm) {
        byte[] buf = rtm.getMessageData();
        String sender = rtm.getSenderParticipantId();
        String Msg;
        
        try{
    		String messageType = new String (Arrays.copyOfRange(buf,0,1),"UTF-8");
    		Log.d(TAG,"MessageType: "+messageType);
    		
    		if (messageType.equals("A")){
    			//Create a InetSocketAddress
    			byte[] addr = Arrays.copyOfRange(buf, 1, buf.length-1);
    			Msg = new String (addr,"UTF-8");
    			Log.d(TAG,"Address received: "+Msg);
    			
    			InetAddress iAddress = InetAddress.getByAddress(addr);
    			info.serverAddress=iAddress;
    			
    		}else if (messageType.equals("P")){
    			//Retrieve and store port number
    			byte[] port = Arrays.copyOfRange(buf, 1, buf.length-1);
    			Msg = new String (port,"UTF-8");
    			Log.d(TAG,"Port Number received: "+Msg);
    			info.serverPort=Integer.parseInt(Msg);
    			
    		}else{
    			Log.d(TAG, "Message type is not recognised.");
    		}
        	
        }catch (Exception e){
        	Log.d(TAG, "Error reading from received message: "+e.getMessage());
        }
    }

    // Broadcast serverLocalAddress to all connected clients
    void broadcastAddress(MMServer server) {
        if (!info.isServer)
            return; // Player is not server
        
        String Msg ="A";
        Msg += server.getServerAddress().toString();
        byte[] mMsgBuf = new byte[Msg.length()];
//        // Encode String Message in UTF_8 byte format for transmission
        
        mMsgBuf =Msg.getBytes(Charset.forName("UTF-8")); 
        
//        // Send to every other participant.
        for (Object o : info.mParticipants) {
        	Participant p = (Participant) o;
        	if (p.getStatus() != Participant.STATUS_JOINED)
        		continue;
        	Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, null, mMsgBuf,
        			info.mRoomId, p.getParticipantId());
        }
    }

// Broadcast port Number to all connected clients
void broadcastPort(MMServer server) {
    if (!info.isServer)
        return; // Player is not server
    String Msg ="P";
    Msg += String.valueOf(server.getServerPort());
    
    byte[] mMsgBuf = new byte[Msg.length()];
    // Encode String Message in UTF_8 byte format for transmission    
    mMsgBuf =Msg.getBytes(Charset.forName("UTF-8")); 
    
    // Send to every other participant.
    for (Object o : info.mParticipants) {
    	Participant p = (Participant) o;
    	if (p.getStatus() != Participant.STATUS_JOINED)
    		continue;
    	Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, null, mMsgBuf,
    			info.mRoomId, p.getParticipantId());
    }
}
}
