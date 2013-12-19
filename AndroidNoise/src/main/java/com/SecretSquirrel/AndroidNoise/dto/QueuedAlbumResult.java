package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by bswanson on 12/19/13.

import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;

public class QueuedAlbumResult extends BaseServerResult {
	private Album   mAlbum;

	public QueuedAlbumResult( Album album, BaseServerResult serverResult ) {
		super( serverResult );
		mAlbum = album;
	}

	public Album getAlbum() {
		return( mAlbum );
	}
}
