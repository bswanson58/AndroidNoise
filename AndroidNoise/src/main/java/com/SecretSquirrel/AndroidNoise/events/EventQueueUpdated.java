package com.SecretSquirrel.AndroidNoise.events;

// Secret Squirrel Software - Created by bswanson on 2/26/14.

import com.SecretSquirrel.AndroidNoise.dto.PlayQueueTrack;

import java.util.ArrayList;

public class EventQueueUpdated {
	private final ArrayList<PlayQueueTrack> mQueueList;
	private final PlayQueueTrack            mCurrentlyPlayingTrack;

	public EventQueueUpdated( ArrayList<PlayQueueTrack> queueList, PlayQueueTrack currentlyPlayingTrack ) {
		mQueueList = queueList;
		mCurrentlyPlayingTrack = currentlyPlayingTrack;
	}

	public ArrayList<PlayQueueTrack> getQueueList() {
		return( mQueueList );
	}

	public PlayQueueTrack getCurrentlyPlayingTrack() {
		return( mCurrentlyPlayingTrack );
	}
}
