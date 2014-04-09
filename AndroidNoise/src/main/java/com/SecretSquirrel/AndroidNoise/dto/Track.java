package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by bswanson on 12/17/13.

import android.os.Parcel;
import android.os.Parcelable;

import com.SecretSquirrel.AndroidNoise.services.rto.RoTrack;

public class Track implements Parcelable {
	private long        mTrackId;
	private long        mAlbumId;
	private long        mArtistId;
	private String      mTrackName;
	private String      mAlbumName;
	private String      mArtistName;
	private long        mDuration;
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
		mTrackId = fromTrack.TrackId;
		mAlbumId = fromTrack.AlbumId;
		mArtistId = fromTrack.ArtistId;
		mTrackName = fromTrack.TrackName;
		mAlbumName = fromTrack.AlbumName;
		mArtistName = fromTrack.ArtistName;
		mDuration = fromTrack.Duration;
		mRating = fromTrack.Rating;
		mTrackNumber = fromTrack.TrackNumber;
		mVolumeName = fromTrack.VolumeName;
		mIsFavorite = fromTrack.IsFavorite;
	}

	public Track( Favorite favorite ) {
		mTrackId = favorite.getTrackId();
		mAlbumId = favorite.getAlbumId();
		mArtistId = favorite.getArtistId();
		mTrackName = favorite.getTrack();
		mArtistName = favorite.getArtist();
		mAlbumName = favorite.getAlbum();
		mIsFavorite = true;
	}

	public Track( SearchResultItem searchItem ) {
		mTrackId = searchItem.getTrackId();
		mAlbumId = searchItem.getAlbumId();
		mArtistId = searchItem.getArtistId();
		mTrackName = searchItem.getTrackName();
		mAlbumName = searchItem.getAlbumName();
		mArtistName = searchItem.getArtistName();
	}

	public Track( Parcel parcel ) {
		mTrackId = parcel.readLong();
		mAlbumId = parcel.readLong();
		mArtistId = parcel.readLong();
		mTrackName = parcel.readString();
		mAlbumName = parcel.readString();
		mArtistName = parcel.readString();
		mDuration = parcel.readLong();
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

	public String getTrackName() {
		return( mTrackName );
	}

	public String getAlbumName() {
		return( mAlbumName );
	}

	public String getArtistName() {
		return( mArtistName );
	}

	public long getDurationMilliseconds() {
		return( mDuration );
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

	public boolean getIsFavorite() {
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
		parcel.writeString( mTrackName );
		parcel.writeString( mAlbumName );
		parcel.writeString( mArtistName );
		parcel.writeLong( mDuration );
		parcel.writeInt( mRating );
		parcel.writeInt( mTrackNumber );
		parcel.writeString( mVolumeName );
		parcel.writeByte((byte)( mIsFavorite ? 1 : 0));
	}
}
