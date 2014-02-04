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
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseQueue;
import com.SecretSquirrel.AndroidNoise.model.NoiseRemoteApplication;
import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;

import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import rx.android.observables.AndroidObservable;
import rx.util.functions.Action1;

public class TransportFragment extends Fragment {
	private static final String     TAG = TransportFragment.class.getName();

	private TextView    mTotalTimeView;
	private TextView    mRemainingTimeView;
	private String      mTotalTimeFormat;
	private String      mRemainingTimeFormat;

	public static TransportFragment newInstance() {
		return( new TransportFragment());
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		mTotalTimeFormat = getResources().getString( R.string.tr_total_time_format );
		mRemainingTimeFormat = getResources().getString( R.string.tr_remaining_time_format );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_transport, container, false );

		if( myView != null ) {
			myView.findViewById( R.id.play_button ).setOnClickListener( new View.OnClickListener() {
				@Override
				public void onClick( View view ) {
					ExecuteCommand( INoiseQueue.TransportCommand.Play );
				}
			} );
			myView.findViewById( R.id.pause_button ).setOnClickListener( new View.OnClickListener() {
				@Override
				public void onClick( View view ) {
					ExecuteCommand( INoiseQueue.TransportCommand.Pause );
				}
			} );
			myView.findViewById( R.id.stop_button ).setOnClickListener( new View.OnClickListener() {
				@Override
				public void onClick( View view ) {
					ExecuteCommand( INoiseQueue.TransportCommand.Stop );
				}
			} );
			myView.findViewById( R.id.next_track_button ).setOnClickListener( new View.OnClickListener() {
				@Override
				public void onClick( View view ) {
					ExecuteCommand( INoiseQueue.TransportCommand.PlayNext );
				}
			} );
			myView.findViewById( R.id.previous_track_button ).setOnClickListener( new View.OnClickListener() {
				@Override
				public void onClick( View view ) {
					ExecuteCommand( INoiseQueue.TransportCommand.PlayPrevious );
				}
			} );
			myView.findViewById( R.id.repeat_track_button ).setOnClickListener( new View.OnClickListener() {
				@Override
				public void onClick( View view ) {
					ExecuteCommand( INoiseQueue.TransportCommand.Repeat );
				}
			} );

			mTotalTimeView = (TextView)myView.findViewById( R.id.tr_total_time );
			mRemainingTimeView = (TextView)myView.findViewById( R.id.tr_remaining_time );
		}

		return( myView );
	}

	@Override
	public void onResume() {
		super.onResume();

		EventBus.getDefault().register( this );
	}

	@Override
	public void onPause() {
		super.onPause();

		EventBus.getDefault().unregister( this );
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
		AndroidObservable.fromFragment( this, getApplicationState().getQueueClient().ExecuteTransportCommand( command ))
				.subscribe( new Action1<BaseServerResult>() {
			@Override
			public void call( BaseServerResult serverResult ) {
					if(!serverResult.Success ) {
						Log.e( TAG, "The transport command was not executed: " + serverResult.ErrorMessage );
					}
			}
		} );
	}

	private IApplicationState getApplicationState() {
		NoiseRemoteApplication application = (NoiseRemoteApplication)getActivity().getApplication();

		return( application.getApplicationState());
	}
}
