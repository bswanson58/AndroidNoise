package com.SecretSquirrel.AndroidNoise.services;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;

import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseServer;
import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;
import com.SecretSquirrel.AndroidNoise.services.noiseApi.RemoteServerRestApi;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.concurrency.Schedulers;
import rx.subscriptions.Subscriptions;

// Secret Squirrel Software - Created by bswanson on 12/6/13.

public class NoiseRemoteClient implements INoiseServer {
	private final Context               mContext;
	private final String                mServerAddress;
	private final RemoteServerRestApi   mService;

	@Inject
	public NoiseRemoteClient( RemoteServerRestApi noiseServer, IApplicationState applicationState, Context context ) {
		mServerAddress = applicationState.getCurrentServer().getServerAddress();
		mService = noiseServer;
		mContext = context;
	}

	public NoiseRemoteClient( RemoteServerRestApi noiseServer, String serverAddress, Context context ) {
		mServerAddress = serverAddress;
		mService = noiseServer;
		mContext = context;
	}

	public void getServerVersion( ResultReceiver receiver ) {
		Intent  intent = new Intent( mContext, NoiseRemoteService.class );

		intent.putExtra( NoiseRemoteApi.RemoteServerAddress, mServerAddress );
		intent.putExtra( NoiseRemoteApi.RemoteApiParameter, NoiseRemoteApi.GetServerVersion );
		intent.putExtra( NoiseRemoteApi.RemoteCallReceiver, receiver );

		mContext.startService( intent );
	}

	@Override
	public Observable<BaseServerResult> requestEvents( final String address ) {
		return( Observable.create( new Observable.OnSubscribeFunc<BaseServerResult>() {
			@Override
			public Subscription onSubscribe( Observer<? super BaseServerResult> observer ) {
				try {
					observer.onNext( mService.RequestEvents( address ) );
					observer.onCompleted();
				}
				catch( Exception ex ) {
					observer.onError( ex );
				}

				return( Subscriptions.empty());
			}
		} ).subscribeOn( Schedulers.threadPoolForIO()));
	}

	@Override
	public Observable<BaseServerResult> revokeEvents( final String address ) {
		return( Observable.create( new Observable.OnSubscribeFunc<BaseServerResult>() {
			@Override
			public Subscription onSubscribe( Observer<? super BaseServerResult> observer ) {
				try {
					observer.onNext( mService.RevokeEvents( address ) );
					observer.onCompleted();
				}
				catch( Exception ex ) {
					observer.onError( ex );
				}

				return( Subscriptions.empty());
			}
		} ).subscribeOn( Schedulers.threadPoolForIO()));
	}
}
