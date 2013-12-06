package com.SecretSquirrel.AndroidNoise.services;

import com.SecretSquirrel.AndroidNoise.services.rto.RoServerVersion;

import retrofit.http.GET;

/**
 * Created by bswanson on 12/5/13.
 */
public interface INoiseRestClient {
	@GET( "/serverVersion" )
	RoServerVersion GetServerVersion();
}
