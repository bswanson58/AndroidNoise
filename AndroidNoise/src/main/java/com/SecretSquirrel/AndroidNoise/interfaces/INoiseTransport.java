package com.SecretSquirrel.AndroidNoise.interfaces;

// Created by BSwanson on 2/27/14.

import com.SecretSquirrel.AndroidNoise.dto.AudioState;
import com.SecretSquirrel.AndroidNoise.dto.ServerTimeSync;
import com.SecretSquirrel.AndroidNoise.dto.TransportState;
import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;

import rx.Observable;

public interface INoiseTransport {
	Observable<ServerTimeSync>      syncServerTime();
	Observable<TransportState>      getTransportState();

	Observable<AudioState>          getAudioState();
	Observable<BaseServerResult>    setAudioState( AudioState state );
}
