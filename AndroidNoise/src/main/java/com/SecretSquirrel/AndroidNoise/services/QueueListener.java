package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 2/26/14.

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.SecretSquirrel.AndroidNoise.dto.PlayQueueListResult;
import com.SecretSquirrel.AndroidNoise.dto.PlayQueueTrack;
import com.SecretSquirrel.AndroidNoise.events.EventActivityPausing;
import com.SecretSquirrel.AndroidNoise.events.EventActivityResuming;
import com.SecretSquirrel.AndroidNoise.events.EventQueueTimeUpdate;
import com.SecretSquirrel.AndroidNoise.events.EventQueueUpdated;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseQueue;
import com.SecretSquirrel.AndroidNoise.interfaces.IQueueStatus;
import com.SecretSquirrel.AndroidNoise.support.Constants;

import java.util.ArrayList;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import rx.Subscription;
import rx.util.functions.Action1;

public class QueueListener implements IQueueStatus {
	private final String                TAG = QueueListener.class.getName();

	private final EventBus              mEventBus;
	private final INoiseQueue           mNoiseQueue;
	private final IApplicationState     mApplicationState;
	private final EventHostClient       mEventHostClient;
	private final Messenger             mMessenger;
	private ArrayList<PlayQueueTrack>   mQueueList;
	private Subscription                mQueueSubscription;
	private Messenger                   mService;
	private boolean                     mIsBound;

	private ServiceConnection           mConnection = new ServiceConnection() {
		public void onServiceConnected( ComponentName className, IBinder service ) {
			mService = new Messenger( service );

			try {
				Message message = Message.obtain( null, EventHostService.SERVER_EVENT_REGISTER_CLIENT );

				if( message != null ) {
					message.replyTo = mMessenger;

					mService.send( message );
				}
			} catch( RemoteException ex ) {
				if( Constants.LOG_ERROR ) {
					Log.e( TAG, "Sending register client.", ex );
				}
			}
		}

		public void onServiceDisconnected( ComponentName className ) {
			// This is called when the connection with the service has been unexpectedly disconnected - process crashed.
			mService = null;
		}
	};

	private class IncomingHandler extends Handler {
		@Override
		public void handleMessage( Message message ) {
			switch( message.what ) {
				case EventHostService.SERVER_EVENT_QUEUE_CHANGED:
					requestQueueList();
					break;

				default:
					super.handleMessage( message );
			}
		}
	}

	@Inject
	public QueueListener( EventBus eventBus, IApplicationState applicationState,
	                      INoiseQueue noiseQueue, EventHostClient eventHostClient ) {
		mEventBus = eventBus;
		mApplicationState = applicationState;
		mNoiseQueue = noiseQueue;
		mEventHostClient = eventHostClient;

		mQueueList = new ArrayList<PlayQueueTrack>();
		mMessenger = new Messenger( new IncomingHandler());

		mEventBus.register( this );

		if( mApplicationState.getIsConnected()) {
			bindToEventService();

			requestQueueList();
		}
	}

	public ArrayList<PlayQueueTrack> getPlayQueueItems() {
		ArrayList<PlayQueueTrack>   retValue = mQueueList;

		if( retValue == null ) {
			retValue = new ArrayList<PlayQueueTrack>();
		}

		return( retValue );
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventActivityResuming args ) {
		if( mApplicationState.getIsConnected()) {
			bindToEventService();

			requestQueueList();
		}
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventActivityPausing args ) {
		if( mQueueSubscription != null ) {
			mQueueSubscription.unsubscribe();
			mQueueSubscription = null;
		}

		unbindEventService();
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventServerSelected args ) {
		if( mApplicationState.getIsConnected()) {
			bindToEventService();

			requestQueueList();
		}
	}

	private void bindToEventService() {
		if(!mIsBound ) {
			mEventHostClient.registerForEvents( mConnection );

			mIsBound = true;
		}
	}

	private void unbindEventService() {
		if( mIsBound ) {
			if( mService != null ) {
				try {
					Message message = Message.obtain( null, EventHostService.SERVER_EVENT_UNREGISTER_CLIENT );

					if( message != null ) {
						message.replyTo = mMessenger;

						mService.send( message );
					}
				} catch( RemoteException ex ) {
					if( Constants.LOG_ERROR ) {
						Log.e( TAG, "unbindEventService", ex );
					}
				}
			}

			// Detach our existing connection.
			mEventHostClient.unregisterFromEvents( mConnection );
			mIsBound = false;
		}
	}

	private void requestQueueList() {
		if( mQueueSubscription != null ) {
			mQueueSubscription.unsubscribe();
			mQueueSubscription = null;
		}

		mQueueSubscription = mNoiseQueue
				.GetQueuedTrackList( new Action1<PlayQueueListResult>() {
					                     @Override
					                     public void call( PlayQueueListResult playQueueListResult ) {
						                     setQueueList( playQueueListResult.getTracks());
					                     }
				                     }, new Action1<Throwable>() {
					                     @Override
					                     public void call( Throwable throwable ) {
						                     setQueueList( null );

						                     if( Constants.LOG_ERROR ) {
							                     Log.e( TAG, "GetQueuedTrackList", throwable );
						                     }
					                     }
				                     }
				);
	}

	private void setQueueList( ArrayList<PlayQueueTrack> queueList ) {
		mQueueList = queueList;

		mEventBus.post( new EventQueueUpdated( getPlayQueueItems()));

		long    totalMilliseconds = 0;
		long    remainingMilliseconds = 0;

		if( mQueueList != null ) {
			for( PlayQueueTrack track : mQueueList ) {
				totalMilliseconds += track.getDurationMilliseconds();

				if(!track.getHasPlayed()) {
					remainingMilliseconds += track.getDurationMilliseconds();
				}
			}
		}

		mEventBus.post( new EventQueueTimeUpdate( totalMilliseconds, remainingMilliseconds ) );
	}
}
