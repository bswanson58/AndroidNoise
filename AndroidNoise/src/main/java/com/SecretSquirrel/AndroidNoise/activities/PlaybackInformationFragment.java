package com.SecretSquirrel.AndroidNoise.activities;

// Created by BSwanson on 2/27/14.

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.ServerTimeSync;
import com.SecretSquirrel.AndroidNoise.dto.TransportState;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.events.EventTransportUpdate;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseTransport;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import rx.android.observables.AndroidObservable;
import rx.util.functions.Action1;

public class PlaybackInformationFragment extends Fragment {
	private static final String TAG = PlaybackInformationFragment.class.getName();

	private long                mServerTimeOffset;
	private long                mCurrentTrack;
	private long                mLastPosition;
	private long                mLastReceived;
	private long                mTrackLength;
	private int                 mPlayState;
	private Handler             mTimerHandler;

	private Runnable            mTimerRunnable = new Runnable() {
		@Override
		public void run() {
			updateDisplay();

			mTimerHandler.postDelayed( this, 300 );
		}
	};

	@Inject	EventBus            mEventBus;
	@Inject	IApplicationState   mApplicationState;
	@Inject	INoiseTransport     mNoiseTransport;

	@InjectView( R.id.pi_track_position )	TextView    mPlaybackPosition;

	public static PlaybackInformationFragment newInstance() {
		return( new PlaybackInformationFragment());
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		IocUtility.inject( this );

		mTimerHandler = new Handler();
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_playback_information, container, false );

		if( myView != null ) {
			ButterKnife.inject( this, myView );
		}

		return( myView );
	}

	@Override
	public void onResume() {
		super.onResume();

		if( mApplicationState.getIsConnected()) {
			syncTime();
		}

		mTimerHandler.postDelayed( mTimerRunnable, 100 );
		mEventBus.register( this );
	}

	@Override
	public void onPause() {
		super.onPause();

		mTimerHandler.removeCallbacks( mTimerRunnable );
		mEventBus.unregister( this );
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventServerSelected args ) {
		if( mApplicationState.getIsConnected()) {
			syncTime();
		}
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventTransportUpdate args ) {
		mPlayState = args.getPlayState();
		mCurrentTrack = args.getCurrentTrack();
		mLastPosition = args.getCurrentPosition();
		mTrackLength = args.getTrackLength();
		mLastReceived = args.getTimeReceived();

		updateDisplay();
	}

	private void syncTime() {
		AndroidObservable.fromFragment( this, mNoiseTransport.SyncServerTime())
				.subscribe( new Action1<ServerTimeSync>() {
					@Override
					public void call( ServerTimeSync timeSync ) {
						//mServerTimeOffset = timeSync.getTimeDifference();

						getCurrentState();
					}
				}, new Action1<Throwable>() {
		            @Override
		            public void call( Throwable throwable ) {
			            Log.e( TAG, "The ServerTimeSync call failed: " + throwable );
		            }
	            } );
	}

	private void getCurrentState() {
		AndroidObservable.fromFragment( this, mNoiseTransport.GetTransportState())
				.subscribe( new Action1<TransportState>() {
					            @Override
					            public void call( TransportState state ) {
						            mPlayState = state.getPlayState();
						            mCurrentTrack = state.getCurrentTrack();
						            mLastPosition = state.getCurrentTrackPosition();
						            mTrackLength = state.getCurrentTrackLength();
						            mLastReceived = state.getReceivedTime();

						            updateDisplay();
					            }
				            }, new Action1<Throwable>() {
					            @Override
					            public void call( Throwable throwable ) {
						            Log.e( TAG, "The GetTransportState call failed: " + throwable );
					            }
				            }
				);
	}

	private void updateDisplay() {
		switch( mPlayState ) {
			case 1: // Stopped
				mPlaybackPosition.setText( "Stopped" );
				break;

			case 2: // Playing
				long    timeOffset = System.currentTimeMillis() - mLastReceived;
				long    currentPosition = mLastPosition + timeOffset - mServerTimeOffset;

				mPlaybackPosition.setText( String.format( "Playing: %s", formatPlayingTime( currentPosition )));
				break;

			case 3: // Paused
				mPlaybackPosition.setText( String.format( "Paused: %s", formatPlayingTime( mLastPosition )));
				break;
		}
	}

	private String formatPlayingTime( long currentPosition ) {
		return(	String.format( "%d:%02d", TimeUnit.MILLISECONDS.toMinutes( currentPosition ),
										  TimeUnit.MILLISECONDS.toSeconds( currentPosition ) -
											TimeUnit.MINUTES.toSeconds( TimeUnit.MILLISECONDS.toMinutes( currentPosition ))));
	}
}
