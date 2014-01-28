package com.SecretSquirrel.AndroidNoise.events;

// Secret Squirrel Software - Created by bswanson on 1/28/14.

public class EventAlbumRequest {
	private long    mArtistId;
	private long    mAlbumId;

	public EventAlbumRequest( long artistId, long albumId ) {
		mArtistId = artistId;
		mAlbumId = albumId;
	}

	public long getArtistId() {
		return( mArtistId );
	}

	public long getAlbumId() {
		return( mAlbumId );
	}
}
