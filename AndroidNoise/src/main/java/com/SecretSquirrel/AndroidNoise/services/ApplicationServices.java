package com.SecretSquirrel.AndroidNoise.services;

import com.SecretSquirrel.AndroidNoise.events.EventActivityPausing;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.events.EventShakeDetected;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationServices;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.interfaces.IQueueRequestHandler;
import com.SecretSquirrel.AndroidNoise.interfaces.IRecentData;
import com.SecretSquirrel.AndroidNoise.interfaces.IRecentDataManager;
import com.SecretSquirrel.AndroidNoise.support.ShakeHandler;

import javax.inject.Inject;

import dagger.Lazy;
import de.greenrobot.event.EventBus;

// Created by BSwanson on 2/9/14.

public class ApplicationServices implements IApplicationServices {
	private final static long                   SHAKE_INTERVAL = 5000;

	private final IApplicationState             mApplicationState;
	private final IRecentDataManager            mRecentDataManager;
	private final Lazy<IQueueRequestHandler>    mQueueRequestProvider;
	private final ShakeHandler                  mShakeHandler;
	private final ShakeHandler.ShakeResponder   mShakeResponder;
	private IQueueRequestHandler                mQueueRequestHandler;
	private long                                mLastShakeTime;

	@Inject
	public ApplicationServices( EventBus eventBus,
	                            IApplicationState applicationState,
	                            IRecentDataManager recentDataManager,
	                            ShakeHandler shakeHandler,
	                            ShakeHandler.ShakeResponder shakeResponder,
	                            Lazy<IQueueRequestHandler> queueRequestProvider ) {
		mApplicationState = applicationState;
		mRecentDataManager = recentDataManager;
		mShakeHandler = shakeHandler;
		mShakeResponder = shakeResponder;
		mQueueRequestProvider = queueRequestProvider;

		eventBus.register( this );
	}

	public IRecentData getRecentDataManager() {
		return( mRecentDataManager.getRecentData());
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

	@SuppressWarnings( "unused" )
	public void onEvent( EventShakeDetected args ) {
		long    currentTime = System.currentTimeMillis();

		if(( mApplicationState.getIsConnected()) &&
		  (( mLastShakeTime + SHAKE_INTERVAL ) < currentTime )) {
			mShakeResponder.onShake();

			mLastShakeTime = currentTime;
		}
	}
}
