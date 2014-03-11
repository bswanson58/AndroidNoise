package com.SecretSquirrel.AndroidNoise.activities;// Created by BSwanson on 3/10/14.

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.AudioState;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseTransport;
import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import rx.android.observables.AndroidObservable;
import rx.util.functions.Action1;
import timber.log.Timber;

public class PlaybackAudioFragment extends Fragment {
	public static PlaybackAudioFragment newInstance() {
		return( new PlaybackAudioFragment());
	}

	private AudioState          mAudioState;

	@Inject	EventBus            mEventBus;
	@Inject	INoiseTransport     mNoiseTransport;
	@Inject	IApplicationState   mApplicationState;

	@InjectView( R.id.pac_volume )	SeekBar mVolume;

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		IocUtility.inject( this );

		mAudioState = new AudioState();
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_playback_audio_config, container, false );

		if( myView != null ) {
			ButterKnife.inject( this, myView );

			mVolume.setMax( 100 );
			mVolume.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged( SeekBar seekBar, int i, boolean fromUser ) {
					if( fromUser ) {
						mAudioState.setVolumeLevel( i );
					}

					updateAudioState();
				}

				@Override
				public void onStartTrackingTouch( SeekBar seekBar ) { }
				@Override
				public void onStopTrackingTouch( SeekBar seekBar ) { }
			} );
		}

		return( myView );
	}

	@Override
	public void onResume() {
		super.onResume();

		retrieveAudioState();

		mEventBus.register( this );
	}

	@Override
	public void onPause() {
		super.onPause();

		mEventBus.unregister( this );
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventServerSelected args ) {
		retrieveAudioState();
	}

	private void retrieveAudioState() {
		if( mApplicationState.getIsConnected()) {
			AndroidObservable.fromFragment( this, mNoiseTransport.getAudioState())
					.subscribe( new Action1<AudioState>() {
						            @Override
						            public void call( AudioState state ) {
							            mAudioState = state;

							            syncDisplayWithState();
						            }
					            }, new Action1<Throwable>() {
						            @Override
						            public void call( Throwable throwable ) {
							            Timber.e( "The getAudioState call failed: " + throwable );
						            }
					            } );
		}
	}

	private void updateAudioState() {
		if( mApplicationState.getIsConnected()) {
			AndroidObservable.fromFragment( this, mNoiseTransport.setAudioState( mAudioState ))
					.subscribe( new Action1<BaseServerResult>() {
						            @Override
						            public void call( BaseServerResult result ) {
							            if(!result.Success ) {
								            Timber.e( "The setAudioState call return an error: %s", result.ErrorMessage );
							            }
						            }
					            }, new Action1<Throwable>() {
						            @Override
						            public void call( Throwable throwable ) {
							            Timber.e( "The setAudioState call failed: " + throwable );
						            }
					            } );
		}
	}

	private void syncDisplayWithState() {
		mVolume.setProgress( mAudioState.getVolumeLevel());
	}
}
