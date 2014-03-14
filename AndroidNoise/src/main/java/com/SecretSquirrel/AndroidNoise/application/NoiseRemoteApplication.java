package com.SecretSquirrel.AndroidNoise.application;

// Secret Squirrel Software - Created by BSwanson on 12/9/13.

import android.app.Application;

import com.SecretSquirrel.AndroidNoise.BuildConfig;
import com.SecretSquirrel.AndroidNoise.activities.ActivitiesModule;
import com.SecretSquirrel.AndroidNoise.services.ServicesModule;
import com.SecretSquirrel.AndroidNoise.services.noiseApi.NoiseApiModule;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;
import timber.log.Timber;

public class NoiseRemoteApplication extends Application
									implements IocUtility.ObjectGraphApplication {
	private ObjectGraph     mObjectGraph;

	@Override
	public void onCreate() {
		super.onCreate();

		if( BuildConfig.DEBUG ) {
			Timber.plant( new Timber.DebugTree());
		}
		else {
			Timber.plant( new Timber.HollowTree());
		}

		mObjectGraph = ObjectGraph.create( getModules().toArray());
	}

	protected List<Object> getModules() {
		return Arrays.asList(
				new ApplicationModule( this ),
				ServicesModule.class,
				NoiseApiModule.class,
				ActivitiesModule.class
			);
	}

	@Override
	public void inject( Object dependent ) {
		mObjectGraph.inject( dependent );
	}
}
