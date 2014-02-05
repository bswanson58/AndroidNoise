package com.SecretSquirrel.AndroidNoise.events;

// Secret Squirrel Software - Created by bswanson on 2/5/14.

public class EventAlbumNameRequest {
	private final String    mArtistName;
	private final String    mAlbumName;

	public EventAlbumNameRequest( String artistName, String albumName ) {
		mArtistName = artistName;
		mAlbumName = albumName;
	}

	public String getArtistName() {
		return( mArtistName );
	}

	public String getAlbumName() {
		return( mAlbumName );
	}
}
