package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by bswanson on 3/4/14.

import android.os.Parcel;
import android.os.Parcelable;

import com.SecretSquirrel.AndroidNoise.services.rto.RoTrackAssociation;

public class TrackAssociation implements Parcelable {
	private long    mTrackId;
	private long    mAlbumId;

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
	}

	public TrackAssociation( Parcel parcel ) {
		mTrackId = parcel.readLong();
		mAlbumId = parcel.readLong();
	}

	public long getTrackId() {
		return( mTrackId );
	}

	public long getAlbumId() {
		return( mAlbumId );
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel( Parcel parcel, int i ) {
		parcel.writeLong( mTrackId );
		parcel.writeLong( mAlbumId );
	}
}
