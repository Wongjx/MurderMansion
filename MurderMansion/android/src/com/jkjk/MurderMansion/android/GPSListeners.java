package com.jkjk.MurderMansion.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;

public class GPSListeners implements RoomStatusUpdateListener, RoomUpdateListener, OnInvitationReceivedListener{
	
	private String TAG = "MurderMansion GPS listeners";
	
    // Request codes for the UIs that we show with startActivityForResult:
    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_INVITATION_INBOX = 10001;
    final static int RC_WAITING_ROOM = 10002;
    
	private GoogleApiClient mGoogleApiClient;
	private AndroidLauncher activity;
	
	public GPSListeners(GoogleApiClient mGoogleApiClient,AndroidLauncher activity){
		this.mGoogleApiClient=mGoogleApiClient;
		this.activity=activity;
	}

	// Called when we get an invitation to play a game. We react by showing that to the user.
    @Override
    public void onInvitationReceived(Invitation invitation) {
        // We got an invitation to play a game! So, store it in
        // mIncomingInvitationId
        // and show the popup on the screen.
        activity.setmIncomingInvitationId(invitation.getInvitationId());
//        .setText(invitation.getInviter().getDisplayName() + " is inviting you.");
        //Create a text pop-up for invitations
    }

    @Override
    public void onInvitationRemoved(String invitationId) {
        if (activity.getmIncomingInvitationId().equals(invitationId)) {
        	activity.setmIncomingInvitationId(null);
            //Hide invitation pop up
        }
    }

    @Override
    public void onJoinedRoom(int statusCode, Room room) {
        Log.d(TAG, "onJoinedRoom(" + statusCode + ", " + room + ")");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
//            showGameError();
            return;
        }

//         show the waiting room UI
        showWaitingRoom(room);
    }

	
    // Called when we've successfully left the room (this happens a result of voluntarily leaving
    // via a call to leaveRoom(). If we get disconnected, we get onDisconnectedFromRoom()).
    @Override
    public void onLeftRoom(int statusCode, String roomId) {
        // we have left the room; return to main screen.
        Log.d(TAG, "onLeftRoom, code " + statusCode);
//        switchToMainScreen();
    }

    // Called when room is fully connected.
    @Override
    public void onRoomConnected(int statusCode, Room room) {
        Log.d(TAG, "onRoomConnected(" + statusCode + ", " + room + ")");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
//            showGameError();
            return;
        }
//        updateRoom(room);
    }

    // Called when room has been created
    @Override
    public void onRoomCreated(int statusCode, Room room) {
        Log.d(TAG, "onRoomCreated(" + statusCode + ", " + room + ")");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomCreated, status " + statusCode);
            handleError(statusCode);
//            showGameError();
            return;
        }

//         show the waiting room UI
        showWaitingRoom(room);
    }

    // Called when we are connected to the room. We're not ready to play yet! (maybe not everybody
    // is connected yet).
    @Override
    public void onConnectedToRoom(Room room) {
        Log.d(TAG, "onConnectedToRoom.");

        // get room ID, participants and my ID:
        activity.setmRoomId(room.getRoomId());
        activity.setmParticipants(room.getParticipants());
        activity.setmMyId(room.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient)));

        // print out the list of participants (for debug purposes)
        Log.d(TAG, "Room ID: " + activity.getmRoomId());
        Log.d(TAG, "My ID " + activity.getmMyId());
        Log.d(TAG, "<< CONNECTED TO ROOM>>");
    }


    // Called when we get disconnected from the room. We return to the main screen.
    @Override
    public void onDisconnectedFromRoom(Room room) {
        activity.setmRoomId(null);
//        showGameError();
    }
    // Show error message about game being cancelled and return to main screen.
//    void showGameError() {
//        BaseGameUtils.makeSimpleDialog(this, getString(R.string.game_problem));
//        switchToMainScreen();
//    }

    
    // We treat most of the room update callbacks in the same way: we update our list of
    // participants and update the display. In a real game we would also have to check if that
    // change requires some action like removing the corresponding player avatar from the screen,
    // etc.
    
    @Override
    public void onPeerDeclined(Room room, List<String> arg1) {
        updateRoom(room);
    }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> arg1) {
        updateRoom(room);
    }

    @Override
    public void onP2PDisconnected(String participant) {
    }

    @Override
    public void onP2PConnected(String participant) {
    }

    @Override
    public void onPeerJoined(Room room, List<String> arg1) {
        updateRoom(room);
    }

    @Override
    public void onPeerLeft(Room room, List<String> peersWhoLeft) {
        updateRoom(room);
    }

    @Override
    public void onRoomAutoMatching(Room room) {
        updateRoom(room);
    }

    @Override
    public void onRoomConnecting(Room room) {
        updateRoom(room);
    }

    @Override
    public void onPeersConnected(Room room, List<String> peers) {
        updateRoom(room);
    }

    @Override
    public void onPeersDisconnected(Room room, List<String> peers) {
        updateRoom(room);
    }

    void updateRoom(Room room) {
        if (room != null) {
            activity.setmParticipants(room.getParticipants());
        }
        if (activity.getmParticipants() != null) {
//            updatePeerScoresDisplay();
        }
    }
    
    // Show the waiting room UI to track the progress of other players as they enter the
    // room and get connected.
    void showWaitingRoom(Room room) {
        // minimum number of players required for our game
        // For simplicity, we require everyone to join the game before we start it
        // (this is signaled by Integer.MAX_VALUE).
        final int MIN_PLAYERS = Integer.MAX_VALUE;
        Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, MIN_PLAYERS);

        // show waiting room UI
        activity.startActivityForResult(i, RC_WAITING_ROOM);
    }
    
    private void handleError(int statusCode){
    	switch (statusCode){
    	case ConnectionResult.RESOLUTION_REQUIRED:
    		activity.gameHelper.beginUserInitiatedSignIn();
    	}
    }

}
