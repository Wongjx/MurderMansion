package com.jkjk.MurderMansion.desktop;

import java.util.ArrayList;

import com.jkjk.MMHelpers.MultiplayerSeissonInfo;

public class desktopMultiplayer extends MultiplayerSeissonInfo{
	public volatile	boolean mMultiplayer=false; 
	public volatile String mIncomingInvitationId="Desktop has nothing";
	public volatile String mRoomId="Desktop room";
	public volatile ArrayList mParticipants;
	public volatile Object mMyId; 
	
	public desktopMultiplayer(){
		
	}
}
