package com.SecretSquirrel.AndroidNoise.events;

// Secret Squirrel Software - Created by bswanson on 2/4/14.

public class EventQueueTimeUpdate {
	private final long    mTotalTime;
	private final long    mRemainingTime;

	public EventQueueTimeUpdate( long totalTime, long remainingTime ) {
		mTotalTime = totalTime;
		mRemainingTime = remainingTime;
	}

	public long getTotalTime() {
		return( mTotalTime );
	}

	public long getRemainingTime() {
		return( mRemainingTime );
	}
}
