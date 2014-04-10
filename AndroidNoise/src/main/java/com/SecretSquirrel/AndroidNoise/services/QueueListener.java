package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by BSwanson on 2/26/14.

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.SecretSquirrel.AndroidNoise.dto.PlayQueueListResult;
import com.SecretSquirrel.AndroidNoise.dto.PlayQueueTrack;
import com.SecretSquirrel.AndroidNoise.events.EventActivityPausing;
import com.SecretSquirrel.AndroidNoise.events.EventActivityResuming;
import com.SecretSquirrel.AndroidNoise.events.EventLibraryStateChange;
import com.SecretSquirrel.AndroidNoise.events.EventQueueTimeUpdate;
import com.SecretSquirrel.AndroidNoise.events.EventQueueUpdated;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.events.EventTransportUpdate;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseQueue;
import com.SecretSquirrel.AndroidNoise.interfaces.IQueueStatus;

import java.util.ArrayList;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import rx.Subscription;
import rx.functions.Action1;
import timber.log.Timber;

public class QueueListener implements IQueueStatus {
	private final EventBus              mEventBus;
	private final INoiseQueue           mNoiseQueue;
	private final IApplicationState     mApplicationState;
	private final EventHostClient       mEventHostClient;
	private final Messenger             mMessenger;
	private ArrayList<PlayQueueTrack>   mQueueList;
	private PlayQueueTrack              mCurrentlyPlayingTrack;
	private Subscription                mQueueSubscription;
	private Messenger                   mService;
	private boolean                     mIsBound;
	private boolean                     mAreTracksQueued;
	private boolean                     mAreTracksPlayed;
	private int                         mServerSequence;

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
				Timber.e( ex, "Sending register client." );
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

				case EventHostService.SERVER_EVENT_TRANSPORT_CHANGED:
					publishTransportEvent( message.getData());
					break;

				case EventHostService.SERVER_EVENT_LIBRARY_CHANGED:
					publishLibraryEvent( message.getData());
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

	@Override
	public PlayQueueTrack getCurrentlyPlayingTrack() {
		return( mCurrentlyPlayingTrack );
	}

	@Override
	public boolean areTracksQueued() {
		return( mAreTracksQueued );
	}

	@Override
	public boolean areTracksPlayed() {
		return( mAreTracksPlayed );
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

		mQueueList.clear();
		unbindEventService();
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventServerSelected args ) {
		mQueueList.clear();

		if( mApplicationState.getIsConnected()) {
			mServerSequence = 0;

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
					Timber.e( ex, "unbindEventService" );
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

						                     Timber.e( throwable, "GetQueuedTrackList" );
					                     }
				                     }
				);
	}

	private void setQueueList( ArrayList<PlayQueueTrack> queueList ) {
		mQueueList = queueList;
		mCurrentlyPlayingTrack = null;
		mAreTracksQueued = false;
		mAreTracksPlayed = false;

		long    totalMilliseconds = 0;
		long    remainingMilliseconds = 0;

		if( mQueueList != null ) {
			for( PlayQueueTrack track : mQueueList ) {
				totalMilliseconds += track.getDurationMilliseconds();

				if(!track.getHasPlayed()) {
					remainingMilliseconds += track.getDurationMilliseconds();
				}
				else {
					mAreTracksPlayed = true;
				}

				if( track.isPlaying()) {
					mCurrentlyPlayingTrack = track;
				}

				mAreTracksQueued = true;
			}
		}

		mEventBus.post( new EventQueueUpdated());
		mEventBus.post( new EventQueueTimeUpdate( totalMilliseconds, remainingMilliseconds ));
	}

	private void publishTransportEvent( Bundle data ) {
		int serverSequence = Integer.parseInt( data.getString( "sequence" ));

		if( serverSequence >= mServerSequence ) {
			EventTransportUpdate    transportUpdate = new EventTransportUpdate( Integer.parseInt( data.getString( "state" )),
																				Long.parseLong( data.getString( "time" )),
																				Long.parseLong( data.getString( "track" )),
																				Long.parseLong( data.getString( "position" )),
																				Long.parseLong( data.getString( "length" )));
			mServerSequence = serverSequence;
			mEventBus.post( transportUpdate );

			Timber.i( "TransportUpdate - Sequence: %s, State: %s, Position: %s", data.getString( "sequence" ), data.getString( "state" ), data.getString( "position" ));
		}
		else {
			Timber.i( "TransportUpdate - out of sequence message received: %d", serverSequence );
		}
	}

	private void publishLibraryEvent( Bundle data ) {
		EventLibraryStateChange event = new EventLibraryStateChange( data.getString( "changed" ));

		mEventBus.post( event );
	}
}
