package com.SecretSquirrel.AndroidNoise.events;

// Secret Squirrel Software - Created by bswanson on 2/4/14.

import com.SecretSquirrel.AndroidNoise.dto.Artist;

public class EventArtistViewed {
	private Artist      mArtist;

	public EventArtistViewed( Artist artist ) {
		mArtist = artist;
	}

	public Artist getArtist() {
		return( mArtist );
	}
}
