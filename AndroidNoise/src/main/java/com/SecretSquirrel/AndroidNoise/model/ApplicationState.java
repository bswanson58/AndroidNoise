package com.SecretSquirrel.AndroidNoise.model;

// Secret Squirrel Software - Created by bswanson on 12/9/13.

import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.SecretSquirrel.AndroidNoise.dto.ServerVersion;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteClient;
import com.SecretSquirrel.AndroidNoise.services.ServiceLocatorClient;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;
import com.SecretSquirrel.AndroidNoise.services.rto.RoServerVersion;

import java.util.ArrayList;

public class ApplicationState {
	private Context                 mContext;
	private ServerVersion           mServerVersion;
	private boolean                 mIsConnected;

	public ApplicationState( Context context ) {
		mContext = context;
	}

	public boolean getIsConnected() {
		return( mIsConnected );
	}

	public void LocateServers( final ServiceResultReceiver receiver ) {
		ServiceLocatorClient    locatorClient = new ServiceLocatorClient();

		locatorClient.LocateServices( "", new ResultReceiver( null ) {
			@Override
			protected void onReceiveResult( int resultCode, Bundle resultData ) {
				if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
					final String[]                  servers = resultData.getStringArray( "" );
					final ArrayList<ServerVersion>  serverInformationList = new ArrayList<ServerVersion>();

					for( String address : servers ) {
						NoiseRemoteClient   remoteClient = new NoiseRemoteClient( mContext, address,
								new ResultReceiver( null ) {
									@Override
									protected void onReceiveResult(int resultCode, Bundle resultData) {
										if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
											RoServerVersion serverVersion = (RoServerVersion)resultData.getParcelable( NoiseRemoteApi.RemoteResultVersion );

											serverInformationList.add( new ServerVersion( serverVersion ));
										}
										else {
											serverInformationList.add( null );
										}

										if( serverInformationList.size() == servers.length ) {
											Bundle  result = new Bundle();

											result.putParcelableArrayList( "", serverInformationList );

											receiver.send( NoiseRemoteApi.RemoteResultSuccess, result );
										}
									}} );
						remoteClient.getServerVersion();
					}
				}
			}}	);
	}
}