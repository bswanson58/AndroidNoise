package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by bswanson on 12/30/13.

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import com.SecretSquirrel.AndroidNoise.services.rto.RoAlbumInfo;

public class AlbumInfo implements Parcelable {
	private long    mAlbumId;
	private Bitmap	mAlbumCover;

	/** Static field used to regenerate object, individually or as arrays */
	public static final Parcelable.Creator<AlbumInfo> CREATOR = new Parcelable.Creator<AlbumInfo>() {
		public AlbumInfo createFromParcel( Parcel parcel ) {
			return new AlbumInfo( parcel );
		}
		public AlbumInfo[] newArray( int size ) {
			return new AlbumInfo[size];
		}
	};

	public AlbumInfo( Parcel parcel ) {
		mAlbumId = parcel.readLong();
		mAlbumCover = parcel.readParcelable( Bitmap.class.getClassLoader());
	}

	public AlbumInfo( RoAlbumInfo roAlbumInfo ) {
		mAlbumId = roAlbumInfo.AlbumId;

		if( roAlbumInfo.AlbumCover != null ) {
			byte[] decodedString = Base64.decode( roAlbumInfo.AlbumCover, Base64.DEFAULT );

			mAlbumCover = BitmapFactory.decodeByteArray( decodedString, 0, decodedString.length );
		}
	}

	public Bitmap getAlbumCover() {
		return( mAlbumCover );
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
		parcel.writeLong( mAlbumId );
		parcel.writeValue( mAlbumCover );
	}
}
