package com.SecretSquirrel.AndroidNoise.services.rto;

// Secret Squirrel Software - Created by bswanson on 12/19/13.

import retrofit.http.GET;
import retrofit.http.Query;

public interface RemoteServerQueueApi {
	@GET( "/Noise/Queue/enqueueTrack" )
	public  BaseServerResult    EnqueueTrack( @Query( "track" )long trackId );

	@GET( "/Noise/Queue/enqueueAlbum" )
	public  BaseServerResult    EnqueueAlbum( @Query( "album" )long albumId );

	@GET( "/Noise/Queue/queueList" )
	public RoPlayQueueListResult GetQueuedTrackList();

	@GET( "/Noise/Queue/transportCommand" )
	public BaseServerResult     ExecuteTransportCommand( @Query( "command") int command );
}
