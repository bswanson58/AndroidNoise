package com.SecretSquirrel.AndroidNoise.services.rto;

// Secret Squirrel Software - Created by bswanson on 12/11/13.

import retrofit.http.GET;

public interface RemoteServerRestApi {
	@GET( "/serverVersion" )
	RoServerVersion GetServerVersion();
}
