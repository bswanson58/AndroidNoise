package com.SecretSquirrel.AndroidNoise.model;

// Secret Squirrel Software - Created by bswanson on 12/9/13.

import android.app.Application;

import com.SecretSquirrel.AndroidNoise.activities.ActivitiesModule;
import com.SecretSquirrel.AndroidNoise.services.ServicesModule;
import com.SecretSquirrel.AndroidNoise.services.noiseApi.NoiseApiModule;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

public class NoiseRemoteApplication extends Application
									implements IocUtility.ObjectGraphApplication {
	private ObjectGraph     mObjectGraph;

	@Override
	public void onCreate() {
		super.onCreate();

		mObjectGraph = ObjectGraph.create( getModules().toArray());
	}

	protected List<Object> getModules() {
		return Arrays.asList(
				new ApplicationModule( this ),
				new ServicesModule(),
				new NoiseApiModule(),
				new ActivitiesModule()
			);
	}

	@Override
	public void inject( Object dependent ) {
		mObjectGraph.inject( dependent );
	}
}
