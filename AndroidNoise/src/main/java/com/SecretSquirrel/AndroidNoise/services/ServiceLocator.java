package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by BSwanson on 1/3/14.

import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.SecretSquirrel.AndroidNoise.dto.ServerInformation;
import com.SecretSquirrel.AndroidNoise.services.noiseApi.RemoteServerRestApi;
import com.SecretSquirrel.AndroidNoise.services.rto.ServiceInformation;

import retrofit.RestAdapter;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public class ServiceLocator {
	private final static String     NOISE_TYPE = "_Noise._Tcp.local.";
	private final static String     HOSTNAME = "NoiseRemote";

	public static Observable<ServerInformation> createServiceLocator( final Context context ) {
		ServiceLocator  locator = new ServiceLocator();

		return( locator.createLocator( context ));
	}

	protected ServiceLocator() { }

	private Observable<ServerInformation> createLocator( final Context context ) {
		return( Observable
				.create( new Observable.OnSubscribe<ServerInformation>() {
					@Override
					public void call( final Subscriber<? super ServerInformation> subscriber ) {
						final Subscription locatorSubscription = ServiceLocatorObservable
								.createServiceLocator( context, NOISE_TYPE, HOSTNAME )
								.subscribe( new Action1<ServiceInformation>() {
									            @Override
									            public void call( ServiceInformation s ) {
										            // Only return results on services Resolved or Deleted.
										            if((!subscriber.isUnsubscribed()) &&
												      (( s.getServiceState() == ServiceInformation.ServiceState.ServiceResolved ) ||
													   ( s.getServiceState() == ServiceInformation.ServiceState.ServiceDeleted ))) {
											            onServiceInformation( s, subscriber, context );
										            }
									            }
								            }, new Action1<Throwable>() {
									            @Override
									            public void call( Throwable throwable ) {
										            subscriber.onError( throwable );
									            }
								            }
								);

						subscriber.add( Subscriptions.create( new Action0() {
							@Override
							public void call() {
								locatorSubscription.unsubscribe();
							}
						} ));
					}
				} ));
	}

	private static void onServiceInformation( final ServiceInformation serviceInformation,
	                                          final Subscriber<? super ServerInformation> subscriber,
	                                          final Context context ) {
		Timber.d( "Retrieving server information for %s", serviceInformation.getHostAddress());

		NoiseRemoteClient   remoteClient = new NoiseRemoteClient( createAdapter( serviceInformation.getHostAddress()),
																  serviceInformation.getHostAddress(), context );

		remoteClient.getServerInformation( new ResultReceiver( null ) {
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
					ServerInformation   serverInformation = resultData.getParcelable( NoiseRemoteApi.ServerInformation );

					if( serverInformation != null ) {
						serverInformation.setServiceInformation( serviceInformation );

						if(!subscriber.isUnsubscribed()) {
							subscriber.onNext( serverInformation );
						}
					}
				}
			}
		} );
	}

	private static RemoteServerRestApi createAdapter( String serverAddress ) {
		RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint( serverAddress ).build();

		return( restAdapter.create( RemoteServerRestApi.class ));
	}
}