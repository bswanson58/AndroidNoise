package com.SecretSquirrel.AndroidNoise.events;

// Secret Squirrel Software - Created by bswanson on 1/28/14.

public class EventArtistRequest {
	private long    mArtistId;

	public EventArtistRequest( long artistId ) {
		mArtistId = artistId;
	}

	public long getArtistId() {
		return( mArtistId );
	}
}
