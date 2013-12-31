package com.SecretSquirrel.AndroidNoise.services.rto;

// Secret Squirrel Software - Created by bswanson on 12/11/13.

import retrofit.http.GET;
import retrofit.http.Query;

public interface RemoteServerRestApi {
	@GET( "/serverVersion" )
	RoServerVersion     GetServerVersion();

	@GET( "/requestEvents" )
	BaseServerResult    RequestEvents( @Query( "address" ) String address );

	@GET( "/revokeEvents" )
	BaseServerResult    RevokeEvents( @Query( "address" ) String address );
}
