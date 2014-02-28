package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 1/2/14.

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.SecretSquirrel.AndroidNoise.interfaces.INoiseServer;
import com.SecretSquirrel.AndroidNoise.nanoHttpd.NanoHTTPD;
import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;
import com.SecretSquirrel.AndroidNoise.support.Constants;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;
import com.SecretSquirrel.AndroidNoise.support.NetworkUtility;

import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;

import rx.Subscription;
import rx.util.functions.Action1;

public class EventHostService extends Service {
	private static final String TAG                             = EventHostService.class.getName();

	private static final int    EVENT_PORT                      = 6502;

	public static final int     SERVER_EVENT_REGISTER_CLIENT    = 1;
	public static final int     SERVER_EVENT_UNREGISTER_CLIENT  = 2;

	public static final int     SERVER_EVENT_QUEUE_CHANGED      = 3;
	public static final int     SERVER_EVENT_TRANSPORT_CHANGED  = 4;

	private ServerEventHost             mEventHost;
	private final ArrayList<Messenger>  mClients;
	private final Messenger             mMessenger;
	private String                      mLocalAddress;
	private Subscription                mEventRequestSubscription;
	private boolean                     mIsRunning;

	@Inject	INoiseServer                mNoiseServer;

	private class IncomingHandler extends Handler { // Handler of incoming messages from clients.
		@Override
		public void handleMessage( Message msg ) {
			switch( msg.what ) {
				case SERVER_EVENT_REGISTER_CLIENT:
					addClient( msg.replyTo );
					break;

				case SERVER_EVENT_UNREGISTER_CLIENT:
					removeClient( msg.replyTo );
					break;

				default:
					super.handleMessage( msg );
			}
		}
	}

	public EventHostService() {
		mClients = new ArrayList<Messenger>();
		mMessenger = new Messenger( new IncomingHandler());
		mIsRunning = false;
	}

	@Override
	public IBinder onBind( Intent intent ) {
		IocUtility.inject( this  );

		return( mMessenger.getBinder());
	}

	private void publishMessage( int eventCode, Map<String, String> parameters ) {
		for( int index = mClients.size() - 1; index >= 0; index-- ) {
			try {
				Message message = Message.obtain( null, eventCode );

				if( message != null ) {
					Bundle  bundle = new Bundle();

					for( String key : parameters.keySet()) {
						bundle.putString( key, parameters.get( key ));
					}
					message.setData( bundle );
					mClients.get( index ).send( message );
				}
			}
			catch( RemoteException e ) {
				// The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
				mClients.remove( index );
			}
		}
	}

	@Override
	public int onStartCommand( Intent intent, int flags, int startId ) {
		if( Constants.LOG_DEBUG ) {
			Log.i( "AndroidNoise:EventHostService", "Received start id " + startId + ": " + intent );
		}

		return START_STICKY; // run until explicitly stopped.
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		stopEventHost();

		if( Constants.LOG_DEBUG ) {
			Log.i( "AndroidNoise:EventHostService", "Service Stopped." );
		}

		mIsRunning = false;
	}

	private void addClient( Messenger client ) {
		if(!mClients.contains( client )) {
			mClients.add( client );
		}

		startEventHost();
	}

	private void removeClient( Messenger client ) {
		if( mClients.contains( client )) {
			mClients.remove( client );
		}

		if( mClients.size() == 0 ) {
			stopEventHost();

			stopSelf();
		}
	}

	private void startEventHost() {
		if(!mIsRunning ) {
			subscribeToEvents();

			mIsRunning = true;
		}
	}

	private void stopEventHost() {
		if( mIsRunning ) {
			revokeEvents();
			mEventHost.stop();

			mIsRunning = false;
		}
	}

	private void subscribeToEvents() {
		configureEventHost();

		try {
			mEventRequestSubscription = mNoiseServer.requestEvents( mLocalAddress )
					.subscribe( new Action1<BaseServerResult>() {
						            @Override
						            public void call( BaseServerResult serverResult ) {
							            startHost();
						            }
					            }, new Action1<Throwable>() {
						            @Override
						            public void call( Throwable throwable ) {
							            if( Constants.LOG_ERROR ) {
								            Log.e( TAG, "Subscribing to Noise events", throwable );
							            }
						            }
					            }
					);
		}
		catch( Exception ex ) {
			if( Constants.LOG_ERROR ) {
				Log.e( TAG, "subscribeToEvents", ex );
			}
		}
	}

	private void configureEventHost() {
		mLocalAddress = String.format( "http://%s:%d", NetworkUtility.getLocalAddress(), EVENT_PORT );
		mEventHost = new ServerEventHost( EVENT_PORT );

		mEventHost.AddResponder( new ServerEventHost.UriResponder() {
			@Override
			public boolean shouldServe( NanoHTTPD.IHTTPSession session ) {
				return (session.getUri().startsWith( "/eventInQueue" ));
			}

			@Override
			public NanoHTTPD.Response serve( NanoHTTPD.IHTTPSession session ) {
				publishMessage( SERVER_EVENT_QUEUE_CHANGED, session.getParms());

				return( new NanoHTTPD.Response( "OK" ));
			}
		} );

		mEventHost.AddResponder( new ServerEventHost.UriResponder() {
			@Override
			public boolean shouldServe( NanoHTTPD.IHTTPSession session ) {
				return (session.getUri().startsWith( "/eventInTransport" ));
			}

			@Override
			public NanoHTTPD.Response serve( NanoHTTPD.IHTTPSession session ) {
				publishMessage( SERVER_EVENT_TRANSPORT_CHANGED, session.getParms());

				return( new NanoHTTPD.Response( "OK" ));
			}
		} );
	}

	private void startHost() {
		mEventHost.start();

		if( mEventRequestSubscription != null ) {
			mEventRequestSubscription.unsubscribe();
			mEventRequestSubscription = null;
		}
	}

	private void revokeEvents() {
		if(!TextUtils.isEmpty( mLocalAddress )) {
			mEventRequestSubscription = mNoiseServer.revokeEvents( mLocalAddress )
					.subscribe( new Action1<BaseServerResult>() {
						            @Override
						            public void call( BaseServerResult serverResult ) {
							            // all is well.
						            }
					            }, new Action1<Throwable>() {
						            @Override
						            public void call( Throwable throwable ) {
							            if( Constants.LOG_ERROR ) {
								            Log.e( TAG, "Subscribing to Noise events", throwable );
							            }
						            }
					            } );
		}
	}
}

