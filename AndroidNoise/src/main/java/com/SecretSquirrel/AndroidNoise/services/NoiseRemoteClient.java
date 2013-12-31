package com.SecretSquirrel.AndroidNoise.services;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;

import com.SecretSquirrel.AndroidNoise.interfaces.INoiseServer;
import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;
import com.SecretSquirrel.AndroidNoise.services.rto.RemoteServerRestApi;

import retrofit.RestAdapter;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.concurrency.Schedulers;
import rx.subscriptions.Subscriptions;

// Secret Squirrel Software - Created by bswanson on 12/6/13.

public class NoiseRemoteClient implements INoiseServer {
	private Context             mContext;
	private String              mServerAddress;
	private RemoteServerRestApi mService;

	public NoiseRemoteClient( Context context, String serverAddress ) {
		mContext = context;
		mServerAddress = serverAddress;
	}

	public void getServerVersion( ResultReceiver receiver ) {
		setupAndCallApi( NoiseRemoteApi.GetServerVersion, receiver );
	}

	private void setupAndCallApi( int apiCode, ResultReceiver resultReceiver ) {
		Intent  intent = new Intent( mContext, NoiseRemoteService.class );

		intent.putExtra( NoiseRemoteApi.RemoteServerAddress, mServerAddress );
		intent.putExtra( NoiseRemoteApi.RemoteApiParameter, apiCode );
		intent.putExtra( NoiseRemoteApi.RemoteCallReceiver, resultReceiver );

		mContext.startService( intent );
	}

	@Override
	public Observable<BaseServerResult> requestEvents( final String address ) {
		return( Observable.create( new Observable.OnSubscribeFunc<BaseServerResult>() {
			@Override
			public Subscription onSubscribe( Observer<? super BaseServerResult> observer ) {
				try {
					observer.onNext( getService().RequestEvents( address ));
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
					observer.onNext( getService().RevokeEvents( address ));
					observer.onCompleted();
				}
				catch( Exception ex ) {
					observer.onError( ex );
				}

				return( Subscriptions.empty());
			}
		} ).subscribeOn( Schedulers.threadPoolForIO()));
	}

	private RemoteServerRestApi getService() {
		if( mService == null ) {
			mService = buildService( mServerAddress );
		}

		return( mService );
	}

	private RemoteServerRestApi buildService( String serverAddress ) {
		RestAdapter restAdapter = new RestAdapter.Builder()
				.setServer( serverAddress )
				.build();

		return( restAdapter.create( RemoteServerRestApi.class ));
	}
}
