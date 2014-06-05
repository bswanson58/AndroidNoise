package com.SecretSquirrel.AndroidNoise.services.noiseApi;

// Secret Squirrel Software - Created by bswanson on 2/7/14.

import com.SecretSquirrel.AndroidNoise.dto.ServerInformation;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.application.ApplicationModule;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module(
		includes = ApplicationModule.class,
		library = true
)
public class NoiseApiModule {
	@Provides
	public RemoteServerRestApi provideServerApi( IApplicationState applicationState ) {
		RemoteServerRestApi     retValue = null;

		if( applicationState.getIsConnected()) {
			retValue = createAdapter( applicationState.getCurrentServer()).create( RemoteServerRestApi.class );
		}

		return( retValue );
	}

	@Provides
	public RemoteServerDataApi providesDataApi( IApplicationState applicationState ) {
		RemoteServerDataApi     retValue = null;

		if( applicationState.getIsConnected()) {
			retValue = createAdapter( applicationState.getCurrentServer()).create( RemoteServerDataApi.class );
		}

		return( retValue );
	}

	@Provides
	public RemoteServerQueueApi providesQueueApi( IApplicationState applicationState ) {
		RemoteServerQueueApi    retValue = null;

		if( applicationState.getIsConnected()) {
			retValue = createAdapter( applicationState.getCurrentServer()).create( RemoteServerQueueApi.class );
		}

		return( retValue );
	}

	@Provides
	public RemoteServerSearchApi providesSearchApi( IApplicationState applicationState ) {
		RemoteServerSearchApi    retValue = null;

		if( applicationState.getIsConnected()) {
			retValue = createAdapter( applicationState.getCurrentServer()).create( RemoteServerSearchApi.class );
		}

		return( retValue );
	}

	@Provides
	public RemoteServerTransportApi provideTransportApi( IApplicationState applicationState ) {
		RemoteServerTransportApi    retValue = null;

		if( applicationState.getIsConnected()) {
			retValue = createAdapter( applicationState.getCurrentServer()).create( RemoteServerTransportApi.class );
		}

		return( retValue );
	}

	@Provides
	public RemoteServerLibraryApi provideLibraryApi( IApplicationState applicationState ) {
		RemoteServerLibraryApi  retValue = null;

		if( applicationState.getIsConnected()) {
			retValue = createAdapter( applicationState.getCurrentServer()).create( RemoteServerLibraryApi.class );
		}

		return( retValue );
	}

	private RestAdapter createAdapter( ServerInformation serverInformation ) {
		return( new RestAdapter.Builder()
				.setEndpoint( serverInformation.getServerAddress())
				.build());
	}
}
