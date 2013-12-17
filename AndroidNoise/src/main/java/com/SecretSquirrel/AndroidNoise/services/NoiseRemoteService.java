package com.SecretSquirrel.AndroidNoise.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import com.SecretSquirrel.AndroidNoise.dto.ServerVersion;
import com.SecretSquirrel.AndroidNoise.services.rto.RemoteServerRestApi;
import com.SecretSquirrel.AndroidNoise.services.rto.RoServerVersion;

import retrofit.RestAdapter;

// Secret Squirrel Software - Created by bswanson on 12/6/13.

public class NoiseRemoteService extends IntentService {

	public NoiseRemoteService() {
		super( "NoiseRemoteService" );
	}

	@Override
	protected void onHandleIntent( Intent intent ) {
		int             callId = intent.getIntExtra( NoiseRemoteApi.RemoteApiParameter, 0 );
		String          serverAddress = intent.getStringExtra( NoiseRemoteApi.RemoteServerAddress );
		ResultReceiver  receiver = intent.getParcelableExtra( NoiseRemoteApi.RemoteCallReceiver );

		if( receiver != null ) {
			if(!TextUtils.isEmpty( serverAddress )) {
				switch( callId ) {
					case NoiseRemoteApi.GetServerVersion:
						getServerVersion( serverAddress, receiver );
						break;
				}
			}
			else {
				Bundle  resultData = new Bundle();

				resultData.putString( NoiseRemoteApi.RemoteResultErrorMessage, "Server address is not valid." );
				receiver.send( NoiseRemoteApi.RemoteResultError, resultData );
			}
		}
	}

	private void getServerVersion( String serverAddress, ResultReceiver receiver ) {
		Bundle  resultData = new Bundle();
		int     resultCode = NoiseRemoteApi.RemoteResultError;

		try {
			RestAdapter         restAdapter = new RestAdapter.Builder().setServer( serverAddress ).build();
			RemoteServerRestApi service = restAdapter.create( RemoteServerRestApi.class );
			RoServerVersion     roVersion = service.GetServerVersion();
			ServerVersion       version = new ServerVersion( roVersion );

			resultData.putInt( NoiseRemoteApi.RemoteApiParameter, NoiseRemoteApi.GetServerVersion );
			resultData.putParcelable( NoiseRemoteApi.RemoteResultVersion, version );
			resultData.putString( NoiseRemoteApi.RemoteServerAddress, serverAddress );
			resultCode = NoiseRemoteApi.RemoteResultSuccess;
		}
		catch( Exception ex ) {
			resultData.putString( NoiseRemoteApi.RemoteResultErrorMessage, ex.getMessage());
			resultCode = NoiseRemoteApi.RemoteResultException;
		}

		receiver.send( resultCode, resultData );
	}
}
