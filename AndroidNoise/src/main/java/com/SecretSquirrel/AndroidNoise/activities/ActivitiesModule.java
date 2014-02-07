package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 2/7/14.

import com.SecretSquirrel.AndroidNoise.model.ApplicationModule;
import com.SecretSquirrel.AndroidNoise.model.QueueRequestHandler;
import com.SecretSquirrel.AndroidNoise.services.ServicesModule;

import dagger.Module;

@Module(
		includes = {
				ServicesModule.class,
				ApplicationModule.class
		},
		injects = {
				QueueListFragment.class,
				SearchListFragment.class,
				TransportFragment.class,
				QueueRequestHandler.class
		}
)
public class ActivitiesModule {
}
