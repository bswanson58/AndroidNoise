package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by bswanson on 12/17/13.

import android.os.Parcel;
import android.os.Parcelable;

import com.SecretSquirrel.AndroidNoise.services.rto.RoTrack;

public class Track implements Parcelable {
	private long        mTrackId;
	private long        mAlbumId;
	private long        mArtistId;
	private String      mName;
	private long        mDurationMilliseconds;
	private int         mRating;
	private int         mTrackNumber;
	private String      mVolumeName;
	private boolean     mIsFavorite;

	/** Static field used to regenerate object, individually or as arrays */
	public static final Parcelable.Creator<Track> CREATOR = new Parcelable.Creator<Track>() {
		public Track createFromParcel(Parcel parcel) {
			return new Track( parcel );
		}
		public Track[] newArray( int size ) {
			return new Track[size];
		}
	};

	public Track( RoTrack fromTrack ) {
		mTrackId = fromTrack.DbId;
		mAlbumId = fromTrack.AlbumId;
		mArtistId = fromTrack.ArtistId;
		mName = fromTrack.Name;
		mDurationMilliseconds = fromTrack.DurationMilliseconds;
		mRating = fromTrack.Rating;
		mTrackNumber = fromTrack.TrackNumber;
		mVolumeName = fromTrack.VolumeName;
		mIsFavorite = fromTrack.IsFavorite;
	}

	public Track( Favorite favorite ) {
		mTrackId = favorite.getTrackId();
		mAlbumId = favorite.getAlbumId();
		mArtistId = favorite.getArtistId();
		mName = favorite.getTrack();
		mIsFavorite = true;
	}

	public Track( SearchResultItem searchItem ) {
		mTrackId = searchItem.getTrackId();
		mAlbumId = searchItem.getAlbumId();
		mArtistId = searchItem.getArtistId();
		mName = searchItem.getTrackName();
	}

	public Track( Parcel parcel ) {
		mTrackId = parcel.readLong();
		mAlbumId = parcel.readLong();
		mArtistId = parcel.readLong();
		mName = parcel.readString();
		mDurationMilliseconds = parcel.readLong();
		mRating = parcel.readInt();
		mTrackNumber = parcel.readInt();
		mVolumeName = parcel.readString();
		mIsFavorite = parcel.readByte() != 0;
	}

	public long getTrackId() {
		return( mTrackId );
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

	public long getDurationMilliseconds() {
		return( mDurationMilliseconds );
	}

	public int getRating() {
		return( mRating );
	}

	public int getTrackNumber() {
		return( mTrackNumber );
	}

	public String getVolumeName() {
		return( mVolumeName );
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
		parcel.writeLong( mTrackId );
		parcel.writeLong( mAlbumId );
		parcel.writeLong( mArtistId );
		parcel.writeString( mName );
		parcel.writeLong( mDurationMilliseconds );
		parcel.writeInt( mRating );
		parcel.writeInt( mTrackNumber );
		parcel.writeString( mVolumeName );
		parcel.writeByte((byte)( mIsFavorite ? 1 : 0));
	}
}
