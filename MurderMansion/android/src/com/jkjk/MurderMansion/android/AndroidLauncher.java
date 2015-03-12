package com.jkjk.MurderMansion.android;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;
import com.jkjk.MMHelpers.ActionResolver;
import com.jkjk.MurderMansion.murdermansion;

public class AndroidLauncher extends AndroidApplication implements GameHelperListener, ActionResolver{
	
	private String TAG = "MurderMansion Andriod Launcher";
    // Request codes for the UIs that we show with startActivityForResult:
    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_INVITATION_INBOX = 10001;
    final static int RC_WAITING_ROOM = 10002;
    // Request code used to invoke sign in user interactions.
    private static final int RC_SIGN_IN = 9001;

	private String mIncomingInvitationId;
	private String mRoomId;
	private ArrayList<Participant> mParticipants;
	private Object mMyId;
	
	private GameHelper gameHelper;
	public GoogleApiClient mGoogleApiClient;
	public GPSListeners mGooglePlayListeners;
	public RealTimeCommunication mRealTimeListener;
	
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (gameHelper == null) {
			gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
			gameHelper.enableDebugLog(true);
		}
		gameHelper.setMaxAutoSignInAttempts(0);
		gameHelper.setup(this);
		Log.d(TAG,"gamehelper setup");
		
		//Get and store api client for multi-player services
		mGoogleApiClient=gameHelper.getApiClient();
		Log.d(TAG,"Set Google API client");
		
		//Initialize listener helper class
		if (mGooglePlayListeners == null) {
			mGooglePlayListeners = new GPSListeners(mGoogleApiClient,this);
		}
		
		if (mRealTimeListener == null) {
			mRealTimeListener = new RealTimeCommunication(mGoogleApiClient,this);
		}
		
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new murdermansion(this), config);

	}

	@Override
	public void onStart(){
		super.onStart();
		gameHelper.onStart(this);
	}

	@Override
	public void onStop(){
		super.onStop();
		gameHelper.onStop();
	}

	@Override
	public void onActivityResult(int requestCode, int responseCode, Intent intent) {
		super.onActivityResult(requestCode, responseCode, intent);
		
		 switch (requestCode) {
         case RC_SELECT_PLAYERS:
             // we got the result from the "select players" UI -- ready to create the room
//             handleSelectPlayersResult(responseCode, intent);
             break;
         case RC_INVITATION_INBOX:
             // we got the result from the "select invitation" UI (invitation inbox). We're
             // ready to accept the selected invitation:
//             handleInvitationInboxResult(responseCode, intent);
             break;
         case RC_WAITING_ROOM:
             // we got the result from the "waiting room" UI.
             if (responseCode == Activity.RESULT_OK) {
                 // ready to start playing
                 Log.d(TAG, "Starting game (waiting room returned OK).");
//                 startGame(true);
             } else if (responseCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                 // player indicated that they want to leave the room
                 leaveRoom();
             } else if (responseCode == Activity.RESULT_CANCELED) {
                 // Dialog was cancelled (user pressed back key, for instance). In our game,
                 // this means leaving the room too. In more elaborate games, this could mean
                 // something else (like minimizing the waiting room UI).
                 leaveRoom();
             }
             break;
         case RC_SIGN_IN:
     		gameHelper.onActivityResult(requestCode, responseCode, intent);
             break;
     }
	}
	
	@Override
	public boolean getSignedInGPGS() {
		return gameHelper.isSignedIn();
	}

	@Override
	public void loginGPGS() {
		try {
			runOnUiThread(new Runnable(){
				public void run() {
					gameHelper.beginUserInitiatedSignIn();
				}
			});
		} catch (final Exception ex) {
			Gdx.app.log("MainActivity", "Log in failed: " + ex.getMessage() + ".");

		}
	}

	@Override
	public void submitScoreGPGS(int score) {
		Games.Leaderboards.submitScore(gameHelper.getApiClient(), "CgkI6574wJUXEAIQBw", score);

	}
	
	@Override
	public void unlockAchievementGPGS(String achievementId) {
	  Games.Achievements.unlock(gameHelper.getApiClient(), achievementId);
	}
	
	@Override
	public void getLeaderboardGPGS() {
	  if (gameHelper.isSignedIn()) {
	    startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(), "CgkI6574wJUXEAIQBw"), 100);
	  }
	  else if (!gameHelper.isConnecting()) {
	    loginGPGS();
	  }
	}

	@Override
	public void getAchievementsGPGS() {
	  if (gameHelper.isSignedIn()) {
	    startActivityForResult(Games.Achievements.getAchievementsIntent(gameHelper.getApiClient()), 101);
	  }
	  else if (!gameHelper.isConnecting()) {
	    loginGPGS();
	  }
	}
	
	@Override
	public void onSignInFailed() {
	}

	@Override
	public void onSignInSucceeded() {
	}

	
	@Override
	public void startQuickGame() {
		// quick-start a game with 1 randomly selected opponent

		final int MIN_OPPONENTS = 1, MAX_OPPONENTS = 1;
		Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS,MAX_OPPONENTS, 0);

		RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(mGooglePlayListeners);
		rtmConfigBuilder.setMessageReceivedListener(mRealTimeListener);
		rtmConfigBuilder.setRoomStatusUpdateListener(mGooglePlayListeners);

		rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);

//		switchToScreen(R.id.screen_wait);
//		keepScreenOn();
//		resetGameVars();
		
		Games.RealTimeMultiplayer.create(mGoogleApiClient, rtmConfigBuilder.build());
	}
	
    // Leave the room.
    void leaveRoom() {
        Log.d(TAG, "Leaving room.");
        if (mRoomId != null) {
            Games.RealTimeMultiplayer.leave(this.mGoogleApiClient, this.mGooglePlayListeners, mRoomId);
            mRoomId = null;
//            switchToScreen(R.id.screen_wait);
        } else {
//            switchToMainScreen();
        }
    }

	public String getmIncomingInvitationId() {
		return mIncomingInvitationId;
	}

	public void setmIncomingInvitationId(String mIncomingInvitationId) {
		this.mIncomingInvitationId = mIncomingInvitationId;
	}

	public String getmRoomId() {
		return mRoomId;
	}

	public void setmRoomId(String mRoomId) {
		this.mRoomId = mRoomId;
	}

	public ArrayList<Participant> getmParticipants() {
		return mParticipants;
	}

	public void setmParticipants(ArrayList<Participant> mParticipants) {
		this.mParticipants = mParticipants;
	}

	public Object getmMyId() {
		return mMyId;
	}

	public void setmMyId(Object mMyId) {
		this.mMyId = mMyId;
	}
}
