package com.SecretSquirrel.AndroidNoise.dto;// Created by BSwanson on 3/3/14.

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ArtistTrack implements Parcelable {
	private long    mTrackId;
	private String  mTrackName;
	private long[]  mAlbums;

	/** Static field used to regenerate object, individually or as arrays */
	public static final Parcelable.Creator<ArtistTrack> CREATOR = new Parcelable.Creator<ArtistTrack>() {
		public ArtistTrack createFromParcel( Parcel parcel ) {
			return new ArtistTrack( parcel );
		}
		public ArtistTrack[] newArray( int size ) {
			return new ArtistTrack[size];
		}
	};

	public ArtistTrack( Parcel parcel ) {

	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel( Parcel parcel, int i ) {
	}
}
