package com.SecretSquirrel.AndroidNoise.services;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.SecretSquirrel.AndroidNoise.dto.ServerInformation;
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

	private final String                mServiceType;
	private final String                mHostName;
	private Subscription                mSubscription;
	private Observer<? super String>    mObserver;
	private JmDNS                       mZeroConfig;
	private WifiManager.MulticastLock   mLock;

	public ServiceLocatorObservable( String forServiceType, String hostName ) {
		mServiceType = forServiceType;
		mHostName = hostName;
	}

	public Observable<String> start( final Context context ) {
		mSubscription = new Subscription() {
			@Override
			public void unsubscribe() {
				mObserver.onCompleted();

				stopProbe();
			}
		};

		return( Observable.create( new Observable.OnSubscribeFunc<String>() {
			@Override
			public Subscription onSubscribe( Observer<? super String> observer ) {
				startProbe( context );

				mObserver = observer;

				return( mSubscription );
			}
		} ).subscribeOn( Schedulers.threadPoolForIO()));
	}

	private void startProbe( Context context ) {
		try {
			WifiManager wifi = (WifiManager) context.getSystemService( Context.WIFI_SERVICE );
			InetAddress address = NetworkUtility.getWirelessIpAddress( wifi );

			Log.d( TAG, String.format( "Local address is: %s", address.toString() ) );

			// start multicast lock
			mLock = wifi.createMulticastLock( String.format( "%s lock", mHostName ));
			mLock.setReferenceCounted( true );
			mLock.acquire();

			mZeroConfig = JmDNS.create( address, mHostName );
			//ServiceInfo[]   services = mZeroConfig.list( NOISE_TYPE );
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

	private final static int DELAY = 500;

	public void serviceAdded( ServiceEvent event ) {
		Log.w( TAG, String.format( "serviceAdded(event=\n%s\n)", event.toString()));
		//final String name = event.getName();

		// trigger delayed gui event
		// needs to be delayed because jmdns hasn't parsed txt info yet
		//resultsUpdated.sendMessageDelayed( Message.obtain( resultsUpdated, -1, name ), DELAY );
	}

	public void serviceRemoved( ServiceEvent event ) {
		Log.w( TAG, String.format( "serviceRemoved(event=\n%s\n)", event.toString()));
	}

	public void serviceResolved( ServiceEvent event ) {
		Log.w( TAG, String.format( "serviceResolved(event=\n%s\n)", event.toString()));

		if( mObserver != null ) {
			ServiceInfo serviceInfo = mZeroConfig.getServiceInfo( mServiceType, event.getName());

			if( serviceInfo != null ) {
				mObserver.onNext( serviceInfo.getInetAddresses()[0].toString());
			}
		}
	}
}
