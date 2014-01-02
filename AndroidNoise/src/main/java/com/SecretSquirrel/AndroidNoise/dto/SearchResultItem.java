package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by bswanson on 12/30/13.

import com.SecretSquirrel.AndroidNoise.services.rto.RoSearchResultItem;

public class SearchResultItem {
	private long    mTrackId;
	private String	mTrackName;
	private long	mAlbumId;
	private String	mAlbumName;
	private long	mArtistId;
	private String	mArtistName;
	private boolean mCanPlay;

	public SearchResultItem( RoSearchResultItem roSearchItem ) {
		mTrackId = roSearchItem.TrackId;
		mTrackName = roSearchItem.TrackName;
		mAlbumId = roSearchItem.AlbumId;
		mAlbumName = roSearchItem.AlbumName;
		mArtistId = roSearchItem.ArtistId;
		mArtistName = roSearchItem.ArtistName;
		mCanPlay = roSearchItem.CanPlay;
	}

	public long getTrackId() {
		return( mTrackId );
	}

	public String getTrackName() {
		return( mTrackName );
	}

	public long getAlbumId() {
		return( mAlbumId );
	}

	public String getAlbumName() {
		return( mAlbumName );
	}

	public long getArtistId() {
		return( mArtistId );
	}

	public String getArtistName() {
		return( mArtistName );
	}

	public boolean isCanPlay() {
		return( mCanPlay );
	}
}