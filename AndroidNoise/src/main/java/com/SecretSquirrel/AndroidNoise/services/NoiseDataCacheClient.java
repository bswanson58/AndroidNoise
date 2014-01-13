package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 1/13/14.

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.dto.Favorite;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;

import java.util.ArrayList;

public class NoiseDataCacheClient implements INoiseData {
	private final INoiseData        mNoiseData;
	private ArrayList<Artist>       mArtistList;
	private ArrayList<Favorite>     mFavoritesList;

	public NoiseDataCacheClient( Context context, String serverAddress ) {
		mNoiseData = new NoiseDataClient( context, serverAddress );
	}

	@Override
	public void GetArtistList( final ResultReceiver receiver ) {
		if( mArtistList != null ) {
			Bundle  resultData = new Bundle();

			resultData.putParcelableArrayList( NoiseRemoteApi.ArtistList, mArtistList );

			receiver.send( NoiseRemoteApi.RemoteResultSuccess, resultData );
		}
		else {
			mNoiseData.GetArtistList( new ResultReceiver( new Handler()) {
				@Override
				public void onReceiveResult( int resultCode, Bundle resultData ) {
					if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
						mArtistList = resultData.getParcelableArrayList( NoiseRemoteApi.ArtistList );

						receiver.send( resultCode, resultData );
					}
				}
			}  );
		}
	}

	@Override
	public void GetArtistInfo( long forArtist, final ResultReceiver receiver ) {
		mNoiseData.GetArtistInfo( forArtist, receiver );
	}

	@Override
	public void GetAlbumList( long forArtist, final ResultReceiver receiver ) {
		mNoiseData.GetAlbumList( forArtist, receiver );
	}

	@Override
	public void GetAlbumInfo( long forAlbum, final ResultReceiver receiver ) {
		mNoiseData.GetAlbumInfo( forAlbum, receiver );
	}

	@Override
	public void GetTrackList( long forAlbum, final ResultReceiver receiver ) {
		mNoiseData.GetTrackList( forAlbum, receiver );
	}

	@Override
	public void GetFavoritesList( final ResultReceiver receiver ) {
		if( mFavoritesList != null ) {
			Bundle  resultData = new Bundle();

			resultData.putParcelableArrayList( NoiseRemoteApi.FavoritesList, mFavoritesList );

			receiver.send( NoiseRemoteApi.RemoteResultSuccess, resultData );
		}
		else {
			mNoiseData.GetFavoritesList( new ResultReceiver( new Handler()) {
				@Override
				public void onReceiveResult( int resultCode, Bundle resultData ) {
					if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
						mFavoritesList = resultData.getParcelableArrayList( NoiseRemoteApi.FavoritesList );

						receiver.send( resultCode, resultData );
					}
				}
			}  );
		}
	}
}
