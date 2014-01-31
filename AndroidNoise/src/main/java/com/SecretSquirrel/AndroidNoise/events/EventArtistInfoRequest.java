package com.SecretSquirrel.AndroidNoise.events;

// Secret Squirrel Software - Created by bswanson on 1/31/14.

import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.dto.ArtistInfo;

public class EventArtistInfoRequest {
	private final Artist        mArtist;
	private final ArtistInfo    mArtistInfo;

	public EventArtistInfoRequest( Artist artist, ArtistInfo artistInfo ) {
		mArtist = artist;
		mArtistInfo = artistInfo;
	}

	public Artist getArtist() {
		return( mArtist );
	}

	public ArtistInfo getArtistInfo() {
		return( mArtistInfo );
	}
}
