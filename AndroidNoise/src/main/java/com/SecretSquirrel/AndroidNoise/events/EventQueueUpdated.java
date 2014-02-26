package com.SecretSquirrel.AndroidNoise.events;

// Secret Squirrel Software - Created by bswanson on 2/26/14.

import com.SecretSquirrel.AndroidNoise.dto.PlayQueueTrack;

import java.util.ArrayList;

public class EventQueueUpdated {
	private final ArrayList<PlayQueueTrack>   mQueueList;

	public EventQueueUpdated( ArrayList<PlayQueueTrack> queueList ) {
		mQueueList = queueList;
	}

	public ArrayList<PlayQueueTrack> getQueueList() {
		return( mQueueList );
	}
}
