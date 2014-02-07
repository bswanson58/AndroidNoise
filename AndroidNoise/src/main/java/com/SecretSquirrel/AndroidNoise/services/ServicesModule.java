package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 2/7/14.

import android.content.Context;

import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseQueue;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseSearch;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseServer;
import com.SecretSquirrel.AndroidNoise.model.ApplicationModule;
import com.SecretSquirrel.AndroidNoise.model.QueueRequestHandler;
import com.SecretSquirrel.AndroidNoise.services.noiseApi.NoiseApiModule;
import com.SecretSquirrel.AndroidNoise.services.noiseApi.RemoteServerDataApi;
import com.SecretSquirrel.AndroidNoise.services.noiseApi.RemoteServerQueueApi;
import com.SecretSquirrel.AndroidNoise.services.noiseApi.RemoteServerRestApi;
import com.SecretSquirrel.AndroidNoise.services.noiseApi.RemoteServerSearchApi;

import dagger.Module;
import dagger.Provides;

@Module(
		includes = {
				NoiseApiModule.class,
				ApplicationModule.class
		},
		library = true,
		injects = {
				QueueRequestHandler.class,
				EventHostService.class
		}
)
public class ServicesModule {
	@Provides
	public INoiseServer provideNoiseServer( RemoteServerRestApi serverApi, IApplicationState applicationState, Context context ) {
		return( new NoiseRemoteClient( serverApi, applicationState, context ));
	}

	@Provides
	public INoiseQueue providesNoiseQueue( RemoteServerQueueApi queueApi ) {
		return( new NoiseQueueClient( queueApi ));
	}

	@Provides
	public INoiseSearch providesNoiseSearch( RemoteServerSearchApi searchApi ) {
		return( new NoiseSearchClient( searchApi ));
	}
}
