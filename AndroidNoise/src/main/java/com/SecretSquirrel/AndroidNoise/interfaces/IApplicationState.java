package com.SecretSquirrel.AndroidNoise.interfaces;

// Secret Squirrel Software - Created by bswanson on 12/11/13.

import android.content.ServiceConnection;
import com.SecretSquirrel.AndroidNoise.dto.ServerInformation;

import rx.Observable;

public interface IApplicationState {
	Observable<ServerInformation>   locateServers();

	boolean             getIsConnected();
	boolean             canResumeWithCurrentServer();
	ServerInformation   getCurrentServer();
	void                selectServer( ServerInformation server );

	void                registerForEvents( ServiceConnection client );
	void                unregisterFromEvents( ServiceConnection client );
}
