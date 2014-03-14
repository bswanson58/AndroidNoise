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
import rx.Subscriber;
import rx.schedulers.Schedulers;

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
		return( Observable.create( new Observable.OnSubscribe<ServerTimeSync>() {
			@Override
			public void call( Subscriber<? super ServerTimeSync> subscriber ) {
				try {
					RoTimeSync  roTimeSync = getService().syncServerTime( System.currentTimeMillis() );

					if( roTimeSync.Success ) {
						subscriber.onNext( new ServerTimeSync( roTimeSync ));
						subscriber.onCompleted();
					}
					else {
						subscriber.onError( new Exception( roTimeSync.ErrorMessage ));
					}
				}
				catch( Exception ex ) {
					subscriber.onError( ex );
				}
			}
		} ).subscribeOn( Schedulers.io()));
	}

	@Override
	public Observable<TransportState> getTransportState() {
		return( Observable.create( new Observable.OnSubscribe<TransportState>() {
			@Override
			public void call( Subscriber<? super TransportState> subscriber ) {
				try {
					RoTransportState    roState = getService().getTransportState();

					if( roState.Success ) {
						subscriber.onNext( new TransportState( roState ) );
						subscriber.onCompleted();
					}
					else {
						subscriber.onError( new Exception( roState.ErrorMessage ) );
					}
				}
				catch( Exception ex ) {
					subscriber.onError( ex );
				}
			}
		} ).subscribeOn( Schedulers.io()));
	}

	@Override
	public Observable<AudioState> getAudioState() {
		return( Observable.create( new Observable.OnSubscribe<AudioState>() {
			@Override
			public void call( Subscriber<? super AudioState> subscriber ) {
				try {
					AudioStateResult    result = getService().getAudioState();

					if( result.Success ) {
						subscriber.onNext( new AudioState( result.AudioState ) );
						subscriber.onCompleted();
					}
					else {
						subscriber.onError( new Exception( result.ErrorMessage ) );
					}
				}
				catch( Exception ex ) {
					subscriber.onError( ex );
				}
			}
		} ).subscribeOn( Schedulers.io()));
	}

	@Override
	public Observable<BaseServerResult> setAudioState( final AudioState state ) {
		return( Observable.create( new Observable.OnSubscribe<BaseServerResult>() {
			@Override
			public void call( Subscriber<? super BaseServerResult> subscriber ) {
				try {
					BaseServerResult    result = getService().setAudioState( state.asRoAudioState());

					if( result.Success ) {
						subscriber.onNext( result );
						subscriber.onCompleted();
					}
					else {
						subscriber.onError( new Exception( result.ErrorMessage ) );
					}
				}
				catch( Exception ex ) {
					subscriber.onError( ex );
				}
			}
		} ).subscribeOn( Schedulers.io()));
	}
}
