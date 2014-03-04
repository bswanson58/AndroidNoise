package com.SecretSquirrel.AndroidNoise.dto;// Created by BSwanson on 3/3/14.

import android.os.Parcel;
import android.os.Parcelable;

import com.SecretSquirrel.AndroidNoise.services.rto.RoArtistTrack;
import com.SecretSquirrel.AndroidNoise.services.rto.RoArtistTracksResult;

import java.util.ArrayList;
import java.util.List;

public class ArtistTrackList implements Parcelable {
	private long                    mArtistId;
	private long                    mAlbumCount;
	private ArrayList<ArtistTrack>  mTracks;

	/** Static field used to regenerate object, individually or as arrays */
	public static final Parcelable.Creator<ArtistTrackList> CREATOR = new Parcelable.Creator<ArtistTrackList>() {
		public ArtistTrackList createFromParcel(Parcel parcel) {
			return new ArtistTrackList( parcel );
		}
		public ArtistTrackList[] newArray( int size ) {
			return new ArtistTrackList[size];
		}
	};

	public ArtistTrackList( RoArtistTracksResult roTrackList ) {
		mArtistId = roTrackList.ArtistId;
		mAlbumCount = roTrackList.AlbumCount;
		mTracks = new ArrayList<ArtistTrack>();

		for( RoArtistTrack track : roTrackList.Tracks ) {
			mTracks.add( new ArtistTrack( track ));
		}
	}

	public ArtistTrackList( Parcel parcel ) {
		mArtistId = parcel.readLong();
		mAlbumCount = parcel.readLong();
		mTracks = new ArrayList<ArtistTrack>();
		parcel.readTypedList( mTracks, ArtistTrack.CREATOR );
	}

	public long getArtistId() {
		return( mArtistId );
	}

	public long getAlbumCount() {
		return( mAlbumCount );
	}

	public List<ArtistTrack> getTracks() {
		return( mTracks );
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel( Parcel parcel, int i ) {
		parcel.writeLong( mArtistId );
		parcel.writeLong( mAlbumCount );
		parcel.writeTypedList( mTracks );
	}
}
