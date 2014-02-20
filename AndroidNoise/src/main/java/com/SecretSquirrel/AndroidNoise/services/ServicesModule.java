package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 2/7/14.

import android.os.Handler;

import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationServices;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseQueue;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseSearch;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseServer;
import com.SecretSquirrel.AndroidNoise.interfaces.IRecentData;
import com.SecretSquirrel.AndroidNoise.services.noiseApi.NoiseApiModule;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
		includes = {
				NoiseApiModule.class
		},
		library = true,
		injects = {
				QueueRequestHandler.class,
				EventHostService.class
		}
)
@SuppressWarnings( "unused" )
public class ServicesModule {
	private ApplicationServices mApplicationServices;
	@Provides
	@Singleton
	public IApplicationServices provideApplicationServices( ApplicationServices applicationServices ) {
		mApplicationServices = applicationServices;

		return( mApplicationServices );
	}

	@Provides
	public ServiceResultReceiver providesServiceResultReceiver() {
		return( new ServiceResultReceiver( new Handler()));
	}

	@Provides
	public INoiseServer provideNoiseServer( NoiseRemoteClient client ) {
		return( client );
	}

	@Provides
	public INoiseQueue providesNoiseQueue( NoiseQueueClient client ) {
		return( client );
	}

	@Provides
	public INoiseSearch providesNoiseSearch( NoiseSearchClient client ) {
		return( client );
	}

	@Provides
	public IRecentData provideRecentData() {
		IRecentData retValue = null;

		if( mApplicationServices != null ) {
			retValue = mApplicationServices.getRecentDataManager();
		}

		return( retValue );
	}

	@Provides
	@Named( "NoiseDataClient" )
	public INoiseData provideNoiseData( NoiseDataClient client ) {
		return( client );
	}

	@Provides
	@Singleton
	public INoiseData provideNoiseDataCache( NoiseDataCacheClient client ) {
		return( client );
	}
}
