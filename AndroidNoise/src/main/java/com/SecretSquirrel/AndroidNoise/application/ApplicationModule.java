package com.SecretSquirrel.AndroidNoise.application;

// Secret Squirrel Software - Created by BSwanson on 2/7/14.

import android.content.Context;

import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

@Module( library = true )
public class ApplicationModule {
	private final Context mContext;

	public ApplicationModule( Context context ) {
		mContext = context;
	}

	@Provides
	public Context provideContext() {
		return( mContext );
	}

	@Provides
	public EventBus provideEventBus() {
		return( EventBus.getDefault());
	}

	@Provides
	@Singleton
	public IApplicationState provideApplicationState( ApplicationState applicationState ) {
		return( applicationState );
	}
}
