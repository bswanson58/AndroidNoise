package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/30/13.

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.events.EventQueueTimeUpdate;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseQueue;
import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;

public class TransportFragment extends Fragment {
	private static final String     TAG = TransportFragment.class.getName();

	private String      mTotalTimeFormat;
	private String      mRemainingTimeFormat;

	@Inject EventBus    mEventBus;
	@Inject INoiseQueue mNoiseQueue;

	@InjectView( R.id.tr_total_time )       TextView    mTotalTimeView;
	@InjectView( R.id.tr_remaining_time )   TextView    mRemainingTimeView;

	public static TransportFragment newInstance() {
		return( new TransportFragment());
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		IocUtility.inject( this );

		mTotalTimeFormat = getResources().getString( R.string.tr_total_time_format );
		mRemainingTimeFormat = getResources().getString( R.string.tr_remaining_time_format );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_transport, container, false );

		if( myView != null ) {
			ButterKnife.inject( this, myView );
		}

		return( myView );
	}

	@SuppressWarnings( "unused" )
	@OnClick( R.id.play_button )
	public void onClickPlay() {
		ExecuteCommand( INoiseQueue.TransportCommand.Play );
	}

	@SuppressWarnings( "unused" )
	@OnClick( R.id.pause_button )
	public void onClickPause() {
		ExecuteCommand( INoiseQueue.TransportCommand.Pause );
	}

	@SuppressWarnings( "unused" )
	@OnClick( R.id.stop_button )
	public void onClickStop() {
		ExecuteCommand( INoiseQueue.TransportCommand.Stop );
	}

	@SuppressWarnings( "unused" )
	@OnClick( R.id.next_track_button )
	public void onClickNextTrack() {
		ExecuteCommand( INoiseQueue.TransportCommand.PlayNext );
	}

	@SuppressWarnings( "unused" )
	@OnClick( R.id.previous_track_button )
	public void onClickPreviousTrack() {
		ExecuteCommand( INoiseQueue.TransportCommand.PlayPrevious );
	}

	@SuppressWarnings( "unused" )
	@OnClick( R.id.repeat_track_button )
	public void onClickRepeat() {
		ExecuteCommand( INoiseQueue.TransportCommand.Repeat );
	}

	@Override
	public void onResume() {
		super.onResume();

		mEventBus.register( this );
	}

	@Override
	public void onPause() {
		super.onPause();

		mEventBus.unregister( this );
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		ButterKnife.reset( this );
	}

	@SuppressWarnings("unused")
	public void onEvent( EventQueueTimeUpdate args ) {
		if(( mTotalTimeView != null ) &&
		   ( mRemainingTimeView != null )) {
			mTotalTimeView.setText(
					String.format( mTotalTimeFormat,
							TimeUnit.MILLISECONDS.toHours( args.getTotalTime()),
							TimeUnit.MILLISECONDS.toMinutes( args.getTotalTime()) -
									TimeUnit.HOURS.toMinutes( TimeUnit.MILLISECONDS.toHours( args.getTotalTime())),
							TimeUnit.MILLISECONDS.toSeconds( args.getTotalTime()) -
									TimeUnit.MINUTES.toSeconds( TimeUnit.MILLISECONDS.toMinutes( args.getTotalTime()))));

			mRemainingTimeView.setText(
					String.format( mRemainingTimeFormat,
							TimeUnit.MILLISECONDS.toHours( args.getRemainingTime()),
							TimeUnit.MILLISECONDS.toMinutes( args.getRemainingTime()) -
									TimeUnit.HOURS.toMinutes( TimeUnit.MILLISECONDS.toHours( args.getRemainingTime())),
							TimeUnit.MILLISECONDS.toSeconds( args.getRemainingTime()) -
									TimeUnit.MINUTES.toSeconds( TimeUnit.MILLISECONDS.toMinutes( args.getRemainingTime()))));
		}
	}

	private void ExecuteCommand( INoiseQueue.TransportCommand command ) {
		AndroidObservable.fromFragment( this, mNoiseQueue.ExecuteTransportCommand( command ))
				.subscribe( new Action1<BaseServerResult>() {
			@Override
			public void call( BaseServerResult serverResult ) {
					if(!serverResult.Success ) {
						Log.e( TAG, "The transport command was not executed: " + serverResult.ErrorMessage );
					}
			}
		} );
	}
}
