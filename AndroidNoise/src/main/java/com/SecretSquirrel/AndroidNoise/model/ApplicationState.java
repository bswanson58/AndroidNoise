package com.SecretSquirrel.AndroidNoise.model;

// Secret Squirrel Software - Created by bswanson on 12/9/13.

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import com.SecretSquirrel.AndroidNoise.dto.ServerInformation;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseQueue;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseSearch;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseServer;
import com.SecretSquirrel.AndroidNoise.services.EventHostService;
import com.SecretSquirrel.AndroidNoise.services.NoiseDataClient;
import com.SecretSquirrel.AndroidNoise.services.NoiseQueueClient;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteClient;
import com.SecretSquirrel.AndroidNoise.services.NoiseSearchClient;
import com.SecretSquirrel.AndroidNoise.services.ServiceLocator;

import rx.Observable;

public class ApplicationState implements IApplicationState {
	private Context                 mContext;
	private ServerInformation       mCurrentServer;
	private boolean                 mIsConnected;
	private NoiseRemoteClient       mNoiseClient;
	private NoiseDataClient         mDataClient;
	private NoiseQueueClient        mQueueClient;
	private NoiseSearchClient       mSearchClient;
	private QueueRequestHandler     mQueueRequestHandler;

	public ApplicationState( Context context ) {
		mContext = context;

		mQueueRequestHandler = new QueueRequestHandler( mContext, this );
	}

	public boolean getIsConnected() {
		return(( mIsConnected ) &&
			   ( mCurrentServer != null ));
	}

	public INoiseServer getNoiseClient() {
		return( mNoiseClient );
	}

	public INoiseData getDataClient() {
		return( mDataClient );
	}

	public INoiseQueue getQueueClient() {
		return( mQueueClient );
	}

	public INoiseSearch getSearchClient() {
		return( mSearchClient );
	}

	public void SelectServer( ServerInformation server ) {
		mCurrentServer = server;
		mIsConnected = mCurrentServer != null;

		if( server != null ) {
			mNoiseClient = new NoiseRemoteClient( mContext, mCurrentServer.getServerAddress());
			mDataClient = new NoiseDataClient( mContext, mCurrentServer.getServerAddress());
			mQueueClient = new NoiseQueueClient( mCurrentServer.getServerAddress());
			mSearchClient = new NoiseSearchClient( mCurrentServer.getServerAddress());
		}
	}

	@Override
	public void registerForEvents( ServiceConnection client ) {
		mContext.bindService( new Intent( mContext, EventHostService.class ), client, Context.BIND_AUTO_CREATE );
	}

	@Override
	public void unregisterFromEvents( ServiceConnection client ) {
		mContext.unbindService( client );
	}

	public Observable<ServerInformation> locateServers() {
		return( ServiceLocator.createServiceLocator( mContext ));
	}
}
