package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by bswanson on 12/17/13.

import android.os.Parcel;
import android.os.Parcelable;

import com.SecretSquirrel.AndroidNoise.services.rto.RoTrack;

public class Track implements Parcelable {
	public long         TrackId;
	public long			AlbumId;
	public long			ArtistId;
	public String		Name;
	public long			DurationMilliseconds;
	public int			Rating;
	public int			TrackNumber;
	public String		VolumeName;
	public boolean		IsFavorite;

	/** Static field used to regenerate object, individually or as arrays */
	public static final Parcelable.Creator<Track> CREATOR = new Parcelable.Creator<Track>() {
		public Track createFromParcel(Parcel parcel) {
			return new Track( parcel );
		}
		public Track[] newArray( int size ) {
			return new Track[size];
		}
	};

	public Track( RoTrack fromTrack ) {
		TrackId = fromTrack.DbId;
		AlbumId = fromTrack.AlbumId;
		ArtistId = fromTrack.ArtistId;
		Name = fromTrack.Name;
		DurationMilliseconds = fromTrack.DurationMilliseconds;
		Rating = fromTrack.Rating;
		TrackNumber = fromTrack.TrackNumber;
		VolumeName = fromTrack.VolumeName;
		IsFavorite = fromTrack.IsFavorite;
	}

	public Track( Parcel parcel ) {
		TrackId = parcel.readLong();
		AlbumId = parcel.readLong();
		ArtistId = parcel.readLong();
		Name = parcel.readString();
		DurationMilliseconds = parcel.readLong();
		Rating = parcel.readInt();
		TrackNumber = parcel.readInt();
		VolumeName = parcel.readString();
		IsFavorite = parcel.readByte() != 0;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel( Parcel parcel, int i ) {
		parcel.writeLong( TrackId );
		parcel.writeLong( AlbumId );
		parcel.writeLong( ArtistId );
		parcel.writeString( Name );
		parcel.writeLong( DurationMilliseconds );
		parcel.writeInt( Rating );
		parcel.writeInt( TrackNumber );
		parcel.writeString( VolumeName );
		parcel.writeByte((byte)( IsFavorite ? 1 : 0));
	}
}
