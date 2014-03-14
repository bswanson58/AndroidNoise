package com.SecretSquirrel.AndroidNoise.services;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.SecretSquirrel.AndroidNoise.services.rto.ServiceInformation;
import com.SecretSquirrel.AndroidNoise.support.Constants;
import com.SecretSquirrel.AndroidNoise.support.NetworkUtility;
import com.SecretSquirrel.AndroidNoise.support.ThreadExecutor;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.schedulers.Schedulers;
import timber.log.Timber;

// Created by BSwanson on 1/2/14.

public class ServiceLocatorObservable implements javax.jmdns.ServiceListener {
	private String                                  mServiceType;
	private String                                  mHostName;
	private Subscription                            mSubscription;
	private Observer<? super ServiceInformation>    mObserver;
	private JmDNS                                   mZeroConfig;
	private static WifiManager.MulticastLock        mLock;

	public static Observable<ServiceInformation> createServiceLocator(  Context context, String forServiceType, String hostName ) {
		ServiceLocatorObservable    locator = new ServiceLocatorObservable();

		return( locator.start( context, forServiceType, hostName ));
	}

	protected ServiceLocatorObservable() { }

	public Observable<ServiceInformation> start( final Context context, String forServiceType, String hostName ) {
		mServiceType = forServiceType;
		mHostName = hostName;

		mSubscription = new Subscription() {
			@Override
			public void unsubscribe() {
				if( mObserver != null ) {
					mObserver.onCompleted();
					mObserver = null;
				}

				stopProbe();
			}

			@Override
			public boolean isUnsubscribed() {
				return( mObserver == null );
			}
		};

		return( Observable.create( new Observable.OnSubscribeFunc<ServiceInformation>() {
				@Override
				public Subscription onSubscribe( Observer<? super ServiceInformation> observer ) {
					if( mLock == null ) {
						mObserver = observer;

						startProbe( context );
					}
					else {
						observer.onError( new Exception( "Only one subscription may be made to an active service locator" ));
					}

					return( mSubscription );
				}
			} ).subscribeOn( Schedulers.io()));
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
				mObserver.onError( ex );

				Timber.e( ex, "failure starting ZeroConf probe." );

				stopProbe();
			}
		}
	}

	private void stopProbe() {
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
		}
	}

	public void serviceAdded( ServiceEvent event ) {
		Timber.d( "Service Added (event=\n%s\n)", event.toString());

		publishEvent( ServiceInformation.ServiceState.ServiceAdded, event.getName());
	}

	public void serviceRemoved( ServiceEvent event ) {
		Timber.d( "Service Removed( event=\n%s\n)", event.toString());

		publishEvent( ServiceInformation.ServiceState.ServiceDeleted, event.getName() );
	}

	public void serviceResolved( ServiceEvent event ) {
		Timber.d( "Service Resolved (event=\n%s\n)", event.toString());

		publishEvent( ServiceInformation.ServiceState.ServiceResolved, event.getName());
	}

	private void publishEvent( ServiceInformation.ServiceState state, String serviceName ) {
		if( mObserver != null ) {
			mObserver.onNext( new ServiceInformation( state, getServiceInfo( serviceName )));
		}
	}

	private ServiceInfo getServiceInfo( String forName ) {
		return( mZeroConfig.getServiceInfo( mServiceType, forName ));
	}
}
