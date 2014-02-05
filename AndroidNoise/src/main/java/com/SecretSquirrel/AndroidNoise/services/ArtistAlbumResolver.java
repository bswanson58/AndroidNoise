package com.SecretSquirrel.AndroidNoise.services;

import android.os.Bundle;

import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;

// Created by BSwanson on 1/28/14.

public class ArtistAlbumResolver {
	private INoiseData      mNoiseData;
	private Artist          mArtist;
	private Album           mAlbum;
	private ServiceResultReceiver.Receiver mClientReceiver;
	private boolean         mClientNotified;

	public ArtistAlbumResolver( INoiseData noiseData ) {
		mNoiseData = noiseData;
	}

	public void requestArtistAlbum( long artistId, long albumId, ServiceResultReceiver.Receiver receiver ) {
		mClientReceiver = receiver;

		ArtistResolver  artistResolver = new ArtistResolver( mNoiseData );

		artistResolver.requestArtist( artistId, new ServiceResultReceiver.Receiver() {
			@Override
			public void onReceiveResult( int resultCode, Bundle resultData ) {
				if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
					mArtist = resultData.getParcelable( NoiseRemoteApi.Artist );

					notifyClient();
				}
				else {
					mClientNotified = true;
					mClientReceiver.onReceiveResult( resultCode, null );
				}
			}
		} );

		AlbumResolver   albumResolver = new AlbumResolver( mNoiseData );

		albumResolver.requestAlbum( albumId, artistId, new ServiceResultReceiver.Receiver() {
			@Override
			public void onReceiveResult( int resultCode, Bundle resultData ) {
				if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
					mAlbum = resultData.getParcelable( NoiseRemoteApi.Album );

					notifyClient();
				}
				else {
					mClientNotified = true;
					mClientReceiver.onReceiveResult( resultCode, null );
				}
			}
		} );
	}

	public void requestArtistAlbum( String artistName, final String albumName, ServiceResultReceiver.Receiver receiver ) {
		mClientReceiver = receiver;

		ArtistResolver  artistResolver = new ArtistResolver( mNoiseData );

		artistResolver.requestArtist( artistName, new ServiceResultReceiver.Receiver() {
			@Override
			public void onReceiveResult( int resultCode, Bundle resultData ) {
				if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
					mArtist = resultData.getParcelable( NoiseRemoteApi.Artist );

					requestAlbum( albumName );
				}
				else {
					mClientNotified = true;
					mClientReceiver.onReceiveResult( resultCode, null );
				}
			}
		} );

	}

	private void requestAlbum( String albumName ) {
		if( mArtist != null ) {
			AlbumResolver   albumResolver = new AlbumResolver( mNoiseData );

			albumResolver.requestAlbum( albumName, mArtist.getArtistId(), new ServiceResultReceiver.Receiver() {
				@Override
				public void onReceiveResult( int resultCode, Bundle resultData ) {
					if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
						mAlbum = resultData.getParcelable( NoiseRemoteApi.Album );

						notifyClient();
					}
					else {
						mClientNotified = true;
						mClientReceiver.onReceiveResult( resultCode, null );
					}
				}
			} );
		}
	}

	private void notifyClient() {
		if(!mClientNotified ) {
			if(( mArtist != null ) &&
			   ( mAlbum != null )) {
				mClientNotified = true;

				Bundle  resultData = new Bundle();

				resultData.putParcelable( NoiseRemoteApi.Artist, mArtist );
				resultData.putParcelable( NoiseRemoteApi.Album, mAlbum );

				mClientReceiver.onReceiveResult( NoiseRemoteApi.RemoteResultSuccess, resultData );
			}
		}
	}
}
