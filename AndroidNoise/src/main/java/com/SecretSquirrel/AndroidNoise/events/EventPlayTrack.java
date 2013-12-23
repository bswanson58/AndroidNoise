package com.SecretSquirrel.AndroidNoise.events;

// Secret Squirrel Software - Created by bswanson on 12/23/13.

import com.SecretSquirrel.AndroidNoise.dto.Track;

public class EventPlayTrack {
	private Track   mTrack;

	public EventPlayTrack( Track track ) {
		mTrack = track;
	}

	public Track getTrack() {
		return mTrack;
	}
}
