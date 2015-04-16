package com.jkjk.MurderMansion.desktop;

import java.util.ArrayList;

import com.jkjk.MMHelpers.MultiplayerSessionInfo;

public class desktopMultiplayer extends MultiplayerSessionInfo{
	public volatile	boolean mMultiplayer=false; 
	public volatile String mIncomingInvitationId="Desktop has nothing";
	public volatile String mRoomId="Desktop room";
	public volatile ArrayList mParticipants;
	public volatile Object mMyId; 
	
	public desktopMultiplayer(){
		
	}
}
