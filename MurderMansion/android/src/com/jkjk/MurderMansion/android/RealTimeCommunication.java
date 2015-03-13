package com.jkjk.MurderMansion.android;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;

public class RealTimeCommunication implements RealTimeMessageReceivedListener{
	private String TAG = "MurderMansion RealTime Communications";
	private Activity activity;
	private GoogleApiClient mGoogleApiClient;

    // Score of other participants. We update this as we receive their scores
    // from the network.
    Map<String, Integer> mParticipantScore = new HashMap<String, Integer>();

    // Participants who sent us their final score.
    Set<String> mFinishedParticipants = new HashSet<String>();
    
    public RealTimeCommunication(GoogleApiClient api, Activity activity){
    	this.activity=activity;
    	this.mGoogleApiClient=api;
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
        Log.d(TAG, "Message received: " + (char) buf[0] + "/" + (int) buf[1]);

        if (buf[0] == 'F' || buf[0] == 'U') {
            // score update.
            int existingScore = mParticipantScore.containsKey(sender) ?
                    mParticipantScore.get(sender) : 0;
            int thisScore = (int) buf[1];
            if (thisScore > existingScore) {
                // this check is necessary because packets may arrive out of
                // order, so we
                // should only ever consider the highest score we received, as
                // we know in our
                // game there is no way to lose points. If there was a way to
                // lose points,
                // we'd have to add a "serial number" to the packet.
                mParticipantScore.put(sender, thisScore);
            }

            // update the scores on the screen
//            updatePeerScoresDisplay();

            // if it's a final score, mark this participant as having finished
            // the game
            if ((char) buf[0] == 'F') {
                mFinishedParticipants.add(rtm.getSenderParticipantId());
            }
        }
    }

    // Broadcast my score to everybody else.
    void broadcastScore(boolean finalScore) {
//        if (!mMultiplayer)
//            return; // playing single-player mode

//        // First byte in message indicates whether it's a final score or not
//        mMsgBuf[0] = (byte) (finalScore ? 'F' : 'U');
//
//        // Second byte is the score.
//        mMsgBuf[1] = (byte) mScore;
//
//        // Send to every other participant.
//        for (Participant p : mParticipants) {
//            if (p.getParticipantId().equals(mMyId))
//                continue;
//            if (p.getStatus() != Participant.STATUS_JOINED)
//                continue;
//            if (finalScore) {
//                // final score notification must be sent via reliable message
//                Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, null, mMsgBuf,
//                        mRoomId, p.getParticipantId());
//            } else {
//                // it's an interim score notification, so we can use unreliable
//                Games.RealTimeMultiplayer.sendUnreliableMessage(mGoogleApiClient, mMsgBuf, mRoomId,
//                        p.getParticipantId());
//            }
//        }
    }

}
