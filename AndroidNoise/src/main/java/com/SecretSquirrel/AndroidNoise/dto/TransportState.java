package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by bswanson on 2/28/14.

import com.SecretSquirrel.AndroidNoise.services.rto.RoTransportState;

public class TransportState {
	private long    mServerTime;
	private long    mReceivedTime;
	private int     mPlayState;
	private long    mCurrentTrack;
	private long    mCurrentTrackPosition;
	private long    mCurrentTrackLength;

	public TransportState( RoTransportState roState ) {
		mReceivedTime = System.currentTimeMillis();

		mServerTime = roState.ServerTime;
		mPlayState = roState.PlayState;
		mCurrentTrack = roState.CurrentTrack;
		mCurrentTrackPosition = roState.CurrentTrackPosition;
		mCurrentTrackLength = roState.CurrentTrackLength;
	}

	public long getServerTime() {
		return( mServerTime );
	}

	public long getReceivedTime() {
		return( mReceivedTime );
	}

	public int getPlayState() {
		return( mPlayState );
	}

	public long getCurrentTrack() {
		return( mCurrentTrack );
	}

	public long getCurrentTrackPosition() {
		return( mCurrentTrackPosition );
	}

	public long getCurrentTrackLength() {
		return( mCurrentTrackLength );
	}
}
