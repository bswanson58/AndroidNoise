package com.SecretSquirrel.AndroidNoise.model;

// Secret Squirrel Software - Created by bswanson on 12/9/13.

import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.SecretSquirrel.AndroidNoise.dto.ServerInformation;
import com.SecretSquirrel.AndroidNoise.dto.ServerVersion;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;
import com.SecretSquirrel.AndroidNoise.services.NoiseDataClient;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteClient;
import com.SecretSquirrel.AndroidNoise.services.ServiceLocatorClient;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;

import java.util.ArrayList;

import rx.Observer;

public class ApplicationState implements IApplicationState {
	private Context                 mContext;
	private ServerInformation       mCurrentServer;
	private boolean                 mIsConnected;
	private NoiseDataClient         mDataClient;

	public ApplicationState( Context context ) {
		mContext = context;
	}

	public boolean getIsConnected() {
		return(( mIsConnected ) &&
			   ( mCurrentServer != null ));
	}

	public INoiseData getDataClient() {
		return( mDataClient );
	}

	public void SelectServer( ServerInformation server ) {
		mCurrentServer = server;
		mIsConnected = mCurrentServer != null;

		if( server != null ) {
			mDataClient = new NoiseDataClient( mContext, mCurrentServer.getServerAddress());
		}
	}

	public void LocateServers( final ServiceResultReceiver receiver ) {
		ServiceLocatorClient    locatorClient = new ServiceLocatorClient( mContext );

		locatorClient.LocateServices( "_Noise._Tcp.local.", new ResultReceiver( null ) {
			@Override
			protected void onReceiveResult( int resultCode, Bundle resultData ) {
				if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
					final String[]  servers = resultData.getStringArray( NoiseRemoteApi.LocateServicesList );
					ServerObserver  observer = new ServerObserver( receiver );

					rx.Observable.from( servers ).subscribe( observer );
				}
				else {
					receiver.send( resultCode, resultData );
				}
			}
		});
	}

	private class ServerObserver implements Observer<String> {
		private ArrayList<ServerInformation>    mServerInformationList;
		private ResultReceiver                  mReceiver;
		private int                             mServerCount;
		private int                             mResultCount;

		public ServerObserver( ResultReceiver receiver ) {
			mServerInformationList = new ArrayList<ServerInformation>();
			mReceiver = receiver;

			mServerCount = 0;
			mResultCount = 0;
		}

		@Override
		public void onCompleted() { }

		@Override
		public void onError( Throwable throwable ) {
			Log.e( "ApplicationState", "LocateServers", throwable );

			Bundle  result = new Bundle();

			result.putString( NoiseRemoteApi.RemoteResultErrorMessage, throwable.getMessage());

			mReceiver.send( NoiseRemoteApi.RemoteResultException, result );
		}

		@Override
		public void onNext( String serverAddress ) {
			mServerCount++;

			NoiseRemoteClient   remoteClient = new NoiseRemoteClient( mContext, serverAddress );

			remoteClient.getServerVersion( new ResultReceiver( null ) {
					@Override
					protected void onReceiveResult(int resultCode, Bundle resultData) {
						if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
							ServerVersion   serverVersion = resultData.getParcelable( NoiseRemoteApi.RemoteResultVersion );
							String          serverAddress = resultData.getString( NoiseRemoteApi.RemoteServerAddress );

							if(( serverVersion != null ) &&
							   (!TextUtils.isEmpty( serverAddress ))) {
								mServerInformationList.add( new ServerInformation( serverAddress, serverVersion ));
							}
						}

						mResultCount++;

						if( mResultCount == mServerCount ) {
							Bundle  result = new Bundle();

							result.putParcelableArrayList( NoiseRemoteApi.LocateServicesList, mServerInformationList );

							mReceiver.send( NoiseRemoteApi.RemoteResultSuccess, result );
						}
					}} );
		}
	}
}
