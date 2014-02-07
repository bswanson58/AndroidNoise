package com.SecretSquirrel.AndroidNoise.interfaces;

// Secret Squirrel Software - Created by bswanson on 12/11/13.

import android.content.ServiceConnection;
import com.SecretSquirrel.AndroidNoise.dto.ServerInformation;

import rx.Observable;

public interface IApplicationState {
	void                            pauseOperation();
	boolean                         resumeOperation();

	boolean                         getIsConnected();
	ServerInformation               getCurrentServer();
	Observable<ServerInformation>   locateServers();
	void                            SelectServer( ServerInformation server );

	INoiseData      getDataClient();
	IRecentData     getRecentData();

	void            registerForEvents( ServiceConnection client );
	void            unregisterFromEvents( ServiceConnection client );
}
