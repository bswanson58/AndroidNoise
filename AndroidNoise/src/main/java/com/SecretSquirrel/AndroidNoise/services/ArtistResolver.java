package com.SecretSquirrel.AndroidNoise.services;

import android.os.Bundle;
import android.os.Handler;

import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;

import java.util.ArrayList;

// Created by BSwanson on 1/28/14.

public class ArtistResolver implements ServiceResultReceiver.Receiver {
	private ServiceResultReceiver           mServiceResultReceiver;
	private ServiceResultReceiver.Receiver  mClientReceiver;
	private INoiseData                      mNoiseData;
	private long                            mArtistId;

	public ArtistResolver( INoiseData data ) {
		mNoiseData = data;

		mServiceResultReceiver = new ServiceResultReceiver( new Handler());
		mServiceResultReceiver.setReceiver( this );
	}

	public void requestArtist( long artistId, ServiceResultReceiver.Receiver receiver ) {
		mArtistId = artistId;
		mClientReceiver = receiver;

		mNoiseData.GetArtistList( mServiceResultReceiver );
	}

	@Override
	public void onReceiveResult( int resultCode, Bundle resultData ) {
		if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
			ArrayList<Artist> artistList = resultData.getParcelableArrayList( NoiseRemoteApi.ArtistList );

			if( artistList != null ) {
				for( Artist artist : artistList ) {
					if( artist.getArtistId() == mArtistId ) {
						Bundle  artistData = new Bundle();

						artistData.putParcelable( NoiseRemoteApi.Artist, artist );
						mClientReceiver.onReceiveResult( NoiseRemoteApi.RemoteResultSuccess, artistData );

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
