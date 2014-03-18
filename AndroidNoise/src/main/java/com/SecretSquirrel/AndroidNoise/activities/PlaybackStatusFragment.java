package com.SecretSquirrel.AndroidNoise.activities;// Created by BSwanson on 3/10/14.

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.PlayQueueTrack;
import com.SecretSquirrel.AndroidNoise.dto.ServerTimeSync;
import com.SecretSquirrel.AndroidNoise.dto.TransportState;
import com.SecretSquirrel.AndroidNoise.events.EventQueueUpdated;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.events.EventTransportUpdate;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseTransport;
import com.SecretSquirrel.AndroidNoise.interfaces.IQueueStatus;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;
import com.SecretSquirrel.AndroidNoise.support.NoiseUtils;
import com.SecretSquirrel.AndroidNoise.views.SlidingPanelLayout;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;
import timber.log.Timber;

public class PlaybackStatusFragment extends Fragment {
		private long                mServerTimeOffset;
		private long                mLastPosition;
		private long                mLastReceived;
		private long                mTrackLength;
		private int                 mPlayState;
		private Handler             mTimerHandler;
		private PlayQueueTrack      mCurrentlyPlaying;
		private boolean             mDisplayRemaining;
		private String              mPlaybackPlayingFormat;
		private String              mPlaybackPausedFormat;

		private Runnable            mTimerRunnable = new Runnable() {
			@Override
			public void run() {
				displayStatus();

				mTimerHandler.postDelayed( this, 300 );
			}
		};

		@Inject EventBus                mEventBus;
		@Inject IApplicationState       mApplicationState;
		@Inject IQueueStatus            mQueueStatus;
		@Inject INoiseData              mNoiseData;
		@Inject INoiseTransport         mNoiseTransport;

		@InjectView( R.id.pi_track_position )   TextView    mPlaybackPosition;
		@InjectView( R.id.pi_progress )         ProgressBar mPlaybackProgress;
		@InjectView( R.id.pi_status )	        TextView    mStatusView;

	public static PlaybackStatusFragment newInstance() {
		return( new PlaybackStatusFragment());
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		IocUtility.inject( this );

		mTimerHandler = new Handler();

		mPlaybackPlayingFormat = getString( R.string.playback_now_playing );
		mPlaybackPausedFormat = getString( R.string.playback_paused );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_playback_status, container, false );

		if( myView != null ) {
			ButterKnife.inject( this, myView );

			ViewParent parent = container.getParent();
			while( parent != null ) {
				if( parent instanceof SlidingPanelLayout ) {
					SlidingPanelLayout  slidingPanel = (SlidingPanelLayout)parent;
					View                dragHandle = myView.findViewById( R.id.pi_drag_handle );

					slidingPanel.setDragView( dragHandle );

					break;
				}
				else {
					parent = parent.getParent();
				}
			}
		}

		return( myView );
	}

	@Override
	public void onResume() {
		super.onResume();

		clearDisplay();

		if( mApplicationState.getIsConnected()) {
			syncTime();
		}

		mTimerHandler.postDelayed( mTimerRunnable, 100 );
		mEventBus.register( this );

		displayStatus();

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( getActivity() );

		mDisplayRemaining = settings.getBoolean( getString( R.string.setting_playback_countdown ), false );
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
	public void onEvent( EventQueueUpdated args ) {
		updateStatusInfo();
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventTransportUpdate args ) {
		mPlayState = args.getPlayState();
		mLastPosition = args.getCurrentPosition();
		mTrackLength = args.getTrackLength();
		mLastReceived = args.getTimeReceived();

		updateStatusInfo();
	}

	private void syncTime() {
		AndroidObservable.fromFragment( this, mNoiseTransport.syncServerTime() )
				.subscribe( new Action1<ServerTimeSync>() {
					            @Override
					            public void call( ServerTimeSync timeSync ) {
						            //mServerTimeOffset = timeSync.getTimeDifference();
						            mServerTimeOffset = 0;

						            getCurrentTransportState();
					            }
				            }, new Action1<Throwable>() {
					            @Override
					            public void call( Throwable throwable ) {
						            Timber.e( "The ServerTimeSync call failed: " + throwable );
					            }
				            } );
	}

	private void getCurrentTransportState() {
		AndroidObservable.fromFragment( this, mNoiseTransport.getTransportState())
				.subscribe( new Action1<TransportState>() {
					            @Override
					            public void call( TransportState state ) {
						            mPlayState = state.getPlayState();
						            mLastPosition = state.getCurrentTrackPosition();
						            mTrackLength = state.getCurrentTrackLength();
						            mLastReceived = state.getReceivedTime();

						            updateStatusInfo();
					            }
				            }, new Action1<Throwable>() {
					            @Override
					            public void call( Throwable throwable ) {
						            Timber.e( "The getTransportState call failed: " + throwable );
					            }
				            }
				);
	}

	private void updateStatusInfo() {
		mCurrentlyPlaying = mQueueStatus.getCurrentlyPlayingTrack();

		displayStatus();
	}

	private void displayStatus() {
		long    currentPosition;

		switch( mPlayState ) {
			case 1: // Stopped
				clearDisplay();
				break;

			case 2: // Playing
				long    currentProgress = mLastPosition + ( System.currentTimeMillis() - mLastReceived ) - mServerTimeOffset;

				if( mDisplayRemaining ) {
					currentPosition = mTrackLength - currentProgress;
					if( currentPosition < 0 ) {
						currentPosition = 0;
					}
				}
				else {
					if( currentProgress > mTrackLength ) {
						currentProgress = mTrackLength;
					}
					currentPosition = currentProgress;
				}
				displayStatus( mPlaybackPlayingFormat, NoiseUtils.formatPlaybackPosition( currentPosition, mDisplayRemaining ), currentProgress );
				break;

			case 3: // Paused
				if( mDisplayRemaining ) {
					currentPosition = mTrackLength - mLastPosition;
				}
				else {
					currentPosition = mLastPosition;
				}
				displayStatus( mPlaybackPausedFormat, NoiseUtils.formatPlaybackPosition( currentPosition, mDisplayRemaining ), mLastPosition );
				break;
		}
	}

	private void displayStatus( String header, String playbackTime, long currentPosition ) {
		if( mCurrentlyPlaying != null ) {
			mStatusView.setText( String.format( header, mCurrentlyPlaying.getTrackName()));

			mPlaybackPosition.setText( playbackTime );
			mPlaybackProgress.setMax((int)mTrackLength );
			mPlaybackProgress.setProgress((int)currentPosition );
			mPlaybackProgress.setVisibility( View.VISIBLE );
		}
		else {
			clearDisplay();
		}
	}

	private void clearDisplay() {
		mStatusView.setText( "Play Something!" );
		mPlaybackPosition.setText( "" );
		mPlaybackProgress.setVisibility( View.INVISIBLE );
	}
}
