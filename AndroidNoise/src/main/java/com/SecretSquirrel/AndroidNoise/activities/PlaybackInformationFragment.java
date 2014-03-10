package com.SecretSquirrel.AndroidNoise.activities;

// Created by BSwanson on 2/27/14.

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.AlbumInfo;
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
import com.SecretSquirrel.AndroidNoise.support.NoiseUtils;
import com.SecretSquirrel.AndroidNoise.views.SlidingPanelLayout;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import rx.android.observables.AndroidObservable;
import rx.util.functions.Action1;
import timber.log.Timber;

public class PlaybackInformationFragment extends Fragment
										 implements ServiceResultReceiver.Receiver {
	private long                mServerTimeOffset;
	private long                mLastPosition;
	private long                mLastReceived;
	private long                mTrackLength;
	private int                 mPlayState;
	private Handler             mTimerHandler;
	private PlayQueueTrack      mCurrentlyPlaying;
	private ArtistInfo          mArtistInfo;
	private AlbumInfo           mAlbumInfo;
	private Bitmap              mUnknownArtist;
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

	@Inject	EventBus                mEventBus;
	@Inject	IApplicationState       mApplicationState;
	@Inject	IQueueStatus            mQueueStatus;
	@Inject	INoiseData              mNoiseData;
	@Inject	INoiseTransport         mNoiseTransport;
	@Inject	ServiceResultReceiver   mServiceResultReceiver;

	@InjectView( R.id.pi_track_position )	TextView        mPlaybackPosition;
	@InjectView( R.id.pi_progress )	        ProgressBar     mPlaybackProgress;
	@InjectView( R.id.pi_status )	        TextView        mStatusView;
	@InjectView( R.id.pi_artist_image ) 	ImageView       mArtistImage;
	@InjectView( R.id.pi_album_image )      ImageView       mAlbumImage;
	@InjectView( R.id.pi_artist_name )      TextView        mArtistName;
	@InjectView( R.id.pi_album_name )       TextView        mAlbumName;
	@InjectView( R.id.pi_image_flipper )	ViewFlipper     mImageAnimator;

	public static PlaybackInformationFragment newInstance() {
		return( new PlaybackInformationFragment());
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		IocUtility.inject( this );

		mTimerHandler = new Handler();
		mUnknownArtist = BitmapFactory.decodeResource( getResources(), R.drawable.unknown_artist );

		mPlaybackPlayingFormat = getString( R.string.playback_now_playing );
		mPlaybackPausedFormat = getString( R.string.playback_paused );
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

			mImageAnimator.setInAnimation( AnimationUtils.loadAnimation( getActivity(), android.R.anim.fade_in ));
			mImageAnimator.setOutAnimation( AnimationUtils.loadAnimation( getActivity(), android.R.anim.fade_out ));
		}

		return( myView );
	}

	@Override
	public void onResume() {
		super.onResume();

		clearDisplay();
		mServiceResultReceiver.setReceiver( this );

		if( mApplicationState.getIsConnected()) {
			syncTime();
		}

		mTimerHandler.postDelayed( mTimerRunnable, 100 );
		mEventBus.register( this );

		displayStatus();
		displayTrackInformation();

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( getActivity());

		mDisplayRemaining = settings.getBoolean( getString( R.string.setting_playback_countdown ), false );
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
		AndroidObservable.fromFragment( this, mNoiseTransport.GetTransportState())
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
						            Timber.e( "The GetTransportState call failed: " + throwable );
					            }
				            }
				);
	}

	private void retrieveArtistInfo() {
		if( mCurrentlyPlaying != null ) {
			mNoiseData.GetArtistInfo( mCurrentlyPlaying.getArtistId(), mServiceResultReceiver );
		}
	}

	private void retrieveAlbumInfo() {
		if( mCurrentlyPlaying != null ) {
			mNoiseData.GetAlbumInfo( mCurrentlyPlaying.getAlbumId(), mServiceResultReceiver );
		}
	}

	@Override
	public void onReceiveResult( int resultCode, Bundle resultData ) {
		if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
			int callCode = resultData.getInt( NoiseRemoteApi.RemoteApiParameter);

			switch( callCode ) {
				case NoiseRemoteApi.GetArtistInfo:
					mArtistInfo = resultData.getParcelable( NoiseRemoteApi.ArtistInfo );
					break;

				case NoiseRemoteApi.GetAlbumInfo:
					mAlbumInfo = resultData.getParcelable( NoiseRemoteApi.AlbumInfo );
					break;
			}
		}

		displayTrackInformation();
	}

	private void updateStatusInfo() {
		PlayQueueTrack  currentTrack = mQueueStatus.getCurrentlyPlayingTrack();

		if( currentTrack != null ) {
			if(( mCurrentlyPlaying != null ) &&
			   ( mCurrentlyPlaying.getId() != currentTrack.getId())) {
				mArtistInfo = null;
				mAlbumInfo = null;

				mImageAnimator.stopFlipping();
				mImageAnimator.setDisplayedChild( 0 );
			}
		}

		mCurrentlyPlaying = currentTrack;

		if( mArtistInfo == null ) {
			retrieveArtistInfo();
		}
		if( mAlbumInfo == null ) {
			retrieveAlbumInfo();
		}

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
				}
				else {
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
	private void displayTrackInformation() {
		if( mCurrentlyPlaying != null ) {
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

			if( mAlbumInfo != null ) {
				Bitmap  albumImage = mAlbumInfo.getAlbumCover();

				if( albumImage != null ) {
					mAlbumImage.setImageBitmap( albumImage );

					mImageAnimator.setFlipInterval( 7000 );
					mImageAnimator.startFlipping();
				}
				else {
					mAlbumImage.setImageBitmap( null );
				}
			}
		}
		else {
			clearDisplay();
		}
	}

	private void clearDisplay() {
		mStatusView.setText( "Play Something!" );
		mPlaybackPosition.setText( "" );
		mArtistName.setText( "" );
		mAlbumName.setText( "" );
		mArtistImage.setImageBitmap( null );
		mAlbumImage.setImageBitmap( null );
		mImageAnimator.stopFlipping();
		mPlaybackProgress.setVisibility( View.INVISIBLE );
	}
}
