package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 12/9/13.

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;

public class ServiceLocatorClient {
	private Context     mContext;

	public ServiceLocatorClient( Context context ) {
		mContext = context;
	}

	public void LocateServices( String ofType, ResultReceiver receiver ) {
		Intent intent = new Intent( mContext, ServiceLocator.class );

		intent.putExtra( NoiseRemoteApi.RemoteApiParameter, NoiseRemoteApi.LocateServices );
		intent.putExtra( NoiseRemoteApi.LocateServicesType, ofType );
		intent.putExtra( NoiseRemoteApi.RemoteCallReceiver, receiver );

		mContext.startService( intent );
	}
}
