package com.SecretSquirrel.AndroidNoise.model;

// Secret Squirrel Software - Created by bswanson on 12/9/13.

import android.app.Application;

import com.SecretSquirrel.AndroidNoise.activities.ActivitiesModule;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.services.ServicesModule;
import com.SecretSquirrel.AndroidNoise.services.noiseApi.NoiseApiModule;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

public class NoiseRemoteApplication extends Application
									implements IocUtility.ObjectGraphApplication {
	private ObjectGraph         mObjectGraph;
	private ApplicationModule   mApplicationModule;
	private IApplicationState   mApplicationState;

	@Override
	public void onCreate() {
		super.onCreate();

		mApplicationState = new ApplicationState( this );
		mApplicationModule = new ApplicationModule( this, mApplicationState );

		mObjectGraph = ObjectGraph.create( getModules().toArray());

	}

	protected List<Object> getModules() {
		return Arrays.<Object>asList(
				mApplicationModule,
				new ServicesModule(),
				new NoiseApiModule(),
				new ActivitiesModule()
		);
	}

	public IApplicationState getApplicationState() {
		return( mApplicationState );
	}

	@Override
	public void inject( Object dependent ) {
		mObjectGraph.inject( dependent );
	}
}
