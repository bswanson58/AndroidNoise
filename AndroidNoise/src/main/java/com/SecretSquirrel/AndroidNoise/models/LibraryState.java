package com.SecretSquirrel.AndroidNoise.models;

// Created by BSwanson on 3/18/14.

import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.support.Constants;

import rx.Observable;
import rx.subjects.BehaviorSubject;

public class LibraryState {
	private final BehaviorSubject<Boolean> mOnStateChanged;
	private Artist                          mCurrentArtist;
	private Album                           mCurrentAlbum;

	public LibraryState() {
		mOnStateChanged = BehaviorSubject.create( true );
	}

	public Album getCurrentAlbum() {
		return( mCurrentAlbum );
	}

	public long getCurrentAlbumId() {
		long    retValue = Constants.NULL_ID;

		if( mCurrentAlbum != null ) {
			retValue = mCurrentAlbum.getAlbumId();
		}

		return( retValue );
	}

	public void setCurrentAlbum( Album album ) {
		mCurrentAlbum = album;

		mOnStateChanged.onNext( true );
	}

	public Artist getCurrentArtist() {
		return( mCurrentArtist );
	}

	public void setCurrentArtist( Artist artist ) {
		mCurrentArtist = artist;

		mOnStateChanged.onNext( true );
	}

	public Observable<Boolean> getStateChange() {
		return( mOnStateChanged );
	}
}
