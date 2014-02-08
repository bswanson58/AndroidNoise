package com.SecretSquirrel.AndroidNoise.services;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;

import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;

import javax.inject.Inject;

// Secret Squirrel Software - Created by bswanson on 12/6/13.

public class NoiseDataClient implements INoiseData {
	private final Context           mContext;
	private final IApplicationState mApplicationState;

	@Inject
	public NoiseDataClient( Context context, IApplicationState applicationState ) {
		mContext = context;
		mApplicationState = applicationState;
	}

	@Override
	public void GetArtistList( ResultReceiver receiver ) {
		mContext.startService( setupApi( NoiseRemoteApi.GetArtistList, receiver ));
	}

	@Override
	public void GetArtistInfo( long forArtist, ResultReceiver receiver ) {
		Intent  intent = setupApi( NoiseRemoteApi.GetArtistInfo, receiver );

		intent.putExtra( NoiseRemoteApi.ArtistId, forArtist );

		mContext.startService( intent );
	}

	@Override
	public void GetAlbumList( long forArtist, ResultReceiver receiver ) {
		Intent  intent = setupApi( NoiseRemoteApi.GetAlbumList, receiver );

		intent.putExtra( NoiseRemoteApi.ArtistId, forArtist );

		mContext.startService( intent );
	}

	@Override
	public void GetAlbumInfo( long forAlbum, ResultReceiver receiver ) {
		Intent  intent = setupApi( NoiseRemoteApi.GetAlbumInfo, receiver );

		intent.putExtra( NoiseRemoteApi.AlbumId, forAlbum );

		mContext.startService( intent );
	}

	@Override
	public void GetTrackList( long forAlbum, ResultReceiver receiver ) {
		Intent  intent = setupApi( NoiseRemoteApi.GetTrackList, receiver );

		intent.putExtra( NoiseRemoteApi.AlbumId, forAlbum );

		mContext.startService( intent );
	}

	@Override
	public void GetFavoritesList( ResultReceiver receiver ) {
		mContext.startService( setupApi( NoiseRemoteApi.GetFavoritesList, receiver ));
	}

	private Intent setupApi( int apiCode, ResultReceiver resultReceiver ) {
		Intent intent = new Intent( mContext, NoiseDataService.class );

		intent.putExtra( NoiseRemoteApi.RemoteServerAddress, mApplicationState.getCurrentServer().getServerAddress());
		intent.putExtra( NoiseRemoteApi.RemoteApiParameter, apiCode );
		intent.putExtra( NoiseRemoteApi.RemoteCallReceiver, resultReceiver );

		return( intent );
	}
}
