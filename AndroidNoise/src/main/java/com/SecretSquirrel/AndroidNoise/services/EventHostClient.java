package com.SecretSquirrel.AndroidNoise.services;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import javax.inject.Inject;


// Created by BSwanson on 2/9/14.

public class EventHostClient {
	private final Context   mContext;

	@Inject
	public EventHostClient( Context context ) {
		mContext = context;
	}

	public void registerForEvents( ServiceConnection client ) {
		mContext.bindService( new Intent( mContext, EventHostService.class ), client, Context.BIND_AUTO_CREATE );
	}

	public void unregisterFromEvents( ServiceConnection client ) {
		mContext.unbindService( client );
	}

}
