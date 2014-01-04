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
import rx.concurrency.Schedulers;
import rx.util.functions.Action1;

public class ServiceLocator {
	public final static String                  NOISE_TYPE = "_Noise._Tcp.local.";
	public final static String                  HOSTNAME = "NoiseRemote";

	private Observer<? super ServerInformation> mObserver;
	private Subscription                        mSubscription;
	private Subscription                        mLocatorSubscription;
	private Context                             mContext;

	public static Observable<ServerInformation> createServiceLocator( final Context context ) {
		ServiceLocator  locator = new ServiceLocator();

		return( locator.createLocator( context ));
	}

	protected ServiceLocator() { }

	private Observable<ServerInformation> createLocator( final Context context ) {
		mContext = context;

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

		return( Observable.create( new Observable.OnSubscribeFunc<ServerInformation>() {
			@Override
			public Subscription onSubscribe( Observer<? super ServerInformation> observer ) {
				mObserver = observer;

				mLocatorSubscription = ServiceLocatorObservable.createServiceLocator( mContext, NOISE_TYPE, HOSTNAME )
					.subscribe( new Action1<ServiceInformation>() {
					@Override
					public void call( ServiceInformation s ) {
								// Only return results on services Resolved or Deleted.
								if( s.getServiceState() != ServiceInformation.ServiceState.ServiceAdded ) {
									onServiceInformation( s );
								}
								}
							},
							new Action1<Throwable>() {
		                     @Override
		                     public void call( Throwable throwable ) {
		                         mObserver.onError( throwable );
		                     }
		                 } );

				return( mSubscription );
			}
		} )).subscribeOn( Schedulers.threadPoolForIO());
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