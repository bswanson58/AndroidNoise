package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by BSwanson on 12/19/13.

import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;

public class QueuedTrackResult extends BaseServerResult {
	private Track   mQueuedTrack;
	private long    mTrackId;
	private int     mTrackCount;

	public QueuedTrackResult( long[] trackList, BaseServerResult serverResult ) {
		super( serverResult );

		mTrackCount = trackList.length;
	}
	public QueuedTrackResult( Track track, BaseServerResult serverResult ) {
		super( serverResult );

		mQueuedTrack = track;
		mTrackCount = 1;
	}

	public QueuedTrackResult( long trackId, BaseServerResult serverResult ) {
		super( serverResult );

		mTrackId = trackId;
		mTrackCount = 1;
	}

	public Track getTrack() {
		return( mQueuedTrack );
	}

	public int getTrackCount() {
		return( mTrackCount );
	}
}
