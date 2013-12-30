package com.SecretSquirrel.AndroidNoise.services.rto;

// Secret Squirrel Software - Created by bswanson on 12/19/13.

import retrofit.http.GET;
import retrofit.http.Query;

public interface RemoteServerQueueApi {
	@GET( "/Queue/enqueueTrack" )
	public  BaseServerResult    EnqueueTrack( @Query( "track" )long trackId );

	@GET( "/Queue/enqueueAlbum" )
	public  BaseServerResult    EnqueueAlbum( @Query( "album" )long albumId );

	@GET( "/Queue/queueList" )
	public  PlayQueueListResult GetQueuedTrackList();
}
