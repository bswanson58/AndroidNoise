package com.SecretSquirrel.AndroidNoise.interfaces;

// Secret Squirrel Software - Created by bswanson on 12/11/13.

import android.content.ServiceConnection;
import com.SecretSquirrel.AndroidNoise.dto.ServerInformation;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;

import rx.Observable;

public interface IApplicationState {
	public  boolean                         getIsConnected();
	public Observable<ServerInformation>    locateServers();
	public  void                            SelectServer( ServerInformation server );

	public  INoiseServer    getNoiseClient();
	public  INoiseData      getDataClient();
	public  INoiseQueue     getQueueClient();
	public  INoiseSearch    getSearchClient();

	public  void            registerForEvents( ServiceConnection client );
	public  void            unregisterFromEvents( ServiceConnection client );
}
