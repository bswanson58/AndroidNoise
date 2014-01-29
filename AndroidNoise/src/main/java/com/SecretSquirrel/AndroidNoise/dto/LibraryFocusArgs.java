package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by bswanson on 1/28/14.

import com.SecretSquirrel.AndroidNoise.support.Constants;

public class LibraryFocusArgs {
	private Artist  mArtist;
	private Album   mAlbum;

	public LibraryFocusArgs( Artist artist ) {
		mArtist = artist;
	}

	public LibraryFocusArgs( Artist artist, Album album ) {
		this( artist );
		mAlbum = album;
	}

	public Artist getArtist() {
		return( mArtist );
	}

	public Album getAlbum() {
		return( mAlbum );
	}

	public boolean getIsAlbumFocusRequest() {
		return( mAlbum != null );
	}
}
