package com.SecretSquirrel.AndroidNoise.events;

// Secret Squirrel Software - Created by bswanson on 2/28/14.

public class EventTransportUpdate {
	private long    mReceivedTime;
	private long    mServerTime;
	private long    mCurrentTrack;
	private long    mCurrentPosition;
	private long    mTrackLength;
	private int     mPlayState;

	public EventTransportUpdate( int playState, long serverTime, long currentTrack, long currentPosition, long trackLength ) {
		mReceivedTime = System.currentTimeMillis();

		mPlayState = playState;
		mServerTime = serverTime;
		mCurrentTrack = currentTrack;
		mCurrentPosition = currentPosition;
		mTrackLength = trackLength;
	}

	public int getPlayState() {
		return( mPlayState );
	}

	public long getServerTime() {
		return( mServerTime );
	}

	public long getCurrentTrack() {
		return( mCurrentTrack );
	}

	public long getCurrentPosition() {
		return( mCurrentPosition );
	}

	public long getTrackLength() {
		return( mTrackLength );
	}

	public long getTimeReceived() {
		return( mReceivedTime );
	}

	public long getTimeDifference() {
		return( mReceivedTime - mServerTime );
	}
}
