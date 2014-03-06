package com.SecretSquirrel.AndroidNoise.events;

// Created by BSwanson on 3/5/14.

import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.dto.ArtistTrack;

public class EventArtistTrackAlbumRequest {
	private final ArtistTrack   mArtistTrack;
	private final Artist        mArtist;

	public EventArtistTrackAlbumRequest() {
		mArtist = null;
		mArtistTrack = null;
	}

	public EventArtistTrackAlbumRequest( Artist artist, ArtistTrack track ) {
		mArtist = artist;
		mArtistTrack = track;
	}

	public Artist getArtist() {
		return( mArtist );
	}

	public ArtistTrack getTrack() {
		return( mArtistTrack );
	}

	public boolean getDisplayView() {
		return( mArtist != null );
	}
}
