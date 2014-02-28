package com.SecretSquirrel.AndroidNoise.interfaces;

// Created by BSwanson on 2/27/14.

import com.SecretSquirrel.AndroidNoise.dto.ServerTimeSync;

import rx.Observable;

public interface INoiseTransport {
	Observable<ServerTimeSync>  SyncServerTime();
}
