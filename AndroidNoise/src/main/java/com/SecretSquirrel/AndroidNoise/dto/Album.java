package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by bswanson on 12/17/13.

import android.os.Parcel;
import android.os.Parcelable;

import com.SecretSquirrel.AndroidNoise.services.rto.RoAlbum;

public class Album implements Parcelable {
	private long        mAlbumId;
	private long        mArtistId;
	private String      mName;
	private int         mTrackCount;
	private int         mRating;
	private long        mPublishedYear;
	private String      mGenre;
	private boolean mIsFavorite;

	/** Static field used to regenerate object, individually or as arrays */
	public static final Parcelable.Creator<Album> CREATOR = new Parcelable.Creator<Album>() {
		public Album createFromParcel(Parcel parcel) {
			return new Album( parcel );
		}
		public Album[] newArray( int size ) {
			return new Album[size];
		}
	};

	public Album( RoAlbum fromAlbum ) {
		mAlbumId = fromAlbum.DbId;
		mArtistId = fromAlbum.ArtistId;
		mName = fromAlbum.Name;
		mTrackCount = fromAlbum.TrackCount;
		mRating = fromAlbum.Rating;
		mPublishedYear = fromAlbum.PublishedYear;
		mGenre = fromAlbum.Genre;
		mIsFavorite = fromAlbum.IsFavorite;
	}

	public Album( Favorite favorite ) {
		mAlbumId = favorite.getAlbumId();
		mArtistId = favorite.getArtistId();
		mName = favorite.getAlbum();
		mIsFavorite = true;
	}

	public Album( Parcel parcel ) {
		mAlbumId = parcel.readLong();
		mArtistId = parcel.readLong();
		mName = parcel.readString();
		mTrackCount = parcel.readInt();
		mRating = parcel.readInt();
		mPublishedYear = parcel.readLong();
		mGenre = parcel.readString();
		mIsFavorite = parcel.readByte() != 0;
	}

	public long getAlbumId() {
		return( mAlbumId );
	}

	public long getArtistId() {
		return( mArtistId );
	}

	public String getName() {
		return( mName );
	}

	public int getTrackCount() {
		return( mTrackCount );
	}

	public int getRating() {
		return( mRating );
	}

	public long getPublishedYear() {
		return( mPublishedYear );
	}

	public String getGenre() {
		return( mGenre );
	}

	public boolean isFavorite() {
		return( mIsFavorite );
	}

	@Override
	public int describeContents() {
		return( 0 );
	}

	@Override
	public void writeToParcel( Parcel parcel, int i ) {
		parcel.writeLong( mAlbumId );
		parcel.writeLong( mArtistId );
		parcel.writeString( mName );
		parcel.writeInt( mTrackCount );
		parcel.writeInt( mRating );
		parcel.writeLong( mPublishedYear );
		parcel.writeString( mGenre );
		parcel.writeByte((byte)( mIsFavorite ? 1 : 0));
	}
}
