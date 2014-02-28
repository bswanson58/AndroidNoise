package com.SecretSquirrel.AndroidNoise.activities;

// Created by BSwanson on 2/27/14.

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SecretSquirrel.AndroidNoise.dto.ServerTimeSync;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseTransport;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import rx.android.observables.AndroidObservable;
import rx.util.functions.Action1;

public class PlaybackInformationFragment extends Fragment {
	private static final String TAG = PlaybackInformationFragment.class.getName();

	private long                mServerTimeOffset;

	@Inject	EventBus            mEventBus;
	@Inject	IApplicationState   mApplicationState;
	@Inject	INoiseTransport     mNoiseTransport;

	public static PlaybackInformationFragment newInstance() {
		return( new PlaybackInformationFragment());
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		IocUtility.inject( this );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		return super.onCreateView( inflater, container, savedInstanceState );
	}

	@Override
	public void onResume() {
		super.onResume();

		if( mApplicationState.getIsConnected()) {
			syncTime();
		}

		mEventBus.register( this );
	}

	@Override
	public void onPause() {
		super.onPause();

		mEventBus.unregister( this );
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventServerSelected args ) {
		if( mApplicationState.getIsConnected()) {
			syncTime();
		}
	}

	private void syncTime() {
		AndroidObservable.fromFragment( this, mNoiseTransport.SyncServerTime())
				.subscribe( new Action1<ServerTimeSync>() {
					@Override
					public void call( ServerTimeSync timeSync ) {
						mServerTimeOffset = timeSync.getTimeDifference();
					}
				}, new Action1<Throwable>() {
		            @Override
		            public void call( Throwable throwable ) {
			            Log.e( TAG, "The ServerTimeSync call failed: " + throwable );
		            }
	            } );
	}
}
