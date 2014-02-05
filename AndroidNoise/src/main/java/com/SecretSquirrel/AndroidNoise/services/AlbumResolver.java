package com.SecretSquirrel.AndroidNoise.services;

import android.os.Bundle;
import android.os.Handler;

import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;
import com.SecretSquirrel.AndroidNoise.support.Constants;

import java.util.ArrayList;

// Created by BSwanson on 1/28/14.

public class AlbumResolver implements ServiceResultReceiver.Receiver {
	private ServiceResultReceiver           mServiceResultReceiver;
	private ServiceResultReceiver.Receiver  mClientReceiver;
	private INoiseData                      mNoiseData;
	private long                            mAlbumId;
	private String                          mAlbumName;

	public AlbumResolver( INoiseData data ) {
		mNoiseData = data;
		mAlbumId = Constants.NULL_ID;

		mServiceResultReceiver = new ServiceResultReceiver( new Handler());
		mServiceResultReceiver.setReceiver( this );
	}

	public void requestAlbum( long albumId, final long forArtistId, ServiceResultReceiver.Receiver receiver ) {
		mAlbumId = albumId;
		mClientReceiver = receiver;

		mNoiseData.GetAlbumList( forArtistId, mServiceResultReceiver );
	}

	public void requestAlbum( String albumName, final long forArtistId, ServiceResultReceiver.Receiver receiver ) {
		mAlbumName = albumName;
		mClientReceiver = receiver;

		mNoiseData.GetAlbumList( forArtistId, mServiceResultReceiver );
	}

	private boolean determineMatch( Album album ) {
		boolean retValue;

		if( mAlbumId != Constants.NULL_ID ) {
			retValue = album.getAlbumId() == mAlbumId;
		}
		else {
			retValue = album.getName().equals( mAlbumName );
		}

		return( retValue );
	}

	@Override
	public void onReceiveResult( int resultCode, Bundle resultData ) {
		if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
			ArrayList<Album> albumList = resultData.getParcelableArrayList( NoiseRemoteApi.AlbumList );

			if( albumList != null ) {
				for( Album album : albumList ) {
					if( determineMatch( album )) {
						Bundle  albumData = new Bundle();

						albumData.putParcelable( NoiseRemoteApi.Album, album );
						mClientReceiver.onReceiveResult( NoiseRemoteApi.RemoteResultSuccess, albumData );

						break;
					}
				}
			}
			else {
				mClientReceiver.onReceiveResult( NoiseRemoteApi.RemoteResultError, null );
			}
		}
		else {
			mClientReceiver.onReceiveResult( resultCode, resultData );
		}
	}
}
