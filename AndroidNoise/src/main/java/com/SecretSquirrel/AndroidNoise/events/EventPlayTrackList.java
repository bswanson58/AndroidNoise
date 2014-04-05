package com.SecretSquirrel.AndroidNoise.events;

// Created by BSwanson on 4/5/14.

public class EventPlayTrackList {
	private long[]  mTrackList;

	public EventPlayTrackList( long[] trackList ) {
		mTrackList = trackList;
	}

	public long[] getTrackList() {
		return( mTrackList );
	}
}
