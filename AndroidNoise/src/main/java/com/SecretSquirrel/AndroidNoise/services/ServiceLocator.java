package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 12/9/13.

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class ServiceLocator extends IntentService {
	public ServiceLocator() {
		super( "ServiceLocator" );
	}

	@Override
	protected void onHandleIntent( Intent intent ) {
		int             callId = intent.getIntExtra( NoiseRemoteApi.RemoteApiParameter, 0 );
		ResultReceiver  receiver = intent.getParcelableExtra( NoiseRemoteApi.RemoteCallReceiver );

		if( receiver != null ) {
			switch( callId ) {
				case NoiseRemoteApi.LocateServices:
					String  servicesType  = intent.getStringExtra( NoiseRemoteApi.LocateServicesType );

					locateServices( servicesType, receiver );
					break;
			}
		}
	}

	private void locateServices( String ofType, ResultReceiver receiver ) {
		Bundle              resultData = new Bundle();
		int                 resultCode = NoiseRemoteApi.RemoteResultError;
		String[]            serverList = new String[1];

		serverList[0] = "http://10.1.1.139:88/Noise";

		resultData.putStringArray( NoiseRemoteApi.LocateServicesList, serverList );
		resultCode = NoiseRemoteApi.RemoteResultSuccess;

		receiver.send( resultCode, resultData );
	}
}
