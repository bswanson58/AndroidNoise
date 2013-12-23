package com.SecretSquirrel.AndroidNoise.events;

// Secret Squirrel Software - Created by bswanson on 12/23/13.

import com.SecretSquirrel.AndroidNoise.dto.Artist;

public class EventArtistSelected {
	private Artist  mArtist;

	public EventArtistSelected( Artist artist ) {
		mArtist = artist;
	}

	public Artist getArtist() {
		return mArtist;
	}
}
