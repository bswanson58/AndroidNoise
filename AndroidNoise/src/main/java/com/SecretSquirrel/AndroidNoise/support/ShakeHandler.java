package com.SecretSquirrel.AndroidNoise.support;

// Created by BSwanson on 2/20/14.

import android.content.Context;
import android.hardware.SensorManager;

import com.SecretSquirrel.AndroidNoise.events.EventActivityPausing;
import com.SecretSquirrel.AndroidNoise.events.EventActivityResuming;
import com.SecretSquirrel.AndroidNoise.events.EventShakeDetected;
import com.squareup.seismic.ShakeDetector;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class ShakeHandler implements ShakeDetector.Listener {
	public interface ShakeResponder {
		void    onShake();
	}

	private final EventBus      mEventBus;
	private final SensorManager mSensorManager;
	private ShakeDetector       mShakeDetector;

	@Inject
	public ShakeHandler( Context context, EventBus eventBus ) {
		mEventBus = eventBus;

		mSensorManager = (SensorManager)context.getSystemService( Context.SENSOR_SERVICE );
		if( mSensorManager != null ) {
			mShakeDetector = new ShakeDetector( this );

			mShakeDetector.start( mSensorManager );

			mEventBus.register( this );
		}
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventActivityResuming args ) {
		mShakeDetector.start( mSensorManager );
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventActivityPausing args ) {
		mShakeDetector.stop();
	}

	@Override
	public void hearShake() {
		mEventBus.post( new EventShakeDetected());
	}
}
