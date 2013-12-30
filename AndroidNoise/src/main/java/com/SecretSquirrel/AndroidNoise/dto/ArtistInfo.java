package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by bswanson on 12/30/13.

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import com.SecretSquirrel.AndroidNoise.services.rto.RoArtistInfo;

public class ArtistInfo implements Parcelable {
	private long        mArtistId;
	private String		mWebsite;
	private String		mBiography;
	private Bitmap		mArtistImage;
	private String[]	mBandMembers;
	private String[]	mTopAlbums;
	private String[]	mSimilarArtists;

	/** Static field used to regenerate object, individually or as arrays */
	public static final Parcelable.Creator<ArtistInfo> CREATOR = new Parcelable.Creator<ArtistInfo>() {
		public ArtistInfo createFromParcel( Parcel parcel ) {
			return new ArtistInfo( parcel );
		}
		public ArtistInfo[] newArray( int size ) {
			return new ArtistInfo[size];
		}
	};

	public ArtistInfo( RoArtistInfo roArtistInfo ) {
		mArtistId = roArtistInfo.ArtistId;
		mBiography = roArtistInfo.Biography;
		mWebsite = roArtistInfo.Website;
		mBandMembers = roArtistInfo.BandMembers;
		mTopAlbums = roArtistInfo.TopAlbums;
		mSimilarArtists = roArtistInfo.SimilarArtists;

		byte[] decodedString = Base64.decode( roArtistInfo.ArtistImage, Base64.DEFAULT );
		mArtistImage = BitmapFactory.decodeByteArray( decodedString, 0, decodedString.length );
	}

	public ArtistInfo( Parcel parcel ) {
		mArtistId = parcel.readLong();
		mWebsite = parcel.readString();
		mBiography = parcel.readString();
		mBandMembers = parcel.createStringArray();
		mTopAlbums = parcel.createStringArray();
		mSimilarArtists = parcel.createStringArray();
		mArtistImage = parcel.readParcelable( Bitmap.class.getClassLoader());
	}

	public long getArtistId() {
		return( mArtistId );
	}

	public String getWebsite() {
		return( mWebsite );
	}

	public String getBiography() {
		return( mBiography );
	}

	public Bitmap getArtistImage() {
		return( mArtistImage );
	}

	public String[] getBandMembers() {
		return( mBandMembers );
	}

	public String[] getTopAlbums() {
		return( mTopAlbums );
	}

	public String[] getSimilarArtists() {
		return( mSimilarArtists );
	}
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel( Parcel parcel, int i ) {
		parcel.writeLong( mArtistId );
		parcel.writeString( mWebsite );
		parcel.writeString( mBiography );
		parcel.writeStringArray( mBandMembers );
		parcel.writeStringArray( mTopAlbums );
		parcel.writeStringArray( mSimilarArtists );
		parcel.writeValue( mArtistImage );
	}
}
