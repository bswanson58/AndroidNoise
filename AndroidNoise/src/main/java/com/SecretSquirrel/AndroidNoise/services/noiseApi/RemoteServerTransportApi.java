package com.SecretSquirrel.AndroidNoise.services.noiseApi;

// Created by BSwanson on 2/27/14.

import com.SecretSquirrel.AndroidNoise.services.rto.RoTimeSync;

import retrofit.http.GET;
import retrofit.http.Query;

public interface RemoteServerTransportApi {
	@GET( "/Noise/Transport/timeSync" )
	RoTimeSync  SyncServerTime( @Query( "client" ) long clientTime );
}
