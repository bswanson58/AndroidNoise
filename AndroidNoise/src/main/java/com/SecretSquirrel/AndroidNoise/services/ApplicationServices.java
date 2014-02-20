package com.SecretSquirrel.AndroidNoise.services;

import com.SecretSquirrel.AndroidNoise.events.EventActivityPausing;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationServices;
import com.SecretSquirrel.AndroidNoise.interfaces.IRecentData;

import javax.inject.Inject;

import dagger.Lazy;
import de.greenrobot.event.EventBus;

// Created by BSwanson on 2/9/14.

public class ApplicationServices implements IApplicationServices {
	private final Lazy<QueueRequestHandler>         mQueueRequestProvider;
	private QueueRequestHandler                     mQueueRequestHandler;
	private final IRecentData                       mRecentDataManager;

	@Inject
	public ApplicationServices( EventBus eventBus,
	                            RecentDataManager recentDataProvider,
	                            Lazy<QueueRequestHandler> queueRequestProvider ) {
		mRecentDataManager = recentDataProvider;
		mQueueRequestProvider = queueRequestProvider;

		eventBus.register( this );
	}

	public IRecentData getRecentDataManager() {
		return( mRecentDataManager );
	}

	@SuppressWarnings("unused")
	public void onEvent( EventServerSelected args ) {
		stop();

		if( mQueueRequestHandler == null ) {
			mQueueRequestHandler = mQueueRequestProvider.get();
		}

		start();
	}

	@SuppressWarnings("unused")
	public void onEvent( EventActivityPausing args ) {
		mRecentDataManager.persistData();
	}

	private void start() {
		mRecentDataManager.start();
	}

	private void stop() {
		mRecentDataManager.persistData();
		mRecentDataManager.stop();
	}
}
