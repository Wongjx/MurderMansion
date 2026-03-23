package com.jkjk.MMHelpers;

public class NoOpActionResolver implements ActionResolver {

	@Override
	public boolean getSignedInGPGS() {
		return false;
	}

	@Override
	public void loginGPGS() {
	}

	@Override
	public void logoutGPGS() {
	}

	@Override
	public void submitScoreGPGS(int score) {
	}

	@Override
	public void unlockAchievementGPGS(String achievementId) {
	}

	@Override
	public void getLeaderboardGPGS() {
	}

	@Override
	public void getAchievementsGPGS() {
	}

	@Override
	public void startQuickGame() {
	}

	@Override
	public void seeInvitations() {
	}

	@Override
	public void sendInvitations() {
	}

	@Override
	public void leaveRoom() {
	}
}
