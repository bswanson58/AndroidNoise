package com.SecretSquirrel.AndroidNoise.services;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.SecretSquirrel.AndroidNoise.services.rto.ServiceInformation;
import com.SecretSquirrel.AndroidNoise.support.NetworkUtility;
import com.SecretSquirrel.AndroidNoise.support.ThreadExecutor;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.schedulers.Schedulers;
import rx.subjects.ReplaySubject;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

// Created by BSwanson on 1/2/14.

public class ServiceLocatorObservable implements javax.jmdns.ServiceListener {
	private String                              mServiceType;
	private String                              mHostName;
	private JmDNS                               mZeroConfig;
	private ReplaySubject<ServiceInformation>   mSubject;
	private static WifiManager.MulticastLock    mLock;

	public static Observable<ServiceInformation> createServiceLocator(  Context context, String forServiceType, String hostName ) {
		ServiceLocatorObservable    locator = new ServiceLocatorObservable();

		return( locator.start( context, forServiceType, hostName ));
	}

	protected ServiceLocatorObservable() { }

	public Observable<ServiceInformation> start( final Context context, String forServiceType, String hostName ) {
		mServiceType = forServiceType;
		mHostName = hostName;

		mSubject = ReplaySubject.create();
		mSubject.subscribeOn( Schedulers.io());

		return( Observable
					.create( new Observable.OnSubscribe<ServiceInformation>() {
						@Override
						public void call( Subscriber<? super ServiceInformation> subscriber ) {
							startProbe( context );

							subscriber.add( Subscriptions.create( new Action0() {
									@Override
									public void call() {
										stopProbe();
									}
								} ));
						}
					} ).multicast( mSubject ).refCount());
	}

	private void startProbe( Context context ) {
		if( mLock == null ) {
			try {
				WifiManager wifiManager = (WifiManager) context.getSystemService( Context.WIFI_SERVICE );
				InetAddress address = NetworkUtility.getWirelessIpAddress( wifiManager );

				Timber.d( "Starting ZeroConfig probe, local address is: %s", address.toString());

				mLock = wifiManager.createMulticastLock( String.format( "%s lock", mHostName ));
				mLock.setReferenceCounted( true );
				mLock.acquire();

				mZeroConfig = JmDNS.create( address, mHostName );
				mZeroConfig.addServiceListener( mServiceType, this );
			}
			catch( Exception ex ) {
				mSubject.onError( ex );

				Timber.e( ex, "failure starting ZeroConf probe." );

				stopProbe();
			}
		}
		else {
			mSubject.onError( new Throwable( "Wifi lock is in use." ));
		}
	}

	private void stopProbe() {
		try {
			if( mZeroConfig != null ) {
				mZeroConfig.removeServiceListener( mServiceType, this );

				ThreadExecutor.runTask( new Runnable() {
					@Override
					public void run() {
						try {
							if( mZeroConfig != null ) {
								mZeroConfig.close();
								mZeroConfig = null;
							}
						}
						catch( IOException ex ) {
							Timber.e( ex, "ZeroConf error stopping probe" );
						}
					}
				} );
			}

			if( mLock != null ) {
				mLock.release();
				mLock = null;

				Timber.d( "Wifi lock released." );
			}
		}
		catch( Exception ex ) {
			Timber.e( ex, "Attempting to stop ZeroConf probe." );
		}
	}

	public void serviceAdded( ServiceEvent event ) {
		Timber.d( "Service Added: %s", event.getName());

		publishEvent( ServiceInformation.ServiceState.ServiceAdded, event.getName());
	}

	public void serviceRemoved( ServiceEvent event ) {
		Timber.d( "Service Removed: %s", event.getName());

		publishEvent( ServiceInformation.ServiceState.ServiceDeleted, event.getName() );
	}

	public void serviceResolved( ServiceEvent event ) {
		Timber.d( "Service Resolved: %s, host: %s, address: %s:%d", event.getName(), event.getInfo().getServer(), event.getInfo().getHostAddresses()[0], event.getInfo().getPort());

		publishEvent( ServiceInformation.ServiceState.ServiceResolved, event.getName());
	}

	private void publishEvent( ServiceInformation.ServiceState state, String serviceName ) {
		mSubject.onNext( new ServiceInformation( state, getServiceInfo( serviceName )));
	}

	private ServiceInfo getServiceInfo( String forName ) {
		return( mZeroConfig.getServiceInfo( mServiceType, forName ));
	}
}
