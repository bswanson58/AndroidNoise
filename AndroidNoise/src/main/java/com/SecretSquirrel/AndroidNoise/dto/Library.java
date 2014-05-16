package com.SecretSquirrel.AndroidNoise.dto;

// Created by BSwanson on 3/6/14.

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.appcompat.R;

import com.SecretSquirrel.AndroidNoise.services.rto.RoLibrary;
import com.SecretSquirrel.AndroidNoise.support.Constants;

public class Library implements Parcelable {
	private long    mLibraryId;
	private String  mLibraryName;
	private String  mDatabaseName;
	private String  mMediaLocation;
	private boolean mIsDefaultLibrary;

	/** Static field used to regenerate object, individually or as arrays */
	public static final Parcelable.Creator<Library> CREATOR = new Parcelable.Creator<Library>() {
		public Library createFromParcel( Parcel parcel ) {
			return new Library( parcel );
		}
		public Library[] newArray(int size) {
			return new Library[size];
		}
	};

	public Library() {
		mLibraryId = Constants.NULL_ID;
		mLibraryName = "";
		mDatabaseName = "";
		mMediaLocation = "";
		mIsDefaultLibrary = false;
	}

	public Library( RoLibrary library ) {
		mLibraryId = library.LibraryId;
		mLibraryName = library.LibraryName;
		mDatabaseName = library.DatabaseName;
		mMediaLocation = library.MediaLocation;
		mIsDefaultLibrary = library.IsDefaultLibrary;
	}

	public Library( Parcel parcel ) {
		mLibraryId = parcel.readLong();
		mLibraryName = parcel.readString();
		mDatabaseName = parcel.readString();
		mMediaLocation = parcel.readString();
		mIsDefaultLibrary = parcel.readByte() != 0;
	}

	@Override
	public void writeToParcel( Parcel parcel, int i ) {
		parcel.writeLong( mLibraryId );
		parcel.writeString( mLibraryName );
		parcel.writeString( mDatabaseName );
		parcel.writeString( mMediaLocation );
		parcel.writeByte((byte)( mIsDefaultLibrary ? 1 : 0));
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public long getLibraryId() {
		return( mLibraryId );
	}

	public String getLibraryName() {
		return( mLibraryName );
	}

	public void setLibraryName( String libraryName ) {
		mLibraryName = libraryName;
	}

	public String getDatabaseName() {
		return( mDatabaseName );
	}

	public void setDatabaseName( String databaseName ) {
		mDatabaseName = databaseName;
	}

	public String getMediaLocation() {
		return( mMediaLocation );
	}

	public void setMediaLocation( String mediaLocation ) {
		mMediaLocation = mediaLocation;
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
