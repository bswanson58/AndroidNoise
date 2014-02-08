package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 2/7/14.

import com.SecretSquirrel.AndroidNoise.events.EventActivityPausing;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseQueue;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseSearch;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseServer;
import com.SecretSquirrel.AndroidNoise.interfaces.IRecentData;
import com.SecretSquirrel.AndroidNoise.model.ApplicationModule;
import com.SecretSquirrel.AndroidNoise.model.QueueRequestHandler;
import com.SecretSquirrel.AndroidNoise.services.noiseApi.NoiseApiModule;

import javax.inject.Inject;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

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
	private IRecentData mRecentData;
	private EventBus    mEventBus;

	@SuppressWarnings("unused")
	public void onEvent( EventServerSelected args ) {
		saveRecentData();
	}

	@SuppressWarnings("unused")
	public void onEvent( EventActivityPausing args ) {
		saveRecentData();
	}

	private void saveRecentData() {
		if( mRecentData != null ) {
			mRecentData.persistData();
			mRecentData.stop();
		}

		mRecentData = null;
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
	public IRecentData provideRecentData( Lazy<RecentDataManager> provider, EventBus eventBus ) {
		if( mEventBus == null ) {
			mEventBus = eventBus;
			mEventBus.register( this );
		}

		if( mRecentData == null ) {
			mRecentData = provider.get();

			mRecentData.start();
		}

		return( mRecentData );
	}
}
