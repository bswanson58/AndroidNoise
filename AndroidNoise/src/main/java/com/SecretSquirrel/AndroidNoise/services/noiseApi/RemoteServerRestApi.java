package com.SecretSquirrel.AndroidNoise.services.noiseApi;

// Secret Squirrel Software - Created by bswanson on 12/11/13.

import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;
import com.SecretSquirrel.AndroidNoise.services.rto.RoServerInformation;
import com.SecretSquirrel.AndroidNoise.services.rto.RoServerVersion;

import retrofit.http.GET;
import retrofit.http.Query;

public interface RemoteServerRestApi {
	@GET( "/Noise/serverVersion" )
	RoServerVersion     GetServerVersion();

	@GET( "/Noise/serverInformation" )
	RoServerInformation GetServerInformation();

	@GET( "/Noise/setOutputDevice" )
	BaseServerResult    SetAudioDevice( @Query( "device" ) int deviceId );

	@GET( "/Noise/requestEvents" )
	BaseServerResult    RequestEvents( @Query( "address" ) String address );

	@GET( "/Noise/revokeEvents" )
	BaseServerResult    RevokeEvents( @Query( "address" ) String address );
}
