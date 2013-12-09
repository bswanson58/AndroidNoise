package com.SecretSquirrel.AndroidNoise.services;

import com.SecretSquirrel.AndroidNoise.services.rto.RoArtist;
import com.SecretSquirrel.AndroidNoise.services.rto.RoServerVersion;

import retrofit.http.GET;

// Secret Squirrel Software - Created by bswanson on 12/6/13.

public interface INoiseRestClient {
	@GET( "/serverVersion" )
	RoServerVersion GetServerVersion();

	@GET( "Data/artists" )
	RoArtist[] GetArtistList();
}
