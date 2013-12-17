package com.SecretSquirrel.AndroidNoise.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.SecretSquirrel.AndroidNoise.services.rto.RoArtist;
import com.SecretSquirrel.AndroidNoise.services.rto.RoServerVersion;

// Secret Squirrel Software - Created by bswanson on 12/6/13.

public class Artist implements Parcelable {
	public long     ArtistId;
	public String   Name;
	public int      AlbumCount;
	public int      Rating;
	public String   Genre;
	public boolean  IsFavorite;

	/** Static field used to regenerate object, individually or as arrays */
	public static final Parcelable.Creator<Artist> CREATOR = new Parcelable.Creator<Artist>() {
		public Artist createFromParcel(Parcel parcel) {
			return new Artist( parcel );
		}
		public Artist[] newArray( int size ) {
			return new Artist[size];
		}
	};

	public Artist( RoArtist fromArtist ) {
		ArtistId = fromArtist.DbId;
		Name = fromArtist.Name;
		AlbumCount = fromArtist.AlbumCount;
		Rating = fromArtist.Rating;
		Genre = fromArtist.Genre;
		IsFavorite = fromArtist.IsFavorite;
	}

	public Artist( Parcel parcel ) {
		ArtistId = parcel.readLong();
		Name = parcel.readString();
		AlbumCount = parcel.readInt();
		Rating = parcel.readInt();
		Genre = parcel.readString();
		IsFavorite = parcel.readByte() != 0;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel( Parcel parcel, int i ) {
		parcel.writeLong( ArtistId );
		parcel.writeString( Name );
		parcel.writeInt( AlbumCount );
		parcel.writeInt( Rating );
		parcel.writeString( Genre );
		parcel.writeByte((byte)( IsFavorite ? 1 : 0));
	}
}
