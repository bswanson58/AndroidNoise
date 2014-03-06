package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by bswanson on 3/4/14.

import android.os.Parcel;
import android.os.Parcelable;

import com.SecretSquirrel.AndroidNoise.services.rto.RoTrackAssociation;

public class TrackAssociation implements Parcelable {
	private long    mTrackId;
	private long    mAlbumId;
	private long    mDuration;
	private int     mTrackNumber;
	private String  mVolumeName;

	/** Static field used to regenerate object, individually or as arrays */
	public static final Parcelable.Creator<TrackAssociation> CREATOR = new Parcelable.Creator<TrackAssociation>() {
		public TrackAssociation createFromParcel( Parcel parcel ) {
			return new TrackAssociation( parcel );
		}
		public TrackAssociation[] newArray( int size ) {
			return new TrackAssociation[size];
		}
	};

	public TrackAssociation( RoTrackAssociation roAssociation ) {
		mTrackId = roAssociation.TrackId;
		mAlbumId = roAssociation.AlbumId;
		mDuration = roAssociation.Duration;
		mTrackNumber = roAssociation.TrackNumber;
		mVolumeName = roAssociation.VolumeName;
	}

	public TrackAssociation( Parcel parcel ) {
		mTrackId = parcel.readLong();
		mAlbumId = parcel.readLong();
		mDuration = parcel.readLong();
		mTrackNumber = parcel.readInt();
		mVolumeName = parcel.readString();
	}

	public long getTrackId() {
		return( mTrackId );
	}

	public long getAlbumId() {
		return( mAlbumId );
	}

	public int getTrackNumber() {
		return( mTrackNumber );
	}

	public String getVolumeName() {
		return( mVolumeName );
	}

	public long getDurationMilliseconds() {
		return( mDuration );
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel( Parcel parcel, int i ) {
		parcel.writeLong( mTrackId );
		parcel.writeLong( mAlbumId );
		parcel.writeLong( mDuration );
		parcel.writeInt( mTrackNumber );
		parcel.writeString( mVolumeName );
	}
}
