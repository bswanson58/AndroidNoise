package com.SecretSquirrel.AndroidNoise.services;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;

import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseServer;
import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;
import com.SecretSquirrel.AndroidNoise.services.noiseApi.RemoteServerRestApi;

import javax.inject.Inject;
import javax.inject.Provider;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

// Secret Squirrel Software - Created by BSwanson on 12/6/13.

public class NoiseRemoteClient implements INoiseServer {
	private final Context                       mContext;
	private final String                        mServerAddress;
	private final Provider<RemoteServerRestApi> mServiceProvider;
	private RemoteServerRestApi                 mService;

	@Inject
	public NoiseRemoteClient( EventBus eventBus, Provider<RemoteServerRestApi> provider,
	                          IApplicationState applicationState, Context context ) {
		if( applicationState.getIsConnected()) {
			mServerAddress = applicationState.getCurrentServer().getServerAddress();
		}
		else {
			mServerAddress = "";
		}
		mServiceProvider = provider;
		mContext = context;

		eventBus.register( this );
	}

	public NoiseRemoteClient( RemoteServerRestApi service, String serverAddress, Context context ) {
		mServerAddress = serverAddress;
		mContext = context;
		mService = service;

		mServiceProvider = null;
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventServerSelected args ) {
		mService = null;
	}

	private RemoteServerRestApi getService() {
		if( mService == null ) {
			mService = mServiceProvider.get();
		}

		return( mService );
	}

	@Override
	public void getServerVersion( ResultReceiver receiver ) {
		Intent  intent = new Intent( mContext, NoiseRemoteService.class );

		intent.putExtra( NoiseRemoteApi.RemoteServerAddress, mServerAddress );
		intent.putExtra( NoiseRemoteApi.RemoteApiParameter, NoiseRemoteApi.GetServerVersion );
		intent.putExtra( NoiseRemoteApi.RemoteCallReceiver, receiver );

		mContext.startService( intent );
	}

	@Override
	public void getServerInformation( ResultReceiver receiver ) {
		Intent  intent = new Intent( mContext, NoiseRemoteService.class );

		intent.putExtra( NoiseRemoteApi.RemoteServerAddress, mServerAddress );
		intent.putExtra( NoiseRemoteApi.RemoteApiParameter, NoiseRemoteApi.GetServerInformation );
		intent.putExtra( NoiseRemoteApi.RemoteCallReceiver, receiver );

		mContext.startService( intent );
	}

	@Override
	public Observable<BaseServerResult> setAudioDevice( final int deviceId ) {
		return( Observable.create( new Observable.OnSubscribe<BaseServerResult>() {
			@Override
			public void call( Subscriber<? super BaseServerResult> subscriber ) {
				try {
					subscriber.onNext( getService().SetAudioDevice( deviceId ));
					subscriber.onCompleted();
				}
				catch( Exception ex ) {
					subscriber.onError( ex );
				}
			}
		} ).subscribeOn( Schedulers.io()));
	}

	@Override
	public Observable<BaseServerResult> requestEvents( final String address ) {
		return( Observable.create( new Observable.OnSubscribe<BaseServerResult>() {
			@Override
			public void call( Subscriber<? super BaseServerResult> subscriber ) {
				try {
					subscriber.onNext( getService().RequestEvents( address ));
					subscriber.onCompleted();
				}
				catch( Exception ex ) {
					subscriber.onError( ex );
				}
			}
		} ).subscribeOn( Schedulers.io()));
	}

	@Override
	public Observable<BaseServerResult> revokeEvents( final String address ) {
		return( Observable.create( new Observable.OnSubscribe<BaseServerResult>() {
			@Override
			public void call( Subscriber<? super BaseServerResult> subscriber ) {
				try {
					subscriber.onNext( getService().RevokeEvents( address ) );
					subscriber.onCompleted();
				}
				catch( Exception ex ) {
					subscriber.onError( ex );
				}
			}
		} ).subscribeOn( Schedulers.io()));
	}
}
