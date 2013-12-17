package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by bswanson on 12/17/13.

import android.os.Parcel;
import android.os.Parcelable;

import com.SecretSquirrel.AndroidNoise.services.rto.RoAlbum;

public class Album implements Parcelable {
	public long         AlbumId;
	public long			ArtistId;
	public String       Name;
	public int			TrackCount;
	public int			Rating;
	public long     	PublishedYear;
	public String		Genre;
	public boolean		IsFavorite;

	/** Static field used to regenerate object, individually or as arrays */
	public static final Parcelable.Creator<Album> CREATOR = new Parcelable.Creator<Album>() {
		public Album createFromParcel(Parcel parcel) {
			return new Album( parcel );
		}
		public Album[] newArray( int size ) {
			return new Album[size];
		}
	};

	public Album( RoAlbum fromAlbum ) {
		AlbumId = fromAlbum.DbId;
		ArtistId = fromAlbum.ArtistId;
		Name = fromAlbum.Name;
		TrackCount = fromAlbum.TrackCount;
		Rating = fromAlbum.Rating;
		PublishedYear = fromAlbum.PublishedYear;
		Genre = fromAlbum.Genre;
		IsFavorite = fromAlbum.IsFavorite;
	}

	public Album( Parcel parcel ) {
		AlbumId = parcel.readLong();
		ArtistId = parcel.readLong();
		Name = parcel.readString();
		TrackCount = parcel.readInt();
		Rating = parcel.readInt();
		PublishedYear = parcel.readLong();
		Genre = parcel.readString();
		IsFavorite = parcel.readByte() != 0;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel( Parcel parcel, int i ) {
		parcel.writeLong( AlbumId );
		parcel.writeLong( ArtistId );
		parcel.writeString( Name );
		parcel.writeInt( TrackCount );
		parcel.writeInt( Rating );
		parcel.writeLong( PublishedYear );
		parcel.writeString( Genre );
		parcel.writeByte((byte)( IsFavorite ? 1 : 0));
	}
}
