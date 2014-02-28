package com.SecretSquirrel.AndroidNoise.services;

// Created by BSwanson on 2/27/14.

import com.SecretSquirrel.AndroidNoise.dto.ServerTimeSync;
import com.SecretSquirrel.AndroidNoise.events.EventActivityPausing;
import com.SecretSquirrel.AndroidNoise.events.EventActivityResuming;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseTransport;
import com.SecretSquirrel.AndroidNoise.services.noiseApi.RemoteServerTransportApi;
import com.SecretSquirrel.AndroidNoise.services.rto.RoTimeSync;

import javax.inject.Inject;
import javax.inject.Provider;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.concurrency.Schedulers;
import rx.subscriptions.Subscriptions;

public class NoiseTransportClient implements INoiseTransport {
	private final EventBus                              mEventBus;
	private final Provider<RemoteServerTransportApi>    mServiceProvider;
	private RemoteServerTransportApi                    mService;

	@Inject
	public NoiseTransportClient( EventBus eventBus, Provider<RemoteServerTransportApi> serviceProvider ) {
		mEventBus = eventBus;
		mServiceProvider = serviceProvider;

		mEventBus.register( this );
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventServerSelected args ) {
		mService = null;
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventActivityPausing args ) {
		mEventBus.unregister( this );
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventActivityResuming args ) {
		if(!mEventBus.isRegistered( this )) {
			mEventBus.register( this );
		}
	}

	private RemoteServerTransportApi getService() {
		if( mService == null ) {
			mService = mServiceProvider.get();
		}

		return( mService );
	}

	@Override
	public Observable<ServerTimeSync> SyncServerTime() {
		return( Observable.create( new Observable.OnSubscribeFunc<ServerTimeSync>() {
			@Override
			public Subscription onSubscribe( Observer<? super ServerTimeSync> observer ) {
				try {
					RoTimeSync  roTimeSync = getService().SyncServerTime( System.currentTimeMillis());

					if( roTimeSync.Success ) {
						observer.onNext( new ServerTimeSync( roTimeSync ));
						observer.onCompleted();
					}
					else {
						observer.onError( new Exception( roTimeSync.ErrorMessage ));
					}
				}
				catch( Exception ex ) {
					observer.onError( ex );
				}

				return( Subscriptions.empty());
			}
		} ).subscribeOn( Schedulers.threadPoolForIO() ));
	}
}
