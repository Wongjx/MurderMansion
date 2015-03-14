package com.jkjk.MMHelpers;

import java.util.ArrayList;
import java.util.List;


/**
 * Storage class to store all information needed for google play services in
 * @author Wong
 *
 */
public class MultiplayerSeissonInfo {
	public volatile	boolean mMultiplayer=false; 
	public volatile String mIncomingInvitationId;
	public volatile String mRoomId;
	public volatile ArrayList mParticipants;
	public volatile Object mMyId; 
	
	public MultiplayerSeissonInfo(){
		
	}
}
