package com.SecretSquirrel.AndroidNoise.application;

// Secret Squirrel Software - Created by BSwanson on 12/9/13.

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import com.SecretSquirrel.AndroidNoise.dto.ServerInformation;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.services.EventHostService;
import com.SecretSquirrel.AndroidNoise.services.ServiceLocator;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import rx.Observable;

public class ApplicationState implements IApplicationState {
	private final Context                   mContext;
	private ServerInformation               mCurrentServer;
	private boolean                         mIsConnected;

	@Inject
	public ApplicationState( Context context ) {
		mContext = context;
	}

	public boolean getIsConnected() {
		return(( mIsConnected ) &&
			   ( mCurrentServer != null ));
	}

	public ServerInformation getCurrentServer() {
		return( mCurrentServer );
	}

	public void setCurrentServer( ServerInformation server ) {
		mCurrentServer = server;
		mIsConnected = mCurrentServer != null;
	}

	@Override
	public boolean canResumeWithCurrentServer() {
		boolean retValue = true;

		if( getIsConnected()) {
			// we need to determine if the server we were connected to is still available...
		}

		return( retValue );
	}

	public Observable<ServerInformation> locateServers() {
		return( ServiceLocator.createServiceLocator( mContext ));
	}
}
