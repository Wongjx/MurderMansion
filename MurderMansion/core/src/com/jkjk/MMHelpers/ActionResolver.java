package com.jkjk.MMHelpers;

public interface ActionResolver {
	public boolean getSignedInGPGS();
	public void loginGPGS();
	public void logoutGPGS();
	public void submitScoreGPGS(int score);
	public void unlockAchievementGPGS(String achievementId);
	public void getLeaderboardGPGS();
	public void getAchievementsGPGS();
	public void startQuickGame();
	public void seeInvitations();
	public void sendInvitations();
	public void leaveRoom();
	
	public final String ACHEIVEMENT_1= "CgkIjKaQnboIEAIQBg";	//You can't kat me
	public final String ACHEIVEMENT_2= "CgkIjKaQnboIEAIQBQ";	//An abosolutely amazing adventure
	public final String ACHEIVEMENT_3= "CgkIjKaQnboIEAIQBw";	//Give me some Lee-way
	public final String ACHEIVEMENT_4= "CgkIjKaQnboIEAIQCA";	//I Koh-uld have made it
	public final String ACHEIVEMENT_5= "CgkIjKaQnboIEAIQCQ";	//I made the Wong choice
	public final String ACHEIVEMENT_6= "CgkIjKaQnboIEAIQCg";	//7th Sun of the 7th Jun
	public final String ACHEIVEMENT_7= "CgkIjKaQnboIEAIQCw";	//On the road to murder
	public final String ACHEIVEMENT_8= "CgkIjKaQnboIEAIQDA"; 	//Trained to RUNNNNNN
	
	
}
