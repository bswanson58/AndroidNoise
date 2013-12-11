package com.SecretSquirrel.AndroidNoise.services;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;

// Secret Squirrel Software - Created by bswanson on 12/6/13.

public class NoiseRemoteClient {
	private Context         mContext;
	private String          mServerAddress;

	public NoiseRemoteClient( Context context, String serverAddress ) {
		mContext = context;
		mServerAddress = serverAddress;
	}

	public void getServerVersion( ResultReceiver receiver ) {
		setupAndCallApi( NoiseRemoteApi.GetServerVersion, receiver );
	}

	private void setupAndCallApi( int apiCode, ResultReceiver resultReceiver ) {
		Intent  intent = new Intent( mContext, NoiseRemoteService.class );

		intent.putExtra( NoiseRemoteApi.RemoteServerAddress, mServerAddress );
		intent.putExtra( NoiseRemoteApi.RemoteApiParameter, apiCode );
		intent.putExtra( NoiseRemoteApi.RemoteCallReceiver, resultReceiver );

		mContext.startService( intent );
	}
}
