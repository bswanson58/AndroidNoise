package com.SecretSquirrel.AndroidNoise.dto;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.SecretSquirrel.AndroidNoise.services.rto.RoArtist;

// Secret Squirrel Software - Created by bswanson on 12/6/13.

@SuppressWarnings( "unused" )
public class Artist implements Parcelable {
	private long        mArtistId;
	private String      mName;
	private String      mDisplayName;
	private String      mSortName;
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
		mDisplayName = "";
		mSortName = "";
		mAlbumCount = fromArtist.AlbumCount;
		mRating = fromArtist.Rating;
		mGenre = fromArtist.Genre;
		mIsFavorite = fromArtist.IsFavorite;
	}

	public Artist( Parcel parcel ) {
		mArtistId = parcel.readLong();
		mName = parcel.readString();
		mDisplayName = parcel.readString();
		mSortName = parcel.readString();
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

	public String getDisplayName() {
		return( TextUtils.isEmpty( mDisplayName ) ? mName : mDisplayName );
	}

	public void setDisplayName( String displayName ) {
		mDisplayName = displayName;
	}

	public String getSortName() {
		return( TextUtils.isEmpty( mSortName ) ? mName : mSortName );
	}

	public void setSortName( String sortName ) {
		mSortName = sortName;
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

	public boolean getIsFavorite() {
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
		parcel.writeString( mDisplayName );
		parcel.writeString( mSortName );
		parcel.writeInt( mAlbumCount );
		parcel.writeInt( mRating );
		parcel.writeString( mGenre );
		parcel.writeByte((byte)( mIsFavorite ? 1 : 0));
	}
}
