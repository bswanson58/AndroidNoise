package com.SecretSquirrel.AndroidNoise.services;

import android.os.Bundle;
import android.os.Handler;

import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;
import com.SecretSquirrel.AndroidNoise.support.Constants;

import java.util.ArrayList;

// Created by BSwanson on 1/28/14.

public class ArtistResolver implements ServiceResultReceiver.Receiver {
	private ServiceResultReceiver           mServiceResultReceiver;
	private ServiceResultReceiver.Receiver  mClientReceiver;
	private INoiseData                      mNoiseData;
	private long                            mArtistId;
	private String                          mArtistName;

	public ArtistResolver( INoiseData data ) {
		mNoiseData = data;
		mArtistId = Constants.NULL_ID;

		mServiceResultReceiver = new ServiceResultReceiver( new Handler());
		mServiceResultReceiver.setReceiver( this );
	}

	public void requestArtist( long artistId, ServiceResultReceiver.Receiver receiver ) {
		mArtistId = artistId;
		mClientReceiver = receiver;

		mNoiseData.GetArtistList( mServiceResultReceiver );
	}

	public void requestArtist( String artistName, ServiceResultReceiver.Receiver receiver ) {
		mArtistName = artistName;
		mClientReceiver = receiver;

		mNoiseData.GetArtistList( mServiceResultReceiver );
	}

	private boolean determineMatch( Artist artist ) {
		boolean retValue;

		if( mArtistId != Constants.NULL_ID ) {
			retValue = artist.getArtistId() == mArtistId;
		}
		else {
			retValue = artist.getName().equals( mArtistName );
		}

		return( retValue );
	}

	@Override
	public void onReceiveResult( int resultCode, Bundle resultData ) {
		if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
			ArrayList<Artist> artistList = resultData.getParcelableArrayList( NoiseRemoteApi.ArtistList );

			if( artistList != null ) {
				for( Artist artist : artistList ) {
					if( determineMatch( artist )) {
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
