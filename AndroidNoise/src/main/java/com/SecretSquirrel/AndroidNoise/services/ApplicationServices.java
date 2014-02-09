package com.SecretSquirrel.AndroidNoise.services;

import com.SecretSquirrel.AndroidNoise.events.EventActivityPausing;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationServices;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;
import com.SecretSquirrel.AndroidNoise.interfaces.IRecentData;

import javax.inject.Inject;

import dagger.Lazy;
import de.greenrobot.event.EventBus;

// Created by BSwanson on 2/9/14.

public class ApplicationServices implements IApplicationServices {
	private final Lazy<QueueRequestHandler>     mQueueProvider;
	private final Lazy<RecentDataManager>       mRecentDataProvider;
	private final Lazy<NoiseDataCacheClient>    mDataProvider;
	private INoiseData                          mNoiseData;
	private IRecentData                         mRecentDataManager;
	private	QueueRequestHandler                 mQueueRequestHandler;

	@Inject
	public ApplicationServices( EventBus eventBus,
	                            Lazy<RecentDataManager> recentDataProvider,
	                            Lazy<QueueRequestHandler> queueRequestProvider,
	                            Lazy<NoiseDataCacheClient> dataProvider ) {
		mQueueProvider = queueRequestProvider;
		mRecentDataProvider = recentDataProvider;
		mDataProvider = dataProvider;

		eventBus.register( this );
	}

	public IRecentData getRecentDataManager() {
		return( mRecentDataManager );
	}

	public INoiseData getNoiseData() {
		if( mNoiseData == null ) {
			mNoiseData = mDataProvider.get();
		}

		return( mNoiseData );
	}

	@SuppressWarnings("unused")
	public void onEvent( EventServerSelected args ) {
		stop();

		mQueueRequestHandler = mQueueProvider.get();
		mRecentDataManager = mRecentDataProvider.get();

		start();
	}

	@SuppressWarnings("unused")
	public void onEvent( EventActivityPausing args ) {
		if( mRecentDataManager != null ) {
			mRecentDataManager.persistData();
		}
	}

	private void start() {
		if( mQueueRequestHandler != null ) {
			mQueueRequestHandler.start();
		}

		if( mRecentDataManager != null ) {
			mRecentDataManager.start();
		}
	}

	private void stop() {
		if( mQueueRequestHandler != null ) {
			mQueueRequestHandler.stop();
		}

		if( mRecentDataManager != null ) {
			mRecentDataManager.persistData();
			mRecentDataManager.stop();
		}

		mNoiseData = null;
	}
}
