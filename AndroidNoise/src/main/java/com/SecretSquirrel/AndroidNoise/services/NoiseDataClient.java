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
		setupAndCallApi( NoiseRemoteApi.GetArtistList, receiver );
	}

	private void setupAndCallApi( int apiCode, ResultReceiver resultReceiver ) {
		Intent intent = new Intent( mContext, NoiseDataService.class );

		intent.putExtra( NoiseRemoteApi.RemoteServerAddress, mServerAddress );
		intent.putExtra( NoiseRemoteApi.RemoteApiParameter, apiCode );
		intent.putExtra( NoiseRemoteApi.RemoteCallReceiver, resultReceiver );

		mContext.startService( intent );
	}
}
