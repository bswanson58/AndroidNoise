package com.SecretSquirrel.AndroidNoise.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import com.SecretSquirrel.AndroidNoise.dto.ServerInformation;
import com.SecretSquirrel.AndroidNoise.dto.ServerVersion;
import com.SecretSquirrel.AndroidNoise.services.noiseApi.RemoteServerRestApi;
import com.SecretSquirrel.AndroidNoise.services.rto.RoServerInformation;
import com.SecretSquirrel.AndroidNoise.services.rto.RoServerVersion;

import retrofit.RestAdapter;
import timber.log.Timber;

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

					case NoiseRemoteApi.GetServerInformation:
						getServerInformation( serverAddress, receiver );
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
		int     resultCode;

		resultData.putInt( NoiseRemoteApi.RemoteApiParameter, NoiseRemoteApi.GetServerVersion );

		try {
			RestAdapter         restAdapter = new RestAdapter.Builder().setEndpoint( serverAddress ).build();
			RemoteServerRestApi service = restAdapter.create( RemoteServerRestApi.class );
			RoServerVersion     roVersion = service.GetServerVersion();
			ServerVersion       version = new ServerVersion( roVersion );

			resultData.putParcelable( NoiseRemoteApi.RemoteResultVersion, version );
			resultData.putString( NoiseRemoteApi.RemoteServerAddress, serverAddress );
			resultCode = NoiseRemoteApi.RemoteResultSuccess;
		}
		catch( Exception ex ) {
			resultData.putString( NoiseRemoteApi.RemoteResultErrorMessage, ex.getMessage());
			resultCode = NoiseRemoteApi.RemoteResultException;

			Timber.w( ex, "getServerVersion" );
		}

		receiver.send( resultCode, resultData );
	}

	private void getServerInformation( String serverAddress, ResultReceiver receiver ) {
		Bundle  resultData = new Bundle();
		int     resultCode;

		resultData.putInt( NoiseRemoteApi.RemoteApiParameter, NoiseRemoteApi.GetServerInformation );

		try {
			RestAdapter         restAdapter = new RestAdapter.Builder().setEndpoint( serverAddress ).build();
			RemoteServerRestApi service = restAdapter.create( RemoteServerRestApi.class );
			RoServerInformation information = service.GetServerInformation();
			ServerInformation   serverInformation = new ServerInformation( serverAddress, information );

			resultData.putParcelable( NoiseRemoteApi.ServerInformation, serverInformation );
			resultData.putString( NoiseRemoteApi.RemoteServerAddress, serverAddress );
			resultCode = NoiseRemoteApi.RemoteResultSuccess;
		}
		catch( Exception ex ) {
			resultData.putString( NoiseRemoteApi.RemoteResultErrorMessage, ex.getMessage());
			resultCode = NoiseRemoteApi.RemoteResultException;

			Timber.w( ex, "getServerInformation" );
		}

		receiver.send( resultCode, resultData );
	}
}
