package com.SecretSquirrel.AndroidNoise.services.discovery;

// Secret Squirrel Software - Created by Bswanson on 7/18/14.

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subjects.ReplaySubject;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public class UdpEndpoint {
	private int                     mPort;
	private ReplaySubject<String>   mSubject;
	private boolean                 mContinueListening;

	protected UdpEndpoint( int listenPort ) {
		mPort = listenPort;
	}

	public static Observable<String> createListener( int listenPort ) {
		UdpEndpoint endPoint = new UdpEndpoint( listenPort );

		return( endPoint.createListener());
	}

	private Observable<String> createListener() {
		mSubject = ReplaySubject.create();

		return( Observable
				.create( new Observable.OnSubscribe<String>() {
					@Override
					public void call( Subscriber<? super String> subscriber ) {
						startListening();

						subscriber.add( Subscriptions.create( new Action0() {
							@Override
							public void call() {
								stopListening();
							}
						}));
					}
				} ).multicast( mSubject ).refCount());
	}

	private void startListening() {
		Thread listenThread = new Thread( new Runnable() {
			@Override
			public void run() {
				DatagramSocket  clientSocket = null;

				try {
					byte[]          receiveData = new byte[256];

					clientSocket = new DatagramSocket( mPort );

					while( mContinueListening ) {
						DatagramPacket receivePacket = new DatagramPacket( receiveData, receiveData.length );

						clientSocket.setSoTimeout( 3000 );

						try {
							clientSocket.receive( receivePacket );

							mSubject.onNext( new String( receiveData, 0, receivePacket.getLength()));
						} catch( SocketTimeoutException ex ) {
							// Time out allows checking for terminating the loop.
						}
					}
				} catch( Exception ex ) {
					Timber.e( ex, "Listening to UDP Endpoint" );
				}
				finally {
					if( clientSocket != null ) {
						clientSocket.disconnect();
						clientSocket.close();
					}
				}
			}
		} );

		mContinueListening = true;
		listenThread.start();
	}

	public void stopListening() {
		mContinueListening = false;
	}
}
