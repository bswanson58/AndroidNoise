package com.SecretSquirrel.AndroidNoise.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.SecretSquirrel.AndroidNoise.services.rto.RoFavorite;

// Created by BSwanson on 12/28/13.

public class Favorite implements Parcelable {
	public long			ArtistId;
	public long			AlbumId;
	public long			TrackId;
	public String		Artist;
	public String		Album;
	public String		Track;

	/** Static field used to regenerate object, individually or as arrays */
	public static final Parcelable.Creator<Favorite> CREATOR = new Parcelable.Creator<Favorite>() {
		public Favorite createFromParcel(Parcel parcel) {
			return new Favorite( parcel );
		}
		public Favorite[] newArray( int size ) {
			return new Favorite[size];
		}
	};

	public Favorite( RoFavorite fromFavorite ) {
		ArtistId = fromFavorite.ArtistId;
		AlbumId = fromFavorite.AlbumId;
		TrackId = fromFavorite.TrackId;
		Artist = fromFavorite.Artist;
		Album = fromFavorite.Album;
		Track = fromFavorite.Track;
	}

	public Favorite( Parcel parcel ) {
		ArtistId = parcel.readLong();
		AlbumId = parcel.readLong();
		TrackId = parcel.readLong();
		Artist = parcel.readString();
		Album = parcel.readString();
		Track = parcel.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel( Parcel parcel, int i ) {
		parcel.writeLong( ArtistId );
		parcel.writeLong( AlbumId );
		parcel.writeLong( TrackId );
		parcel.writeString( Artist );
		parcel.writeString( Album );
		parcel.writeString( Track );
	}
}
