package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by bswanson on 1/28/14.

import com.SecretSquirrel.AndroidNoise.support.Constants;

public class LibraryFocusArgs {
	private long    mArtistId;
	private long    mAlbumId;

	public LibraryFocusArgs( long artistId ) {
		mArtistId = artistId;
		mAlbumId = Constants.NULL_ID;
	}

	public LibraryFocusArgs( long artistId, long albumId ) {
		this( artistId );
		mAlbumId = albumId;
	}

	public long getArtistId() {
		return( mArtistId );
	}

	public long getAlbumId() {
		return( mAlbumId );
	}

	public boolean getIsAlbumFocusRequest() {
		return( mAlbumId != Constants.NULL_ID );
	}
}
