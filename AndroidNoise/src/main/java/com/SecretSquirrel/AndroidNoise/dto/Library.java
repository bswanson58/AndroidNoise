package com.SecretSquirrel.AndroidNoise.dto;

// Created by BSwanson on 3/6/14.

import com.SecretSquirrel.AndroidNoise.services.rto.RoLibrary;

public class Library {
	private long    mLibraryId;
	private String  mLibraryName;
	private String  mDatabaseName;
	private String  mMediaLocation;
	private boolean mIsDefaultLibrary;

	public Library( RoLibrary library ) {
		mLibraryId = library.LibraryId;
		mLibraryName = library.LibraryName;
		mDatabaseName = library.DatabaseName;
		mMediaLocation = library.MediaLocation;
		mIsDefaultLibrary = library.IsDefaultLibrary;
	}

	public long getLibraryId() {
		return( mLibraryId );
	}

	public String getLibraryName() {
		return( mLibraryName );
	}

	public String getDatabaseName() {
		return( mDatabaseName );
	}

	public String getMediaLocation() {
		return( mMediaLocation );
	}

	public boolean isIsDefaultLibrary() {
		return( mIsDefaultLibrary );
	}

	public RoLibrary asRoLibrary() {
		RoLibrary   retValue = new RoLibrary();

		retValue.LibraryId = mLibraryId;
		retValue.LibraryName = mLibraryName;
		retValue.DatabaseName = mDatabaseName;
		retValue.MediaLocation = mMediaLocation;
		retValue.IsDefaultLibrary = mIsDefaultLibrary;

		return( retValue );
	}
}
