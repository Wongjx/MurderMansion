package com.jkjk.MurderMansion.desktop;

import com.jkjk.MMHelpers.ActionResolver;
import com.jkjk.MMHelpers.MultiplayerSessionInfo;

public class ActionResolverDesktop implements ActionResolver {	
	boolean signedInStateGPGS = false;
	public MultiplayerSessionInfo mMultiplayerSeisson;
	
	public ActionResolverDesktop(MultiplayerSessionInfo mMultiplayerSeisson){
		this.mMultiplayerSeisson=mMultiplayerSeisson;
	}
	@Override
	public boolean getSignedInGPGS() {
		return signedInStateGPGS;
	}

	@Override
	public void loginGPGS() {
		System.out.println("loginGPGS");
		signedInStateGPGS = true;
	}

	@Override
	public void submitScoreGPGS(int score) {
		System.out.println("submitScoreGPGS " + score);
	}

	@Override
	public void unlockAchievementGPGS(String achievementId) {
		System.out.println("unlockAchievement " + achievementId);
	}

	@Override
	public void getLeaderboardGPGS() {
		System.out.println("getLeaderboardGPGS");
	}

	@Override
	public void getAchievementsGPGS() {
		System.out.println("getAchievementsGPGS");
	}

	@Override
	public void startQuickGame() {
		// TODO Auto-generated method stub
		System.out.println("Start quick game");
		this.mMultiplayerSeisson.mState=mMultiplayerSeisson.ROOM_PLAY;
	}

	@Override
	public void logoutGPGS() {
		// TODO Auto-generated method stub
		System.out.println("Log out of GPGS");
		
	}

	@Override
	public void seeInvitations() {
		System.out.println("See invites");
		this.mMultiplayerSeisson.mState=mMultiplayerSeisson.ROOM_PLAY;
	}

	@Override
	public void sendInvitations() {
		System.out.println("Send out invite");
		this.mMultiplayerSeisson.mState=mMultiplayerSeisson.ROOM_PLAY;
		
	}
	@Override
	public void leaveRoom(){
		System.out.println("Leave room");
	}
}
