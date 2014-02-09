package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 2/7/14.

import com.SecretSquirrel.AndroidNoise.events.EventActivityPausing;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationServices;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseQueue;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseSearch;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseServer;
import com.SecretSquirrel.AndroidNoise.interfaces.IRecentData;
import com.SecretSquirrel.AndroidNoise.services.noiseApi.NoiseApiModule;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

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
public class ServicesModule {
	private EventBus            mEventBus;
	private IRecentData         mRecentData;
	private INoiseData          mNoiseData;

	@SuppressWarnings("unused")
	public void onEvent( EventServerSelected args ) {
		saveRecentData();

		mNoiseData = null;
	}

	@SuppressWarnings("unused")
	public void onEvent( EventActivityPausing args ) {
		saveRecentData();

		mNoiseData = null;
	}

	private void saveRecentData() {
		if( mRecentData != null ) {
			mRecentData.persistData();
			mRecentData.stop();
		}

		mRecentData = null;
	}

	@Provides
	@Singleton
	public IApplicationServices provideApplicationServices( Lazy<ApplicationServices> provider ) {
		return( provider.get());
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

	@Provides
	@Named( "NoiseDataClient" )
	public INoiseData provideNoiseData( NoiseDataClient client ) {
		return( client );
	}

	@Provides
	public INoiseData provideNoiseDataCache( Lazy<NoiseDataCacheClient> provider ) {
		if( mNoiseData == null ) {
			mNoiseData = provider.get();
		}

		return( mNoiseData );
	}
}
