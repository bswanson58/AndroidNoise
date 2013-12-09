package com.SecretSquirrel.AndroidNoise.model;

// Secret Squirrel Software - Created by bswanson on 12/9/13.

import android.app.Application;

public class NoiseRemoteApplication extends Application {
	private ApplicationState    mApplicationState;

	@Override
	public void onCreate() {
		super.onCreate();

		mApplicationState = new ApplicationState( this );
	}

	public ApplicationState getApplicationState() {
		return mApplicationState;
	}
}
