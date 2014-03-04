package com.SecretSquirrel.AndroidNoise.dto;// Created by BSwanson on 3/3/14.

import android.os.Parcel;
import android.os.Parcelable;

import com.SecretSquirrel.AndroidNoise.services.rto.RoArtistTrack;
import com.SecretSquirrel.AndroidNoise.services.rto.RoTrackAssociation;

import java.util.ArrayList;
import java.util.List;

public class ArtistTrack implements Parcelable {
	private String                      mTrackName;
	private ArrayList<TrackAssociation> mTracks;

	/** Static field used to regenerate object, individually or as arrays */
	public static final Parcelable.Creator<ArtistTrack> CREATOR = new Parcelable.Creator<ArtistTrack>() {
		public ArtistTrack createFromParcel( Parcel parcel ) {
			return new ArtistTrack( parcel );
		}
		public ArtistTrack[] newArray( int size ) {
			return new ArtistTrack[size];
		}
	};

	public ArtistTrack( RoArtistTrack roTrack ) {
		mTrackName = roTrack.TrackName;
		mTracks = new ArrayList<TrackAssociation>();

		for( RoTrackAssociation track : roTrack.Tracks ) {
			mTracks.add( new TrackAssociation( track ));
		}
	}

	public ArtistTrack( Parcel parcel ) {
		mTrackName = parcel.readString();
		parcel.readTypedList( mTracks, TrackAssociation.CREATOR );
	}

	public String getTrackName() {
		return( mTrackName );
	}

	public List<TrackAssociation> getTracks() {
		return( mTracks );
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel( Parcel parcel, int i ) {
		parcel.writeString( mTrackName );
		parcel.writeTypedList( mTracks );
	}
}
