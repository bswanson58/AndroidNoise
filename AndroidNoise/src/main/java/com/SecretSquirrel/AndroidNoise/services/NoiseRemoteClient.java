package com.SecretSquirrel.AndroidNoise.services;

import android.content.Context;
import android.content.Intent;

// Secret Squirrel Software - Created by bswanson on 12/6/13.

public class NoiseRemoteClient {
	private Context                 mContext;
	private ServiceResultReceiver   mReceiver;
	private String                  mServerAddress;

	public NoiseRemoteClient( Context context, ServiceResultReceiver receiver, String serverAddress ) {
		mContext = context;
		mReceiver = receiver;
		mServerAddress = serverAddress;
	}

	public void getServerVersion() {
		setupAndCallApi( NoiseRemoteApi.GetServerVersion );
	}

	private void setupAndCallApi( int apiCode ) {
		Intent  intent = new Intent( mContext, NoiseRemoteService.class );

		intent.putExtra( NoiseRemoteApi.RemoteServerAddress, mServerAddress );
		intent.putExtra( NoiseRemoteApi.RemoteApiParameter, apiCode );
		intent.putExtra( NoiseRemoteApi.RemoteCallReceiver, mReceiver );

		mContext.startService( intent );
	}
}
