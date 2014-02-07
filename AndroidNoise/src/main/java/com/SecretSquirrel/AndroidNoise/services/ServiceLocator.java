package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 1/3/14.

import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.SecretSquirrel.AndroidNoise.dto.ServerInformation;
import com.SecretSquirrel.AndroidNoise.dto.ServerVersion;
import com.SecretSquirrel.AndroidNoise.services.noiseApi.RemoteServerRestApi;
import com.SecretSquirrel.AndroidNoise.services.rto.ServiceInformation;

import retrofit.RestAdapter;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.concurrency.Schedulers;
import rx.util.functions.Action1;

public class ServiceLocator {
	public final static String                  NOISE_TYPE = "_Noise._Tcp.local.";
	public final static String                  HOSTNAME = "NoiseRemote";

	public static Observable<ServerInformation> createServiceLocator( final Context context ) {
		ServiceLocator  locator = new ServiceLocator();

		return( locator.createLocator( context ));
	}

	protected ServiceLocator() { }

	private Observable<ServerInformation> createLocator( final Context context ) {
		return( Observable.create( new Observable.OnSubscribeFunc<ServerInformation>() {
			@Override
			public Subscription onSubscribe( final Observer<? super ServerInformation> observer ) {
				final Subscription locatorSubscription = ServiceLocatorObservable
						.createServiceLocator( context, NOISE_TYPE, HOSTNAME )
							.subscribe( new Action1<ServiceInformation>() {
	                                @Override
	                                public void call( ServiceInformation s ) {
	                                    // Only return results on services Resolved or Deleted.
	                                    if( s.getServiceState() != ServiceInformation.ServiceState.ServiceAdded ) {
	                                        onServiceInformation( s, observer, context );
	                                    }
	                                }
	                            }, new Action1<Throwable>() {
									@Override
									public void call( Throwable throwable ) {
										observer.onError( throwable );
									}
								}
						);

				return( new Subscription() {
					@Override
					public void unsubscribe() {
						locatorSubscription.unsubscribe();

						observer.onCompleted();
					}
				});
			}
		} )).subscribeOn( Schedulers.threadPoolForIO());
	}

	private static void onServiceInformation( final ServiceInformation serviceInformation,
	                                          final Observer<? super ServerInformation> observer,
	                                          final Context context ) {
		NoiseRemoteClient   remoteClient = new NoiseRemoteClient( createAdapter(serviceInformation.getHostAddress()),
																  serviceInformation.getHostAddress(), context );

		remoteClient.getServerVersion( new ResultReceiver( null ) {
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
					ServerVersion   serverVersion = resultData.getParcelable( NoiseRemoteApi.RemoteResultVersion );

					if( serverVersion != null ) {
						observer.onNext( new ServerInformation( serviceInformation, serverVersion ));
					}
				}
			}} );
	}

	private static RemoteServerRestApi createAdapter( String serverAddress ) {
		RestAdapter restAdapter = new RestAdapter.Builder().setServer( serverAddress ).build();

		return( restAdapter.create( RemoteServerRestApi.class ));
	}
}