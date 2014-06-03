package com.SecretSquirrel.AndroidNoise.interfaces;

// Secret Squirrel Software - Created by bswanson on 12/11/13.

import android.os.ResultReceiver;

import com.SecretSquirrel.AndroidNoise.dto.ServerInformation;
import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;

import rx.Observable;

public interface INoiseServer {
	public void                             getServerVersion( ResultReceiver receiver );
	public void                             getServerInformation( ResultReceiver receiver );

	public Observable<BaseServerResult>     setAudioDevice( int deviceId );
	public Observable<BaseServerResult>     requestEvents( String address );
	public Observable<BaseServerResult>     revokeEvents( String address );
}
