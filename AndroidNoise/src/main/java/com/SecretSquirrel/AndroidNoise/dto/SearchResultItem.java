package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by bswanson on 12/30/13.

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.SecretSquirrel.AndroidNoise.services.rto.RoSearchResultItem;
import com.SecretSquirrel.AndroidNoise.support.Constants;

public class SearchResultItem implements Parcelable {
	private long    mTrackId;
	private String	mTrackName;
	private long	mAlbumId;
	private String	mAlbumName;
	private long	mArtistId;
	private String	mArtistName;
	private boolean mCanPlay;

	/** Static field used to regenerate object, individually or as arrays */
	public static final Parcelable.Creator<SearchResultItem> CREATOR = new Parcelable.Creator<SearchResultItem>() {
		public SearchResultItem createFromParcel( Parcel parcel ) {
			return new SearchResultItem( parcel );
		}
		public SearchResultItem[] newArray( int size ) {
			return new SearchResultItem[size];
		}
	};

	public SearchResultItem( RoSearchResultItem roSearchItem ) {
		mTrackId = roSearchItem.TrackId;
		mTrackName = roSearchItem.TrackName;
		mAlbumId = roSearchItem.AlbumId;
		mAlbumName = roSearchItem.AlbumName;
		mArtistId = roSearchItem.ArtistId;
		mArtistName = roSearchItem.ArtistName;
		mCanPlay = roSearchItem.CanPlay;
	}

	public SearchResultItem( Parcel parcel ) {
		mTrackId = parcel.readLong();
		mTrackName = parcel.readString();
		mAlbumId = parcel.readLong();
		mAlbumName = parcel.readString();
		mArtistId = parcel.readLong();
		mArtistName = parcel.readString();
		mCanPlay = parcel.readByte() != 0;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel( Parcel parcel, int i ) {
		parcel.writeLong( mTrackId );
		parcel.writeString( mTrackName );
		parcel.writeLong( mAlbumId );
		parcel.writeString( mAlbumName );
		parcel.writeLong( mArtistId );
		parcel.writeString( mArtistName );
		parcel.writeByte((byte)( mCanPlay ? 1 : 0));
	}

	public long getTrackId() {
		return( mTrackId );
	}

	public String getTrackName() {
		return( mTrackName );
	}

	public long getAlbumId() {
		return( mAlbumId );
	}

	public String getAlbumName() {
		return( mAlbumName );
	}

	public long getArtistId() {
		return( mArtistId );
	}

	public String getArtistName() {
		return( mArtistName );
	}

	public boolean getCanPlay() {
		return( mCanPlay );
	}

	public String getItemTitle() {
		String  retValue = getTrackName();

		if( TextUtils.isEmpty( retValue )) {
			retValue = getAlbumName();
		}

		if( TextUtils.isEmpty( retValue )) {
			retValue = getArtistName();
		}

		return( retValue );
	}

	public String getItemSubTitle() {
		String  retValue = "";

		if(!TextUtils.isEmpty( getTrackName())) {
			retValue = String.format( "(%s/%s)", getArtistName(), getAlbumName());
		}
		else if(!TextUtils.isEmpty( getAlbumName())) {
			retValue = String.format( "(%s)", getArtistName());
		}

		return( retValue );
	}

	public boolean getIsArtist() {
		return(( getAlbumId() == Constants.NULL_ID ) &&
				( getTrackId() == Constants.NULL_ID ));
	}

	public boolean getIsAlbum() {
		return(( getTrackId() == Constants.NULL_ID ) &&
				( getAlbumId() != Constants.NULL_ID ));
	}

	public boolean getIsTrack() {
		return( getTrackId() != Constants.NULL_ID );
	}

}
