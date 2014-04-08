package com.SecretSquirrel.AndroidNoise.application;

// Secret Squirrel Software - Created by BSwanson on 12/9/13.

import android.app.Application;

import com.SecretSquirrel.AndroidNoise.BuildConfig;
import com.SecretSquirrel.AndroidNoise.activities.ActivitiesModule;
import com.SecretSquirrel.AndroidNoise.models.ModelsModule;
import com.SecretSquirrel.AndroidNoise.services.ServicesModule;
import com.SecretSquirrel.AndroidNoise.services.noiseApi.NoiseApiModule;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;
import com.crashlytics.android.Crashlytics;

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

		createLogger();

		mObjectGraph = ObjectGraph.create( getModules().toArray());
	}

	protected List<Object> getModules() {
		return Arrays.asList(
				new ApplicationModule( this ),
				ServicesModule.class,
				NoiseApiModule.class,
				ModelsModule.class,
				ActivitiesModule.class
			);
	}

	private void createLogger() {
		if( BuildConfig.DEBUG ) {
			Timber.plant( new Timber.DebugTree() {
				@Override
				public void e( Throwable t, String message, Object... args ) {
					Crashlytics.logException( t );

					super.e( t, message, args );
				}
			});
		}
		else {
			Timber.plant( new Timber.HollowTree() {
				@Override
				public void e( Throwable t, String message, Object... args ) {
					Crashlytics.logException( t );

					super.e( t, message, args );
				}
			});
		}
	}

	@Override
	public void inject( Object dependent ) {
		mObjectGraph.inject( dependent );
	}
}
