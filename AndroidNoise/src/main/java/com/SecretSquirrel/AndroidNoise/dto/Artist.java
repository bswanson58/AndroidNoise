package com.SecretSquirrel.AndroidNoise.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.SecretSquirrel.AndroidNoise.services.rto.RoArtist;

// Secret Squirrel Software - Created by bswanson on 12/6/13.

public class Artist implements Parcelable {
	private long        mArtistId;
	private String      mName;
	private int         mAlbumCount;
	private int         mRating;
	private String      mGenre;
	private boolean     mIsFavorite;

	/** Static field used to regenerate object, individually or as arrays */
	public static final Parcelable.Creator<Artist> CREATOR = new Parcelable.Creator<Artist>() {
		public Artist createFromParcel(Parcel parcel) {
			return new Artist( parcel );
		}
		public Artist[] newArray( int size ) {
			return new Artist[size];
		}
	};

	public Artist( RoArtist fromArtist ) {
		mArtistId = fromArtist.DbId;
		mName = fromArtist.Name;
		mAlbumCount = fromArtist.AlbumCount;
		mRating = fromArtist.Rating;
		mGenre = fromArtist.Genre;
		mIsFavorite = fromArtist.IsFavorite;
	}

	public Artist( Parcel parcel ) {
		mArtistId = parcel.readLong();
		mName = parcel.readString();
		mAlbumCount = parcel.readInt();
		mRating = parcel.readInt();
		mGenre = parcel.readString();
		mIsFavorite = parcel.readByte() != 0;
	}

	public long getArtistId() {
		return( mArtistId );
	}

	public String getName() {
		return( mName );
	}

	public int getAlbumCount() {
		return( mAlbumCount );
	}

	public int getRating() {
		return( mRating );
	}

	public String getGenre() {
		return( mGenre );
	}

	public boolean isFavorite() {
		return( mIsFavorite );
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel( Parcel parcel, int i ) {
		parcel.writeLong( mArtistId );
		parcel.writeString( mName );
		parcel.writeInt( mAlbumCount );
		parcel.writeInt( mRating );
		parcel.writeString( mGenre );
		parcel.writeByte((byte)( mIsFavorite ? 1 : 0));
	}
}
