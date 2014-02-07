package com.SecretSquirrel.AndroidNoise.model;

// Secret Squirrel Software - Created by bswanson on 2/7/14.

import android.content.Context;

import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.services.ServicesModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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
	@Singleton
	public IApplicationState provideApplicationState() {
		return( new ApplicationState( mContext ));
	}
}
