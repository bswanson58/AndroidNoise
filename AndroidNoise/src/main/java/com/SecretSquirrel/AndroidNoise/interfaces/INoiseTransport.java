package com.SecretSquirrel.AndroidNoise.interfaces;

// Created by BSwanson on 2/27/14.

import com.SecretSquirrel.AndroidNoise.dto.ServerTimeSync;
import com.SecretSquirrel.AndroidNoise.dto.TransportState;

import rx.Observable;

public interface INoiseTransport {
	Observable<ServerTimeSync>  SyncServerTime();
	Observable<TransportState>  GetTransportState();
}
