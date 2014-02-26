package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by bswanson on 12/30/13.

import android.os.Parcel;
import android.os.Parcelable;

import com.SecretSquirrel.AndroidNoise.services.rto.RoPlayQueueTrack;

public class PlayQueueTrack implements Parcelable {
	private long        mTrackId;
	private String	    mTrackName;
	private long        mAlbumId;
	private String	    mAlbumName;
	private long        mArtistId;
	private String	    mArtistName;
	private int         mDurationMilliseconds;
	private boolean		mIsPlaying;
	private boolean		mHasPlayed;
	private boolean		mIsFaulted;
	private boolean		mIsStrategySourced;

	/** Static field used to regenerate object, individually or as arrays */
	public static final Parcelable.Creator<PlayQueueTrack> CREATOR = new Parcelable.Creator<PlayQueueTrack>() {
		public PlayQueueTrack createFromParcel( Parcel parcel ) {
			return new PlayQueueTrack( parcel );
		}
		public PlayQueueTrack[] newArray( int size ) {
			return new PlayQueueTrack[size];
		}
	};

	public PlayQueueTrack( RoPlayQueueTrack roTrack ) {
		mTrackId = roTrack.TrackId;
		mTrackName = roTrack.TrackName;
		mAlbumId = roTrack.AlbumId;
		mAlbumName = roTrack.AlbumName;
		mArtistId = roTrack.ArtistId;
		mArtistName = roTrack.ArtistName;
		mDurationMilliseconds = roTrack.DurationMilliseconds;
		mIsPlaying = roTrack.IsPlaying;
		mHasPlayed = roTrack.HasPlayed;
		mIsFaulted = roTrack.IsFaulted;
		mIsStrategySourced = roTrack.IsStrategySourced;
	}

	public PlayQueueTrack( Parcel parcel ) {
		mTrackId = parcel.readLong();
		mTrackName = parcel.readString();
		mAlbumId = parcel.readLong();
		mAlbumName = parcel.readString();
		mArtistId = parcel.readLong();
		mArtistName = parcel.readString();
		mDurationMilliseconds = parcel.readInt();
		mIsPlaying = parcel.readByte() != 0;
		mHasPlayed = parcel.readByte() != 0;
		mIsFaulted = parcel.readByte() != 0;
		mIsStrategySourced = parcel.readByte() != 0;	}

	public String getTrackName() {
		return( mTrackName );
	}

	public String getAlbumName() {
		return( mAlbumName );
	}

	public String getArtistName() {
		return( mArtistName );
	}

	public int getDurationMilliseconds() {
		return( mDurationMilliseconds );
	}

	public boolean isPlaying() {
		return( mIsPlaying );
	}

	public boolean getHasPlayed() {
		return( mHasPlayed );
	}

	public boolean getIsFaulted() {
		return( mIsFaulted );
	}

	public boolean getIsStrategySourced() {
		return( mIsStrategySourced );
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

	@Override
	public int describeContents() {
		return( 0 );
	}

	@Override
	public void writeToParcel( Parcel parcel, int i ) {
		parcel.writeLong( mTrackId );
		parcel.writeString( mTrackName );
		parcel.writeLong( mAlbumId );
		parcel.writeString( mAlbumName );
		parcel.writeLong( mArtistId );
		parcel.writeString( mArtistName );
		parcel.writeInt( mDurationMilliseconds );
		parcel.writeByte((byte)( mIsPlaying ? 1 : 0));
		parcel.writeByte((byte)( mHasPlayed ? 1 : 0));
		parcel.writeByte((byte)( mIsFaulted ? 1 : 0));
		parcel.writeByte((byte)( mIsStrategySourced ? 1 : 0));
	}
}
