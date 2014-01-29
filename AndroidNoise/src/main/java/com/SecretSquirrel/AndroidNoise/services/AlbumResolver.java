package com.SecretSquirrel.AndroidNoise.services;

import android.os.Bundle;
import android.os.Handler;

import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;

import java.util.ArrayList;

// Created by BSwanson on 1/28/14.

public class AlbumResolver implements ServiceResultReceiver.Receiver {
	private ServiceResultReceiver           mServiceResultReceiver;
	private ServiceResultReceiver.Receiver  mClientReceiver;
	private INoiseData                      mNoiseData;
	private long                            mAlbumId;

	public AlbumResolver( INoiseData data ) {
		mNoiseData = data;

		mServiceResultReceiver = new ServiceResultReceiver( new Handler());
		mServiceResultReceiver.setReceiver( this );
	}

	public void requestAlbum( long albumId, final long forArtistId, ServiceResultReceiver.Receiver receiver ) {
		mAlbumId = albumId;
		mClientReceiver = receiver;

		mNoiseData.GetAlbumList( forArtistId, mServiceResultReceiver );
	}

	@Override
	public void onReceiveResult( int resultCode, Bundle resultData ) {
		if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
			ArrayList<Album> albumList = resultData.getParcelableArrayList( NoiseRemoteApi.AlbumList );

			if( albumList != null ) {
				for( Album album : albumList ) {
					if( album.getAlbumId() == mAlbumId ) {
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
