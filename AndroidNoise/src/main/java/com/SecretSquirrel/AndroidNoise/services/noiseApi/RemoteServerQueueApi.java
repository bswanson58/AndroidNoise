package com.SecretSquirrel.AndroidNoise.services.noiseApi;

// Secret Squirrel Software - Created by BSwanson on 12/19/13.

import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;
import com.SecretSquirrel.AndroidNoise.services.rto.RoPlayQueueListResult;
import com.SecretSquirrel.AndroidNoise.services.rto.StrategyInformationResult;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Query;

public interface RemoteServerQueueApi {
	@GET( "/Noise/Queue/enqueueTrack" )
	public BaseServerResult EnqueueTrack( @Query( "track" )long trackId );

	@PUT( "/Noise/Queue/EnqueueTrackList" )
	public BaseServerResult EnqueueTrackList( @Body long[] trackList );

	@GET( "/Noise/Queue/enqueueAlbum" )
	public  BaseServerResult    EnqueueAlbum( @Query( "album" )long albumId );

	@GET( "/Noise/Queue/queueList" )
	public RoPlayQueueListResult GetQueuedTrackList();

	@GET( "/Noise/Queue/transportCommand" )
	public BaseServerResult     ExecuteTransportCommand( @Query( "command") int command );

	@GET( "/Noise/Queue/queueCommand" )
	public BaseServerResult     ExecuteQueueCommand( @Query( "command") int command );

	@GET( "/Noise/Queue/queueItemCommand" )
	public BaseServerResult     ExecuteQueueItemCommand( @Query( "command") int command, @Query( "item" ) long itemId );

	@GET( "/Noise/Queue/queueStrategyInformation" )
	public StrategyInformationResult GetQueueStrategyInformation();

	@GET( "/noise/Queue/setQueueStrategy" )
	public BaseServerResult     SetQueueStrategies( @Query( "playStrategy" ) int playStrategyId, @Query( "playParameter" ) long playStrategyParameter,
	                                                @Query( "exhaustedStrategy" ) int exhaustedStrategy, @Query( "exhaustedParameter" ) long exhaustedStrategyParameter );
}
