package com.SecretSquirrel.AndroidNoise.activities;

// Created by BSwanson on 2/27/14.

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.AlbumInfo;
import com.SecretSquirrel.AndroidNoise.dto.ArtistInfo;
import com.SecretSquirrel.AndroidNoise.dto.PlayQueueTrack;
import com.SecretSquirrel.AndroidNoise.events.EventAlbumRequest;
import com.SecretSquirrel.AndroidNoise.events.EventArtistRequest;
import com.SecretSquirrel.AndroidNoise.events.EventQueueUpdated;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.events.EventTransportUpdate;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;
import com.SecretSquirrel.AndroidNoise.interfaces.IQueueStatus;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class PlaybackInformationFragment extends Fragment
										 implements ServiceResultReceiver.Receiver {
	private PlayQueueTrack      mCurrentlyPlaying;
	private ArtistInfo          mArtistInfo;
	private AlbumInfo           mAlbumInfo;
	private Bitmap              mUnknownArtist;

	@Inject	EventBus                mEventBus;
	@Inject	IQueueStatus            mQueueStatus;
	@Inject	INoiseData              mNoiseData;
	@Inject	ServiceResultReceiver   mServiceResultReceiver;

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

		mUnknownArtist = BitmapFactory.decodeResource( getResources(), R.drawable.unknown_artist );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_playback_information, container, false );

		if( myView != null ) {
			ButterKnife.inject( this, myView );

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

		mEventBus.register( this );

		displayTrackInformation();
	}

	@Override
	public void onPause() {
		super.onPause();

		mServiceResultReceiver.clearReceiver();
		mEventBus.unregister( this );
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventServerSelected args ) {
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventQueueUpdated args ) {
		updateStatusInfo();
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventTransportUpdate args ) {
		updateStatusInfo();
	}

	@SuppressWarnings( "unused" )
	@OnClick( R.id.pi_artist_name )
	public void onClickArtistName() {
		if( mCurrentlyPlaying != null ) {
			mEventBus.post( new EventArtistRequest( mCurrentlyPlaying.getArtistId()));
		}
	}

	@SuppressWarnings( "unused" )
	@OnClick( R.id.pi_album_name )
	public void onClickAlbumName() {
		if( mCurrentlyPlaying != null ) {
			mEventBus.post( new EventAlbumRequest( mCurrentlyPlaying.getArtistId(), mCurrentlyPlaying.getAlbumId()));
		}
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
		mArtistName.setText( "" );
		mAlbumName.setText( "" );
		mArtistImage.setImageBitmap( null );
		mAlbumImage.setImageBitmap( null );
		mImageAnimator.stopFlipping();
	}
}
