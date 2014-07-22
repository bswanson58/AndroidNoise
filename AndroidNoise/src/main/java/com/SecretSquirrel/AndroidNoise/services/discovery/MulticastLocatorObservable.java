package com.SecretSquirrel.AndroidNoise.services.discovery;

// Secret Squirrel Software - Created by BSwanson on 7/18/14.

import android.content.Context;

import com.SecretSquirrel.AndroidNoise.services.rto.ServiceInformation;

import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.ReplaySubject;
import rx.subscriptions.Subscriptions;

public class MulticastLocatorObservable {
	private static final String                 cDiscoveryRealm = "NoiseMusicSystem";
	private static final String                 cDiscoveryAddress = "239.10.30.58";
	private static final String                 cDiscoveryCommand = cDiscoveryRealm + "|ServerDiscovery";
	private static final String                 cDiscoveryResponse = "ServerEndpoint";
	private static final int                    cDiscoveryPort = 6502;
	private static final int                    cDiscoveryResponsePort = cDiscoveryPort + 1;

	private ReplaySubject<ServiceInformation>   mSubject;
	private MulticastEndpoint                   mDiscoveryMessenger;
	private Subscription                        mUdpListener;
	Timer                                       mDiscoveryTimer;

	public static Observable<ServiceInformation> createServiceLocator(  Context context ) {
		MulticastLocatorObservable    locator = new MulticastLocatorObservable();

		return( locator.start( context ));
	}

	protected MulticastLocatorObservable() { }

	public Observable<ServiceInformation> start( final Context context ) {
		mSubject = ReplaySubject.create();
		mSubject.subscribeOn( Schedulers.io() );

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
				} ).subscribeOn( Schedulers.io()).multicast( mSubject ).refCount());
	}

	private void startProbe( Context context ) {
		mDiscoveryMessenger = new MulticastEndpoint( context, cDiscoveryAddress, cDiscoveryPort );
		mUdpListener = UdpEndpoint.createListener( cDiscoveryResponsePort ).subscribe( new Action1<String>() {
			@Override
			public void call( String s ) {
				StringTokenizer tokens = new StringTokenizer( s, "|" );
				String  realm = tokens.nextToken();
				String  command = tokens.nextToken();
				String serviceAddress = tokens.nextToken();

				if(( realm.equalsIgnoreCase( cDiscoveryRealm )) &&
				   ( command.equalsIgnoreCase( cDiscoveryResponse )) &&
				   ( serviceAddress.length() > 0 )) {

					mSubject.onNext( new ServiceInformation( ServiceInformation.ServiceState.ServiceResolved, "", serviceAddress ));
				}
			}
		} );

		mDiscoveryTimer = new Timer();
		mDiscoveryTimer.scheduleAtFixedRate( new TimerTask() {
			@Override
			public void run() {
				sendDiscoveryProbe();
			}
		}, 50, 3000 );
	}

	private void sendDiscoveryProbe() {
		mDiscoveryMessenger.sendMessage( cDiscoveryCommand );
	}

	private void stopProbe() {
		mDiscoveryTimer.cancel();
		mDiscoveryTimer.purge();

		mUdpListener.unsubscribe();
	}
}
