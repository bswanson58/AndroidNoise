package com.SecretSquirrel.AndroidNoise.model;

// Secret Squirrel Software - Created by bswanson on 12/9/13.

import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.dto.ServerInformation;
import com.SecretSquirrel.AndroidNoise.dto.ServerVersion;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseQueue;
import com.SecretSquirrel.AndroidNoise.services.NoiseDataClient;
import com.SecretSquirrel.AndroidNoise.services.NoiseQueueClient;
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
	private Artist                  mCurrentArtist;
	private Album                   mCurrentAlbum;
	private NoiseDataClient         mDataClient;
	private NoiseQueueClient        mQueueClient;
	private QueueRequestHandler     mQueueRequestHandler;

	public ApplicationState( Context context ) {
		mContext = context;

		mQueueRequestHandler = new QueueRequestHandler( mContext, this );
	}

	public boolean getIsConnected() {
		return(( mIsConnected ) &&
			   ( mCurrentServer != null ));
	}

	public INoiseData getDataClient() {
		return( mDataClient );
	}

	public INoiseQueue getQueueClient() {
		return( mQueueClient );
	}

	@Override
	public Artist getCurrentArtist() {
		return( mCurrentArtist );
	}

	@Override
	public void setCurrentArtist( Artist artist ) {
		mCurrentArtist = artist;
	}

	@Override
	public Album getCurrentAlbum() {
		return( mCurrentAlbum );
	}

	@Override
	public void setCurrentAlbum( Album album ) {
		mCurrentAlbum = album;
	}

	public void SelectServer( ServerInformation server ) {
		mCurrentServer = server;
		mIsConnected = mCurrentServer != null;

		if( server != null ) {
			mDataClient = new NoiseDataClient( mContext, mCurrentServer.getServerAddress());
			mQueueClient = new NoiseQueueClient( mCurrentServer.getServerAddress());
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
