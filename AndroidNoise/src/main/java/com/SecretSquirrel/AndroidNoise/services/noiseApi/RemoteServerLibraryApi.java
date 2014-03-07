package com.SecretSquirrel.AndroidNoise.services.noiseApi;

// Created by BSwanson on 3/6/14.

import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;
import com.SecretSquirrel.AndroidNoise.services.rto.RoLibrary;
import com.SecretSquirrel.AndroidNoise.services.rto.RoLibraryListResult;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Query;

public interface RemoteServerLibraryApi {
	@GET( "/Noise/Library/libraryList" )
	RoLibraryListResult getLibraries();

	@GET( "/Noise/Library/selectLibrary" )
	BaseServerResult selectLibrary( @Query( "library") long libraryId );

	@GET( "/Noise/Library/syncLibrary" )
	BaseServerResult syncLibrary();

	@POST( "/Noise/Library/createLibrary" )
	RoLibraryListResult createLibrary( @Body RoLibrary library );

	@PUT( "/Noise/Library/updateLibrary")
	BaseServerResult updateLibrary( @Body RoLibrary library );
}
