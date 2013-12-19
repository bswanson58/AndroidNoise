package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by bswanson on 12/19/13.

import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;

public class QueuedTrackResult extends BaseServerResult {
	private Track   mQueuedTrack;

	public QueuedTrackResult( Track track, BaseServerResult serverResult ) {
		super( serverResult );

		mQueuedTrack = track;
	}

	public Track getTrack() {
		return( mQueuedTrack );
	}
}
