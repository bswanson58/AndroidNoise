package com.SecretSquirrel.AndroidNoise.interfaces;

// Secret Squirrel Software - Created by BSwanson on 12/17/13.

import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.PlayQueueListResult;
import com.SecretSquirrel.AndroidNoise.dto.QueuedAlbumResult;
import com.SecretSquirrel.AndroidNoise.dto.QueuedTrackResult;
import com.SecretSquirrel.AndroidNoise.dto.StrategyInformation;
import com.SecretSquirrel.AndroidNoise.dto.Track;
import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;

import rx.Observable;
import rx.Subscription;
import rx.util.functions.Action1;

public interface INoiseQueue {
	public enum TransportCommand {
		Play, Stop, Pause, PlayNext, PlayPrevious, Repeat
	}

	public enum QueueCommand {
		StartPlaying, Clear, ClearPlayed
	}

	public enum QueueItemCommand {
		Remove, PlayNext, Replay
	}

	public  Subscription                    EnqueueTrack( final long trackId, Action1<QueuedTrackResult> result );
	public  Observable<QueuedTrackResult>   EnqueueTrack( final long trackId );

	public  Subscription                    EnqueueTrack( Track track, Action1<QueuedTrackResult> result );
	public  Subscription                    EnqueueTrack( Track track, Action1<QueuedTrackResult> result, Action1<Throwable> errorAction );
	public  Observable<QueuedTrackResult>   EnqueueTrack( final Track track );

	public  Subscription                    EnqueueAlbum( Album album, Action1<QueuedAlbumResult> resultAction );
	public  Subscription                    EnqueueAlbum( Album album, Action1<QueuedAlbumResult> result, Action1<Throwable> errorAction );
	public  Observable<QueuedAlbumResult>   EnqueueAlbum( final Album album );

	public  Subscription                    GetQueuedTrackList( Action1<PlayQueueListResult> resultAction, Action1<Throwable> errorAction );
	public  Observable<PlayQueueListResult> GetQueuedTrackList();

	public  Observable<BaseServerResult>    ExecuteTransportCommand( TransportCommand command );
	public  Observable<BaseServerResult>    ExecuteQueueCommand( QueueCommand command );
	public  Observable<BaseServerResult>    ExecuteQueueItemCommand( QueueItemCommand command, long itemId );

	public  Observable<StrategyInformation> GetStrategyInformation();
	public  Observable<BaseServerResult>    SetStrategyInformation( int playStrategyId, long playStrategyParameter,
	                                                                int exhaustedStrategyId, long exhaustedStrategyParameter );
}
