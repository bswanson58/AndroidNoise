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
import com.SecretSquirrel.AndroidNoise.interfaces.IRecentData;
import com.SecretSquirrel.AndroidNoise.services.EventHostService;
import com.SecretSquirrel.AndroidNoise.services.NoiseDataCacheClient;
import com.SecretSquirrel.AndroidNoise.services.NoiseDataClient;
import com.SecretSquirrel.AndroidNoise.services.NoiseQueueClient;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteClient;
import com.SecretSquirrel.AndroidNoise.services.NoiseSearchClient;
import com.SecretSquirrel.AndroidNoise.services.RecentDataManager;
import com.SecretSquirrel.AndroidNoise.services.ServiceLocator;

import rx.Observable;

public class ApplicationState implements IApplicationState {
	private Context                 mContext;
	private ServerInformation       mCurrentServer;
	private boolean                 mIsConnected;
	private NoiseRemoteClient       mNoiseClient;
	private NoiseDataCacheClient    mDataClient;
	private NoiseQueueClient        mQueueClient;
	private NoiseSearchClient       mSearchClient;
	private QueueRequestHandler     mQueueRequestHandler;
	private IRecentData             mRecentData;

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

	public IRecentData getRecentData() {
		return( mRecentData );
	}

	public void SelectServer( ServerInformation server ) {
		mCurrentServer = server;
		mIsConnected = mCurrentServer != null;

		if( server != null ) {
			mNoiseClient = new NoiseRemoteClient( mContext, mCurrentServer.getServerAddress());
			mDataClient = new NoiseDataCacheClient( new NoiseDataClient( mContext, mCurrentServer.getServerAddress()));
			mQueueClient = new NoiseQueueClient( mCurrentServer.getServerAddress());
			mSearchClient = new NoiseSearchClient( mCurrentServer.getServerAddress());

			if( mRecentData != null ) {
				mRecentData.persistData();
				mRecentData.stop();
			}

			mRecentData = new RecentDataManager( mContext, mCurrentServer );
			mRecentData.start();
		}
	}

	@Override
	public void pauseOperation() {
		if( getIsConnected()) {
			if( mRecentData != null ) {
				mRecentData.persistData();
			}
		}
	}

	@Override
	public boolean resumeOperation() {
		boolean retValue = true;

		if( getIsConnected()) {
			// we need to determine if the server we were connected to is still available...
		}

		return( retValue );
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
