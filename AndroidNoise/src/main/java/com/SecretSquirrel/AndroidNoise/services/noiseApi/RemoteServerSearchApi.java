package com.SecretSquirrel.AndroidNoise.services.noiseApi;

// Secret Squirrel Software - Created by bswanson on 12/31/13.

import com.SecretSquirrel.AndroidNoise.services.rto.RoSearchResult;

import retrofit.http.GET;
import retrofit.http.Query;

public interface RemoteServerSearchApi {
	@GET( "/Noise/Search/search" )
	RoSearchResult Search( @Query( "text" ) String searchTerms );
}
