package com.SecretSquirrel.AndroidNoise.services.noiseApi;

// Created by BSwanson on 2/27/14.

import com.SecretSquirrel.AndroidNoise.services.rto.AudioStateResult;
import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;
import com.SecretSquirrel.AndroidNoise.services.rto.RoAudioState;
import com.SecretSquirrel.AndroidNoise.services.rto.RoTimeSync;
import com.SecretSquirrel.AndroidNoise.services.rto.RoTransportState;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Query;

public interface RemoteServerTransportApi {
	@GET( "/Noise/Transport/timeSync" )
	RoTimeSync          syncServerTime( @Query("client") long clientTime );

	@GET( "/Noise/Transport/getTransportState" )
	RoTransportState    getTransportState();

	@GET( "/Noise/Transport/getAudioState" )
	AudioStateResult    getAudioState();

	@PUT( "/Noise/Transport/setAudioState")
	BaseServerResult    setAudioState( @Body RoAudioState state );
}
