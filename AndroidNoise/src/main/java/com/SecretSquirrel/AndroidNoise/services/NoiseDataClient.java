package com.SecretSquirrel.AndroidNoise.services;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;

import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;

// Secret Squirrel Software - Created by bswanson on 12/6/13.

public class NoiseDataClient implements INoiseData {
	private Context mContext;
	private String          mServerAddress;

	public NoiseDataClient( Context context, String serverAddress ) {
		mContext = context;
		mServerAddress = serverAddress;
	}

	public void GetArtistList( ResultReceiver receiver ) {
		mContext.startService( setupApi( NoiseRemoteApi.GetArtistList, receiver ));
	}

	@Override
	public void GetAlbumList( long forArtist, ResultReceiver receiver ) {
		Intent  intent = setupApi( NoiseRemoteApi.GetAlbumList, receiver );

		intent.putExtra( NoiseRemoteApi.ArtistId, forArtist );

		mContext.startService( intent );
	}

	@Override
	public void GetTrackList( long forAlbum, ResultReceiver receiver ) {
		Intent  intent = setupApi( NoiseRemoteApi.GetTrackList, receiver );

		intent.putExtra( NoiseRemoteApi.AlbumId, forAlbum );

		mContext.startService( intent );
	}

	private Intent setupApi( int apiCode, ResultReceiver resultReceiver ) {
		Intent intent = new Intent( mContext, NoiseDataService.class );

		intent.putExtra( NoiseRemoteApi.RemoteServerAddress, mServerAddress );
		intent.putExtra( NoiseRemoteApi.RemoteApiParameter, apiCode );
		intent.putExtra( NoiseRemoteApi.RemoteCallReceiver, resultReceiver );

		return( intent );
	}
}
