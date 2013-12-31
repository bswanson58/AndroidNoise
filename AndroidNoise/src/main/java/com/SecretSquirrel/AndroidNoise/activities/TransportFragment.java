package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/30/13.

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseQueue;
import com.SecretSquirrel.AndroidNoise.model.NoiseRemoteApplication;
import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;

import rx.android.observables.AndroidObservable;
import rx.util.functions.Action1;

public class TransportFragment extends Fragment {
	private static final String     TAG = TransportFragment.class.getName();

	public static TransportFragment newInstance() {
		return( new TransportFragment());
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_transport, container, false );

		((Button)myView.findViewById( R.id.play_button )).setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick( View view ) {
				ExecuteCommand( INoiseQueue.TransportCommand.Play );
			}
		} );
		((Button)myView.findViewById( R.id.pause_button )).setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick( View view ) {
				ExecuteCommand( INoiseQueue.TransportCommand.Pause );
			}
		} );
		((Button)myView.findViewById( R.id.stop_button )).setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick( View view ) {
				ExecuteCommand( INoiseQueue.TransportCommand.Stop );
			}
		} );
		((Button)myView.findViewById( R.id.next_track_button )).setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick( View view ) {
				ExecuteCommand( INoiseQueue.TransportCommand.PlayNext );
			}
		} );
		((Button)myView.findViewById( R.id.previous_track_button )).setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick( View view ) {
				ExecuteCommand( INoiseQueue.TransportCommand.PlayPrevious );
			}
		} );
		((Button)myView.findViewById( R.id.repeat_track_button )).setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick( View view ) {
				ExecuteCommand( INoiseQueue.TransportCommand.Repeat );
			}
		} );

		return( myView );
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
