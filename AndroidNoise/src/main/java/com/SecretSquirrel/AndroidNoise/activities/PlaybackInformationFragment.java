package com.SecretSquirrel.AndroidNoise.activities;

// Created by BSwanson on 2/27/14.

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.ArtistInfo;
import com.SecretSquirrel.AndroidNoise.dto.PlayQueueTrack;
import com.SecretSquirrel.AndroidNoise.dto.ServerTimeSync;
import com.SecretSquirrel.AndroidNoise.dto.TransportState;
import com.SecretSquirrel.AndroidNoise.events.EventArtistRequest;
import com.SecretSquirrel.AndroidNoise.events.EventQueueUpdated;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.events.EventTransportUpdate;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseTransport;
import com.SecretSquirrel.AndroidNoise.interfaces.IQueueStatus;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;
import com.SecretSquirrel.AndroidNoise.views.SlidingPanelLayout;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import rx.android.observables.AndroidObservable;
import rx.util.functions.Action1;

public class PlaybackInformationFragment extends Fragment
										 implements ServiceResultReceiver.Receiver {
	private static final String TAG = PlaybackInformationFragment.class.getName();

	private long                mServerTimeOffset;
	private long                mLastPosition;
	private long                mLastReceived;
	private long                mTrackLength;
	private int                 mPlayState;
	private Handler             mTimerHandler;
	private PlayQueueTrack      mCurrentlyPlaying;
	private ArtistInfo          mArtistInfo;
	private Bitmap              mUnknownArtist;

	private Runnable            mTimerRunnable = new Runnable() {
		@Override
		public void run() {
			updateDisplay();

			mTimerHandler.postDelayed( this, 300 );
		}
	};

	@Inject	EventBus                mEventBus;
	@Inject	IApplicationState       mApplicationState;
	@Inject	IQueueStatus            mQueueStatus;
	@Inject	INoiseData              mNoiseData;
	@Inject	INoiseTransport         mNoiseTransport;
	@Inject	ServiceResultReceiver   mServiceResultReceiver;

	@InjectView( R.id.pi_track_position )	TextView    mPlaybackPosition;
	@InjectView( R.id.pi_status )	        TextView    mStatusView;
	@InjectView( R.id.pi_artist_image ) 	ImageView   mArtistImage;
	@InjectView( R.id.pi_artist_name )      TextView    mArtistName;
	@InjectView( R.id.pi_album_name )       TextView    mAlbumName;

	public static PlaybackInformationFragment newInstance() {
		return( new PlaybackInformationFragment());
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		IocUtility.inject( this );

		mTimerHandler = new Handler();
		mUnknownArtist = BitmapFactory.decodeResource( getResources(), R.drawable.unknown_artist );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_playback_information, container, false );

		if( myView != null ) {
			ButterKnife.inject( this, myView );

			ViewParent    parent = container.getParent();
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

		mServiceResultReceiver.setReceiver( this );

		if( mApplicationState.getIsConnected()) {
			syncTime();
		}

		mTimerHandler.postDelayed( mTimerRunnable, 100 );
		mEventBus.register( this );

		updateStateInfo();
	}

	@Override
	public void onPause() {
		super.onPause();

		mServiceResultReceiver.clearReceiver();
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
		updateStateInfo();
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventTransportUpdate args ) {
		mPlayState = args.getPlayState();
		mLastPosition = args.getCurrentPosition();
		mTrackLength = args.getTrackLength();
		mLastReceived = args.getTimeReceived();

		updateDisplay();
	}

	@SuppressWarnings( "unused" )
	@OnClick( R.id.pi_artist_image )
	public void onClickArtistImage() {
		if( mCurrentlyPlaying != null ) {
			mEventBus.post( new EventArtistRequest( mCurrentlyPlaying.getArtistId()));
		}
	}

	private void syncTime() {
		AndroidObservable.fromFragment( this, mNoiseTransport.SyncServerTime())
				.subscribe( new Action1<ServerTimeSync>() {
					@Override
					public void call( ServerTimeSync timeSync ) {
						//mServerTimeOffset = timeSync.getTimeDifference();

						getCurrentTransportState();
					}
				}, new Action1<Throwable>() {
		            @Override
		            public void call( Throwable throwable ) {
			            Log.e( TAG, "The ServerTimeSync call failed: " + throwable );
		            }
	            } );
	}

	private void getCurrentTransportState() {
		AndroidObservable.fromFragment( this, mNoiseTransport.GetTransportState())
				.subscribe( new Action1<TransportState>() {
					            @Override
					            public void call( TransportState state ) {
						            mPlayState = state.getPlayState();
						            mLastPosition = state.getCurrentTrackPosition();
						            mTrackLength = state.getCurrentTrackLength();
						            mLastReceived = state.getReceivedTime();

						            updateStateInfo();
					            }
				            }, new Action1<Throwable>() {
					            @Override
					            public void call( Throwable throwable ) {
						            Log.e( TAG, "The GetTransportState call failed: " + throwable );
					            }
				            }
				);
	}

	private void retrieveArtistInfo() {
		if( mCurrentlyPlaying != null ) {
			mNoiseData.GetArtistInfo( mCurrentlyPlaying.getArtistId(), mServiceResultReceiver );
		}
	}

	@Override
	public void onReceiveResult( int resultCode, Bundle resultData ) {
		if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
			mArtistInfo = resultData.getParcelable( NoiseRemoteApi.ArtistInfo );
		}

		updateDisplay();
	}

	private void updateStateInfo() {
		PlayQueueTrack  currentTrack = mQueueStatus.getCurrentlyPlayingTrack();

		if( currentTrack != null ) {
			if(( mCurrentlyPlaying != null ) &&
			   ( mCurrentlyPlaying.getId() != currentTrack.getId())) {
				mArtistInfo = null;
			}
		}

		mCurrentlyPlaying = currentTrack;

		if( mArtistInfo == null ) {
			retrieveArtistInfo();
		}

		updateDisplay();
	}

	private void updateDisplay() {
		switch( mPlayState ) {
			case 1: // Stopped
				clearDisplay();
				break;

			case 2: // Playing
				long    timeOffset = System.currentTimeMillis() - mLastReceived;
				long    currentPosition = mLastPosition + timeOffset - mServerTimeOffset;

				mPlaybackPosition.setText( formatPlayingTime( currentPosition ));
				displayTrackStatus( "Now Playing: %s" );
				break;

			case 3: // Paused
				mPlaybackPosition.setText( formatPlayingTime( mLastPosition ));
				displayTrackStatus( "Paused: %s" );
				break;
		}
	}

	private String formatPlayingTime( long currentPosition ) {
		return(	String.format( "%d:%02d", TimeUnit.MILLISECONDS.toMinutes( currentPosition ),
										  TimeUnit.MILLISECONDS.toSeconds( currentPosition ) -
											TimeUnit.MINUTES.toSeconds( TimeUnit.MILLISECONDS.toMinutes( currentPosition ))));
	}

	private void displayTrackStatus( String header ) {
		if( mCurrentlyPlaying != null ) {
			mStatusView.setText( String.format( header, mCurrentlyPlaying.getTrackName() ));
			mArtistName.setText( mCurrentlyPlaying.getArtistName());
			mAlbumName.setText( mCurrentlyPlaying.getAlbumName());

			if( mArtistInfo != null ) {
				Bitmap  artistImage = mArtistInfo.getArtistImage();

				if( artistImage == null ) {
					artistImage = mUnknownArtist;
				}

				mArtistImage.setImageBitmap( artistImage );
			}
			else {
				mArtistImage.setImageBitmap( null );
			}
		}
		else {
			mStatusView.setText( "Play Something!" );
			mArtistName.setText( "" );
			mAlbumName.setText( "" );
		}
	}

	private void clearDisplay() {
		mStatusView.setText( "Play Something!" );
		mPlaybackPosition.setText( "" );
		mArtistName.setText( "" );
		mAlbumName.setText( "" );
		mArtistImage.setImageBitmap( null );
	}
}
