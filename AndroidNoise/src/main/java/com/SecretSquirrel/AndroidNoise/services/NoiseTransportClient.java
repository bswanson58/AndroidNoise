package com.SecretSquirrel.AndroidNoise.services;

// Created by BSwanson on 2/27/14.

import com.SecretSquirrel.AndroidNoise.dto.AudioState;
import com.SecretSquirrel.AndroidNoise.dto.ServerTimeSync;
import com.SecretSquirrel.AndroidNoise.dto.TransportState;
import com.SecretSquirrel.AndroidNoise.events.EventActivityPausing;
import com.SecretSquirrel.AndroidNoise.events.EventActivityResuming;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseTransport;
import com.SecretSquirrel.AndroidNoise.services.noiseApi.RemoteServerTransportApi;
import com.SecretSquirrel.AndroidNoise.services.rto.AudioStateResult;
import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;
import com.SecretSquirrel.AndroidNoise.services.rto.RoTimeSync;
import com.SecretSquirrel.AndroidNoise.services.rto.RoTransportState;

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
	public Observable<ServerTimeSync> syncServerTime() {
		return( Observable.create( new Observable.OnSubscribeFunc<ServerTimeSync>() {
			@Override
			public Subscription onSubscribe( Observer<? super ServerTimeSync> observer ) {
				try {
					RoTimeSync  roTimeSync = getService().syncServerTime( System.currentTimeMillis() );

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

	@Override
	public Observable<TransportState> getTransportState() {
		return( Observable.create( new Observable.OnSubscribeFunc<TransportState>() {
			@Override
			public Subscription onSubscribe( Observer<? super TransportState> observer ) {
				try {
					RoTransportState    roState = getService().getTransportState();

					if( roState.Success ) {
						observer.onNext( new TransportState( roState ));
						observer.onCompleted();
					}
					else {
						observer.onError( new Exception( roState.ErrorMessage ));
					}
				}
				catch( Exception ex ) {
					observer.onError( ex );
				}

				return( Subscriptions.empty());
			}
		} ).subscribeOn( Schedulers.threadPoolForIO() ));
	}

	@Override
	public Observable<AudioState> getAudioState() {
		return( Observable.create( new Observable.OnSubscribeFunc<AudioState>() {
			@Override
			public Subscription onSubscribe( Observer<? super AudioState> observer ) {
				try {
					AudioStateResult    result = getService().getAudioState();

					if( result.Success ) {
						observer.onNext( new AudioState( result.AudioState ));
						observer.onCompleted();
					}
					else {
						observer.onError( new Exception( result.ErrorMessage ));
					}
				}
				catch( Exception ex ) {
					observer.onError( ex );
				}

				return( Subscriptions.empty());
			}
		} ).subscribeOn( Schedulers.threadPoolForIO() ));
	}

	@Override
	public Observable<BaseServerResult> setAudioState( final AudioState state ) {
		return( Observable.create( new Observable.OnSubscribeFunc<BaseServerResult>() {
			@Override
			public Subscription onSubscribe( Observer<? super BaseServerResult> observer ) {
				try {
					BaseServerResult    result = getService().setAudioState( state.asRoAudioState());

					if( result.Success ) {
						observer.onNext( result );
						observer.onCompleted();
					}
					else {
						observer.onError( new Exception( result.ErrorMessage ));
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
