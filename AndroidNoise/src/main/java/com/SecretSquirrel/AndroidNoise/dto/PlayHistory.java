package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by BSwanson on 4/7/14.

import android.os.Parcel;
import android.os.Parcelable;

import com.SecretSquirrel.AndroidNoise.services.rto.RoPlayHistory;

public class PlayHistory implements Parcelable {
	private long    mArtistId;
	private long    mAlbumId;
	private long    mTrackId;
	private String  mArtistName;
	private String  mAlbumName;
	private String  mTrackName;
	private long    mPlayOnTicks;

	/** Static field used to regenerate object, individually or as arrays */
	public static final Parcelable.Creator<PlayHistory> CREATOR = new Parcelable.Creator<PlayHistory>() {
		public PlayHistory createFromParcel( Parcel parcel ) {
			return new PlayHistory( parcel );
		}
		public PlayHistory[] newArray( int size ) {
			return new PlayHistory[size];
		}
	};

	public PlayHistory( RoPlayHistory roHistory ) {
		mArtistId = roHistory.ArtistId;
		mAlbumId = roHistory.AlbumId;
		mTrackId = roHistory.TrackId;
		mArtistName = roHistory.ArtistName;
		mAlbumName = roHistory.AlbumName;
		mTrackName = roHistory.TrackName;
		mPlayOnTicks = roHistory.PlayedOnTicks;
	}

	public PlayHistory( Parcel parcel ) {
		mArtistId = parcel.readLong();
		mAlbumId = parcel.readLong();
		mTrackId = parcel.readLong();
		mArtistName = parcel.readString();
		mAlbumName = parcel.readString();
		mTrackName = parcel.readString();
		mPlayOnTicks = parcel.readLong();
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

	public String getArtistName() {
		return( mArtistName );
	}

	public String getAlbumName() {
		return( mAlbumName );
	}

	public String getTrackName() {
		return( mTrackName );
	}

	public long getPlayOnTicks() {
		return( mPlayOnTicks );
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
		parcel.writeString( mArtistName );
		parcel.writeString( mAlbumName );
		parcel.writeString( mTrackName );
		parcel.writeLong( mPlayOnTicks );
	}
}
