package com.SecretSquirrel.AndroidNoise.services;

import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationServices;

import javax.inject.Inject;

import dagger.Lazy;
import de.greenrobot.event.EventBus;

// Created by BSwanson on 2/9/14.

public class ApplicationServices implements IApplicationServices {
	private final Lazy<QueueRequestHandler> mQueueProvider;
	private	QueueRequestHandler             mQueueRequestHandler;

	@Inject
	public ApplicationServices( EventBus eventBus, Lazy<QueueRequestHandler> provider ) {
		mQueueProvider = provider;

		eventBus.register( this );
	}

	@SuppressWarnings("unused")
	public void onEvent( EventServerSelected args ) {
		stop();

		mQueueRequestHandler = mQueueProvider.get();

		start();
	}

	@Override
	public void start() {
		if( mQueueRequestHandler != null ) {
			mQueueRequestHandler.start();
		}
	}

	@Override
	public void stop() {
		if( mQueueRequestHandler != null ) {
			mQueueRequestHandler.stop();
		}
	}
}
