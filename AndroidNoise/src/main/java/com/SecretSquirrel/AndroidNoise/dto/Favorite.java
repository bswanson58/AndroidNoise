package com.SecretSquirrel.AndroidNoise.dto;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.SecretSquirrel.AndroidNoise.services.rto.RoFavorite;

// Created by BSwanson on 12/28/13.

public class Favorite implements Parcelable {
	private long        mArtistId;
	private long        mAlbumId;
	private long        mTrackId;
	private String      mArtist;
	private String      mAlbum;
	private String      mTrack;

	/** Static field used to regenerate object, individually or as arrays */
	public static final Parcelable.Creator<Favorite> CREATOR = new Parcelable.Creator<Favorite>() {
		public Favorite createFromParcel(Parcel parcel) {
			return new Favorite( parcel );
		}
		public Favorite[] newArray( int size ) {
			return new Favorite[size];
		}
	};

	public Favorite( RoFavorite fromFavorite ) {
		mArtistId = fromFavorite.ArtistId;
		mAlbumId = fromFavorite.AlbumId;
		mTrackId = fromFavorite.TrackId;
		mArtist = fromFavorite.Artist;
		mAlbum = fromFavorite.Album;
		mTrack = fromFavorite.Track;
	}

	public Favorite( Parcel parcel ) {
		mArtistId = parcel.readLong();
		mAlbumId = parcel.readLong();
		mTrackId = parcel.readLong();
		mArtist = parcel.readString();
		mAlbum = parcel.readString();
		mTrack = parcel.readString();
	}

	public long getArtistId() {
		return( mArtistId );
	}

	public long getAlbumId() {
		return( mAlbumId );
	}

	public long getTrackId() {
		return( mTrackId );
	}

	public String getArtist() {
		return( mArtist );
	}

	public String getAlbum() {
		return( mAlbum );
	}

	public String getTrack() {
		return( mTrack );
	}

	public String getSortingName() {
		String  retValue = getTrack();

		if( TextUtils.isEmpty( retValue )) {
			retValue = getAlbum();
		}

		if( TextUtils.isEmpty( retValue )) {
			retValue = getArtist();
		}

		return( retValue );
	}

	@Override
	public int describeContents() {
		return( 0 );
	}

	@Override
	public void writeToParcel( Parcel parcel, int i ) {
		parcel.writeLong( mArtistId );
		parcel.writeLong( mAlbumId );
		parcel.writeLong( mTrackId );
		parcel.writeString( mArtist );
		parcel.writeString( mAlbum );
		parcel.writeString( mTrack );
	}
}
