package com.SecretSquirrel.AndroidNoise.services;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.SecretSquirrel.AndroidNoise.services.rto.ServiceInformation;
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
import rx.concurrency.Schedulers;

// Created by BSwanson on 1/2/14.

public class ServiceLocatorObservable implements javax.jmdns.ServiceListener {
	private final static String         TAG = ServiceLocatorObservable.class.getName();

	private final String                            mServiceType;
	private final String                            mHostName;
	private Subscription                            mSubscription;
	private Observer<? super ServiceInformation>    mObserver;
	private JmDNS                                   mZeroConfig;
	private WifiManager.MulticastLock               mLock;

	public ServiceLocatorObservable( String forServiceType, String hostName ) {
		mServiceType = forServiceType;
		mHostName = hostName;
	}

	public Observable<ServiceInformation> start( final Context context ) {
		mSubscription = new Subscription() {
			@Override
			public void unsubscribe() {
				mObserver.onCompleted();

				stopProbe();
			}
		};

		return( Observable.create( new Observable.OnSubscribeFunc<ServiceInformation>() {
			@Override
			public Subscription onSubscribe( Observer<? super ServiceInformation> observer ) {
				startProbe( context );

				mObserver = observer;

				return( mSubscription );
			}
		} ).subscribeOn( Schedulers.threadPoolForIO()));
	}

	private void startProbe( Context context ) {
		try {
			WifiManager wifiManager = (WifiManager) context.getSystemService( Context.WIFI_SERVICE );
			InetAddress address = NetworkUtility.getWirelessIpAddress( wifiManager );

			Log.d( TAG, String.format( "Local address is: %s", address.toString()));

			mLock = wifiManager.createMulticastLock( String.format( "%s lock", mHostName ));
			mLock.setReferenceCounted( true );
			mLock.acquire();

			mZeroConfig = JmDNS.create( address, mHostName );
			mZeroConfig.addServiceListener( mServiceType, this );
		}
		catch( Exception ex ) {
			mObserver.onError( ex );

			stopProbe();
		}
	}

	private void stopProbe() {
		mZeroConfig.removeServiceListener( mServiceType, this );

		ThreadExecutor.runTask( new Runnable() {
			@Override
			public void run() {
				try {
					mZeroConfig.close();
					mZeroConfig = null;
				}
				catch( IOException e ) {
					Log.d( TAG, String.format( "ZeroConf Error: %s", e.getMessage() ) );
				}
			}
		} );

		if( mLock != null ) {
			mLock.release();
			mLock = null;
		}

		mObserver = null;
	}

	public void serviceAdded( ServiceEvent event ) {
		Log.w( TAG, String.format( "Service Added (event=\n%s\n)", event.toString()));

		publishEvent( ServiceInformation.ServiceState.ServiceAdded, event.getName());
	}

	public void serviceRemoved( ServiceEvent event ) {
		Log.w( TAG, String.format( "Service Removed( event=\n%s\n)", event.toString()));

		publishEvent( ServiceInformation.ServiceState.ServiceDeleted, event.getName() );
	}

	public void serviceResolved( ServiceEvent event ) {
		Log.w( TAG, String.format( "Service Resolved (event=\n%s\n)", event.toString()));

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
