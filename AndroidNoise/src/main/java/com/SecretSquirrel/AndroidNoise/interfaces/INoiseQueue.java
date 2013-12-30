package com.SecretSquirrel.AndroidNoise.interfaces;

// Secret Squirrel Software - Created by bswanson on 12/17/13.

import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.PlayQueueListResult;
import com.SecretSquirrel.AndroidNoise.dto.QueuedAlbumResult;
import com.SecretSquirrel.AndroidNoise.dto.QueuedTrackResult;
import com.SecretSquirrel.AndroidNoise.dto.Track;
import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;

import rx.Observable;
import rx.Subscription;
import rx.util.functions.Action1;

public interface INoiseQueue {
	public enum TransportCommand {
		Play, Stop, Pause, PlayNext, PlayPrevious, Repeat
	}

	public  Subscription                    EnqueueTrack( Track track, Action1<QueuedTrackResult> result );
	public  Subscription                    EnqueueTrack( Track track, Action1<QueuedTrackResult> result, Action1<Throwable> errorAction );
	public  Observable<QueuedTrackResult>   EnqueueTrack( final Track track );

	public  Subscription                    EnqueueAlbum( Album album, Action1<QueuedAlbumResult> resultAction );
	public  Subscription                    EnqueueAlbum( Album album, Action1<QueuedAlbumResult> result, Action1<Throwable> errorAction );
	public  Observable<QueuedAlbumResult>   EnqueueAlbum( final Album album );

	public  Subscription                    GetQueuedTrackList( Action1<PlayQueueListResult> resultAction, Action1<Throwable> errorAction );
	public  Observable<PlayQueueListResult> GetQueuedTrackList();

	public  Observable<BaseServerResult>    ExecuteTransportCommand( TransportCommand command );
}
