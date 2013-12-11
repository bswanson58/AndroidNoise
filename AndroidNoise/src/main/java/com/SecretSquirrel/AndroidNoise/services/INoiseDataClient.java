package com.SecretSquirrel.AndroidNoise.services;

import android.os.ResultReceiver;

import com.SecretSquirrel.AndroidNoise.services.rto.RoArtist;
import com.SecretSquirrel.AndroidNoise.services.rto.RoArtistListResult;

import retrofit.http.GET;

// Secret Squirrel Software - Created by bswanson on 12/6/13.

public interface INoiseDataClient {
	@GET( "Data/artists" )
	RoArtistListResult  GetArtistList();
}
