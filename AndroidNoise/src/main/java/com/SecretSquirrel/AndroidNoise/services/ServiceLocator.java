package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 1/3/14.

import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.SecretSquirrel.AndroidNoise.dto.ServerInformation;
import com.SecretSquirrel.AndroidNoise.dto.ServerVersion;
import com.SecretSquirrel.AndroidNoise.services.rto.ServiceInformation;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.concurrency.AndroidSchedulers;
import rx.util.functions.Action1;

public class ServiceLocator {
	public final static String                  NOISE_TYPE = "_Noise._Tcp.local.";
	public final static String                  HOSTNAME = "NoiseRemote";

	private ServiceLocatorObservable            mServiceLocator;
	private Observable<ServiceInformation>      mServiceObservable;
	private Observer<? super ServerInformation> mObserver;
	private Subscription                        mSubscription;
	private Subscription                        mLocatorSubscription;
	private Context                             mContext;

	public Observable<ServerInformation> startServiceLocator( final Context context ) {
		mContext = context;

		return( Observable.create( new Observable.OnSubscribeFunc<ServerInformation>() {
			@Override
			public Subscription onSubscribe( Observer<? super ServerInformation> observer ) {
				mObserver = observer;

				mSubscription = new Subscription() {
					@Override
					public void unsubscribe() {
						if( mLocatorSubscription != null ) {
							mLocatorSubscription.unsubscribe();
							mLocatorSubscription = null;
						}

						if( mObserver != null ) {
							mObserver.onCompleted();
							mObserver = null;
						}
					}
				};

				mServiceLocator = new ServiceLocatorObservable( NOISE_TYPE, HOSTNAME );
				mServiceObservable = mServiceLocator.start( mContext );
				mLocatorSubscription = mServiceObservable.observeOn( AndroidSchedulers.mainThread()).subscribe( new Action1<ServiceInformation>() {
					@Override
					public void call( ServiceInformation s ) {
						// Only return results on services Resolved or Deleted.
						if( s.getServiceState() != ServiceInformation.ServiceState.ServiceAdded ) {
							onServiceInformation( s );
						}
					}
				}, new Action1<Throwable>() {
                     @Override
                     public void call( Throwable throwable ) {
                         mObserver.onError( throwable );
                     }
                 } );

				return( mSubscription );
			}
		} ));
	}

	private void onServiceInformation( final ServiceInformation serviceInformation ) {
		NoiseRemoteClient   remoteClient = new NoiseRemoteClient( mContext, serviceInformation.getHostAddress());

		remoteClient.getServerVersion( new ResultReceiver( null ) {
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
					ServerVersion   serverVersion = resultData.getParcelable( NoiseRemoteApi.RemoteResultVersion );

					if( serverVersion != null ) {
						mObserver.onNext( new ServerInformation( serviceInformation, serverVersion ) );
					}
				}
			}} );
	}
}