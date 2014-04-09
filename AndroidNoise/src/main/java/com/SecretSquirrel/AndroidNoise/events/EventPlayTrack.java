package com.SecretSquirrel.AndroidNoise.events;

// Secret Squirrel Software - Created by bswanson on 12/23/13.

import com.SecretSquirrel.AndroidNoise.dto.Track;

public class EventPlayTrack {
	private long    mArtistId;
	private long    mTrackId;
	private String  mTrackName;

	public EventPlayTrack( Track track ) {
		mArtistId = track.getArtistId();
		mTrackId = track.getTrackId();
		mTrackName = track.getTrackName();
	}

	public EventPlayTrack( long artistId, long trackId, String trackName ) {
		mArtistId = artistId;
		mTrackId = trackId;
		mTrackName = trackName;
	}

	public long getArtistId() {
		return( mArtistId );
	}

	public long getTrackId() {
		return( mTrackId );
	}

	public String getTrackName() {
		return( mTrackName );
	}
}

