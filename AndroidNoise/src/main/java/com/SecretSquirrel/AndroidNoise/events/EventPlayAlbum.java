package com.SecretSquirrel.AndroidNoise.events;

// Secret Squirrel Software - Created by bswanson on 12/23/13.

import com.SecretSquirrel.AndroidNoise.dto.Album;

public class EventPlayAlbum {
	private Album   mAlbum;

	public EventPlayAlbum( Album album ) {
		mAlbum = album;
	}

	public Album getAlbum() {
		return mAlbum;
	}
}