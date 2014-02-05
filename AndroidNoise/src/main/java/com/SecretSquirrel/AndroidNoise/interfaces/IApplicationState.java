package com.SecretSquirrel.AndroidNoise.interfaces;

// Secret Squirrel Software - Created by bswanson on 12/11/13.

import android.content.ServiceConnection;
import com.SecretSquirrel.AndroidNoise.dto.ServerInformation;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;

import rx.Observable;

public interface IApplicationState {
	void                            pauseOperation();
	boolean                         resumeOperation();

	boolean                         getIsConnected();
	Observable<ServerInformation>   locateServers();
	void                            SelectServer( ServerInformation server );

	INoiseServer    getNoiseClient();
	INoiseData      getDataClient();
	INoiseQueue     getQueueClient();
	INoiseSearch    getSearchClient();
	IRecentData     getRecentData();

	void            registerForEvents( ServiceConnection client );
	void            unregisterFromEvents( ServiceConnection client );
}
